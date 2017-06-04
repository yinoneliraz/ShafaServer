package Server;
import java.net.URLDecoder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Constants {
	public static final int port=4000;
	public static final String USER_AGENT="Shafa";

	public static String getSelectQuery(JSONObject params) throws Exception{
		final String[] sizes={"XS","S","M","L","XL","XXL","XXXL"};
		if (params==null){
			return "";
		}
		String lat=String.valueOf(params.get("lat"));
		String lng=String.valueOf(params.get("lng"));
		String radius=String.valueOf(params.get("radius"));
		String price=String.valueOf(params.get("price"));
		String[] sizeArr=params.get("shirtsize").toString().split(",");
		int size=Integer.parseInt(params.get("pantssize").toString());
		sizeArr[0]=sizeArr[0].replace("[","");
		sizeArr[0]=sizeArr[sizeArr.length-1].replace("]","");
		String group="(";
		for(int i=0;i<7;i++){
			if(Boolean.parseBoolean(sizeArr[i]))
				group+="\"" + sizes[i] + "\",";
		}
		String type="(";
		String[] types=params.get("type").toString().split(",");
		for(int i=0;i<types.length;i++){
			type+="\"" + types[i].trim() + "\",";
		}
		type=type.substring(0,type.length()-1);
		type+=")";
		group=group.substring(0,group.length()-1)+")";
		String ret= "SELECT `id`, `name`, `image`,`userName`, `size`, `price`, `lat` ,`lng`, `description` ,`swap`,`from`,`itemType`, "
				+"( 6371 * acos( cos( radians('"+lat+"') ) * cos( radians( lat ) ) * cos( radians( lng ) - "
				+"radians('"+lng+"') ) + sin( radians('"+lat+"') ) * sin( radians( lat ) ) ) ) "
				+"AS distance FROM items HAVING distance < '"+radius+"' AND price <= "+price+" AND ( (size >= "+ (size- 2) + " AND" +
				" size <= " + (size+2) + ") OR" +
				" size IN "+ group +" ) AND  `itemType` IN " + type + " ORDER BY distance LIMIT 0 , 20 ;";
		System.out.println(ret + "\n");
		return ret;
	}
	
	public static String getInsertQuery(JSONObject params) throws Exception{
		String ret = "INSERT INTO `items`(`name`, `owner_id`, `category`, `size`, `price`, `description`, "
				+"`lat`, `lng`,	`image`, `swap`, `from`, `userName`)"
				+"VALUES ('"+ params.get("name") +"', '"+ params.get("owner_id") +"', '"+ params.get("category") +"', "
				+ "'"+ params.get("size") +"', '"+ params.get("price") +"', '"+ params.get("description") +"', "
				+"'"+ params.get("lat") +"', '"+ params.get("lng") +"', '"+ params.get("images") +"', "
				+ "'"+ params.get("swap") +"', '"+params.get("from") +"', '"+ params.get("userName")+"');";
		return ret;
	}
	
    public static JSONObject parseQuery(String query) throws Exception {
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
					JSONParser parser = new JSONParser();
					Object obj = parser.parse(key);
    				ret=(JSONObject) obj;
    				return ret;
    			}
    		}
    	}
		return ret;
    }
    
    public static String getBasketInsertQuery(JSONObject params){
    	String ret="";
		try {
			ret = "INSERT INTO `Shafa`.`baskets` (`userID`, `itemID`) VALUES ("+
					params.get("userID")+", "+params.get("itemID")+");";
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return ret;
    }

	public static String getDislikeInsertQuery(JSONObject params){
		String ret="";
		try {
			ret = "INSERT INTO `Shafa`.`dislike` (`userID`, `itemID`) VALUES ("+
					params.get("userID")+", "+params.get("itemID")+");";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

    public static String getBasketGetQuery(JSONObject params) throws Exception{
		String userID=String.valueOf(params.get("userID"));

		String ret="SELECT distinct baskets.userID, items.* FROM Shafa.baskets inner join items on baskets.userID="+userID+";";
    	return ret;
    }

	public static String getItemsGetQuery(JSONObject params) throws Exception{
		String userId=String.valueOf(params.get("userID"));

		String ret="SELECT DISTINCT baskets.userId, items.owner_id , items.id, items.name, items.image,items.userName," +
				" items.size, items.price, items.lat ,items.lng, " +
				"items.description ,items.swap,items.from FROM baskets, items " +
				"WHERE items.owner_id = " + userId + " ;";
		System.out.println(ret + "\n");
		return ret;
	}

	public static String getInsertMessageQuery(JSONObject params) throws  Exception{
		String ret="INSERT INTO `Shafa`.`messages`(`fromUserId`,`toUserId`,`fromUserImg`,`toUserImg`," +
				"`fromUserName`,`toUserName`,`messageStr`,`regardingItem`,`itemImage`, `messageDate`)VALUES('"+params.get("fromUserId")+"','" +
				params.get("toUserId")+"','"+params.get("fromUserImg")+"','"+params.get("toUserImg")+"','"+params.get("fromUserName")+
				"','"+params.get("toUserName")+"','"+params.get("messageStr")+"','"+params.get("regardingItem")+
				"','"+params.get("itemImage")+"', '"+params.get("messageDate")+"');";
		return ret;
	}

	public static String getSelectMessagesQuery(JSONObject params) throws  Exception{
		String startingMessage=params.get("fromMessageId")==null ? "0" : params.get("fromMessageId").toString();
		String ret=	"SELECT `messages`.`messageId`, `messages`.`fromUserId`, `messages`.`toUserId`, " +
						"`messages`.`fromUserImg`, `messages`.`toUserImg`, `messages`.`fromUserName`, " +
						"`messages`.`toUserName`, `messages`.`messageStr`, `messages`.`regardingItem` " +
						", `messages`.`itemImage`, `messages`.`messageDate` " +
					"FROM `Shafa`.`messages` " +
					"WHERE `messages`.`toUserId`='"+params.get("userId")+"' OR " +
					"`messages`.`fromUserId`='"+params.get("userId")+"' AND" +
					" `messages`.`messageId` > " + startingMessage +
					" ORDER BY `messages`.`messageId`;";
		return ret;
	}

	public static String getSingleItemQuery(JSONObject params) {
    	String ret="SELECT `items`.`id`, `items`.`name`, `items`.`owner_id`, " +
						"`items`.`category`, `items`.`size`, `items`.`price`, " +
						"`items`.`description`, `items`.`lat`, `items`.`lng`, " +
						"`items`.`image`, `items`.`swap`, `items`.`from`, " +
						"`items`.`userName`FROM `Shafa`.`items`WHERE `items`.`id`="+params.get("id")+";";
    	return ret;
	}

	public static String getMessageCountQuery(JSONObject params) {
		String startingMessage=params.get("fromMessageId")==null ? "0" : params.get("fromMessageId").toString();
		String ret=	"SELECT COUNT(*) AS Count" +
				"FROM `Shafa`.`messages` " +
				"WHERE `messages`.`toUserId`='"+params.get("userId")+"' OR " +
				"`messages`.`fromUserId`='"+params.get("userId")+"' AND" +
				" `messages`.`messageId` > " + startingMessage +
				" ORDER BY `messages`.`messageId`;";
		return ret;
	}
}


