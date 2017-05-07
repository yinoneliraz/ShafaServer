package Server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Constants {
	public static final int port=4000;
	public static final String USER_AGENT="Shafa";
	public static final String INSERT_FORMAT_SQL="INSERT INTO items (name,address"
										+ ",owner_id,category,size,price,description,lat,lng)"
										+ "VALUES (`%1$s`,`%2$s`,`%3$d`,`%4$s`,"
										+ "`%5$s`,`%6$f`,`%7$s`,`%8$f`,`%9$f`);";
	public static final String INSERT_FORMAT_POST="name=%1$s&address=%2$s&owner_id=%3$d"
								+ "&category=%4$s&size=%5$s&price=%6$f&description=%7$s&"
								+ "lat=%8$f&lng=%9$f";


	public static String getSelectQuery(JSONObject params) throws JSONException{
		final String[] sizes={"XS","S","M","L","XL","XXL","XXXL"};
		if (params==null){
			return "";
		}
		String lat=params.getString("lat");
		String lng=params.getString("lng");
		String radius=params.getString("radius");
		String price=params.getString("price");
		String[] sizeArr=params.getString("shirtsize").split(",");
		int size=params.getInt("pantssize");
		sizeArr[0]=sizeArr[0].replace("[","");
		sizeArr[0]=sizeArr[sizeArr.length-1].replace("]","");
		String group="(";
		for(int i=0;i<7;i++){
			if(Boolean.parseBoolean(sizeArr[i]))
				group+="\"" + sizes[i] + "\",";
		}
		group=group.substring(0,group.length()-1)+")";
		String ret= "SELECT `id`, `name`, `image`,`userName`, `size`, `price`, `lat` ,`lng`, `description` ,`swap`,`from`, "
				+"( 6371 * acos( cos( radians('"+lat+"') ) * cos( radians( lat ) ) * cos( radians( lng ) - "
				+"radians('"+lng+"') ) + sin( radians('"+lat+"') ) * sin( radians( lat ) ) ) ) "
				+"AS distance FROM items HAVING distance < '"+radius+"' AND price <= "+price+" AND ( (size >= "+ (size- 2) + " AND" +
				" size <= " + (size+2) + ") OR" +
				" size IN "+ group +" ) ORDER BY distance LIMIT 0 , 20 ;";
		System.out.println(ret + "\n");
		return ret;
	}
	
	public static String getInsertQuery(JSONObject params) throws JSONException{
		String ret = "INSERT INTO `items`(`name`, `owner_id`, `category`, `size`, `price`, `description`, "
				+"`lat`, `lng`,	`image`, `swap`, `from`, `userName`)"
				+"VALUES ('"+ params.getString("name") +"', '"+ params.getString("owner_id") +"', '"+ params.getString("category") +"', "
				+ "'"+ params.getString("size") +"', '"+ params.getString("price") +"', '"+ params.getString("description") +"', "
				+"'"+ params.getString("lat") +"', '"+ params.getString("lng") +"', '"+ params.getString("images") +"', "
						+ "'"+ params.getString("swap") +"', '"+params.getString("from") +"', '"+ params.getString("userName")+"');";
		return ret;
	}
	
    public static JSONObject parseQuery(String query) throws UnsupportedEncodingException, JSONException {
    	JSONObject ret = null;
    	if (query != null) {
    		String pairs[] = query.split("[&]");
    		for (String pair : pairs) {
    			String param[] = pair.split("[=]");
    			String key = null;
    			if (param.length > 0) {
    				key = URLDecoder.decode(param[0], 
    					System.getProperty("file.encoding"));
    			}
    			
    			if(key!= null){
    				ret=new JSONObject(key);
    				return ret;
    			}
    		}
    	}
		return ret;
    }
    
    public static String getBasketInsertQuery(JSONObject params){
    	String ret="";
		try {
			ret = "INSERT INTO `menagerie`.`baskets` (`userID`, `itemID`) VALUES ("+
					params.getString("userID")+", "+params.getString("itemID")+");";
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return ret;
    }
    
    public static String getBasketGetQuery(JSONObject params) throws JSONException{
		String lat=params.getString("lat");
		String lng=params.getString("lng");
		String radius=params.getString("radius");

		String ret="SELECT DISTINCT baskets.userId, baskets.itemID,items.id, items.name, items.image,items.userName," +
				" items.size, items.price, items.lat ,items.lng, " +
				"items.description ,items.swap,items.from, "
				+"( 6371 * acos( cos( radians('"+lat+"') ) * cos( radians( lat ) ) * cos( radians( lng ) - "
				+"radians('"+lng+"') ) + sin( radians('"+lat+"') ) * sin( radians( lat ) ) ) ) "
				+"AS distance FROM baskets, items " +
				"WHERE items.id = baskets.itemID" +
				" HAVING distance < '"+radius+"' ORDER BY distance LIMIT 0 , 20 ;";
    	return ret;
    }

	public static String getItemsGetQuery(JSONObject params) throws JSONException{
		String userId=params.getString("userID");

		String ret="SELECT DISTINCT baskets.userId, items.owner_id , items.id, items.name, items.image,items.userName," +
				" items.size, items.price, items.lat ,items.lng, " +
				"items.description ,items.swap,items.from FROM baskets, items " +
				"WHERE items.owner_id = " + userId + " ;";
		System.out.println(ret + "\n");
		return ret;
	}
}


