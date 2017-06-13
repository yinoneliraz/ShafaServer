package Server;
import java.net.URLDecoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Constants {
	public static final String USER_AGENT="Shafa";

	public static String getSelectQuery(JSONObject params) throws Exception{
		JSONParser parser = new JSONParser();
		String lat=String.valueOf(params.get("lat"));
		String lng=String.valueOf(params.get("lng"));
		String topPrice=String.valueOf(params.get("topPrice"));
		String bottomPrice=String.valueOf(params.get("bottomPrice"));
		String unionQuery="";
		String[] titles={"jeans", "coats", "swimsuits", "overalls", "shirts", "pants", "dresses", "skirts", "shoes"};
		JSONArray sizeArray=new JSONArray();
		for(String title:titles){
			sizeArray.add(parser.parse(String.valueOf(params.get(title))));
		}
		boolean showAccessories=Boolean.valueOf(String.valueOf(params.get("accessories")));
		if(showAccessories){
            unionQuery="SELECT  "+
                    "    `items`.*, "+
                    "    (6371 * ACOS(COS(RADIANS('"+lat+"')) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS('"+lng+"')) + SIN(RADIANS('"+lat+"')) * SIN(RADIANS(lat)))) AS distance "+
                    "FROM "+
                    "    items "+
                    "WHERE "+
                    "    `items`.`itemType` = 'accessories' ";
            unionQuery+="UNION ALL ";
        }
		for(int i=0;i<sizeArray.size();i++){
			JSONArray temp=(JSONArray)sizeArray.get(i);
			if(temp.size()==0)
				continue;
			String sizeStr="(";
			for(Object part:temp){
				sizeStr+="\""+String.valueOf(part)+"\",";
			}
			sizeStr=sizeStr.substring(0,sizeStr.length()-1)+")";
			unionQuery+="SELECT  "+
					"    `items`.*, "+
					"    (6371 * ACOS(COS(RADIANS('"+lat+"')) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS('"+lng+"')) + SIN(RADIANS('"+lat+"')) * SIN(RADIANS(lat)))) AS distance "+
					"FROM "+
					"    items "+
					"WHERE "+
					"    `items`.`itemType` = '"+titles[i]+"' ";
			if(!titles[i].equals("accessories"))
				unionQuery+="        AND `items`.size IN "+ sizeStr+"  ";
			if(i<sizeArray.size()-1){
				unionQuery+="UNION ALL ";
			}
		}
		if(unionQuery.endsWith("UNION ALL ")){
			unionQuery=unionQuery.substring(0,unionQuery.lastIndexOf("UNION ALL "));
		}
		String radius=String.valueOf(params.get("radius"));
		String userID=String.valueOf(params.get("userID"));
		String page=String.valueOf(params.get("page"));
		int startingItem=Integer.valueOf(page)*10;
		int endingItem=startingItem+10;
		String ret="SELECT *" +
				" FROM ("+unionQuery+
				")t2        LEFT JOIN"+
				"    ((SELECT "+
				"        `dislike`.`itemID` dItemID, `dislike`.`userID` dUserID"+
				"    FROM"+
				"        dislike"+
				"    WHERE"+
				"        `dislike`.`userID` = '" + userID + "') UNION ALL (SELECT "+
				"        `baskets`.`itemID` bItemID, `baskets`.`userID` bUserID"+
				"    FROM"+
				"        baskets"+
				"    WHERE"+
				"        `baskets`.`userID` = '" + userID + "')) t1 ON dItemID = `id`"+
				" WHERE"+
				"    dItemID IS NULL"+
				" HAVING distance < '" + radius + "' AND price <= " + topPrice + " AND price>=" + bottomPrice +
				"    AND isSold = 0"+
				" ORDER BY distance" +
				" LIMIT " + startingItem + " , " + endingItem + " ;";
		System.out.println(ret.replace("\t"," ") + "\n");
		return ret.replace("\t"," ");
	}
	
	public static String getInsertQuery(JSONObject params) throws Exception{
		String ret = "INSERT INTO `items`(`name`,`owner_id`,`size`,`price`,`description`,`lat`,`lng`," +
				"`image`,`from`,`userName`,`itemType`) "
				+"VALUES ('"+ params.get("name") +"', '"+ params.get("owner_id") +"', "
				+ "'"+ params.get("size").toString().replace("[","").replace("]","")
				+"', '"+ params.get("price") +"', '"+ params.get("description") +"', "
				+"'"+ params.get("lat") +"', '"+ params.get("lng") +"', '"+ params.get("image") +"', "
				+ "'"+params.get("from") +"', '"+ params.get("userName")+"', '"+
				params.get("itemType")+"');";
		return ret;
	}
	
    public static JSONObject parseQuery(String query) throws Exception {
    	JSONObject ret = null;
		JSONParser parser1 = new JSONParser();
		return (JSONObject)parser1.parse(query);
//    	if (query != null) {
//    		String pairs[] = query.split("[&]");
//    		for (String pair : pairs) {
//    			String param[] = pair.split("[=]");
//    			String key = null;
//    			if (param.length > 0) {
//    				key = URLDecoder.decode(param[0],
//    					System.getProperty("file.encoding"));
//    			}
//
//    			if(key!= null){
//					JSONParser parser = new JSONParser();
//					Object obj = parser.parse(key);
//    				ret=(JSONObject) obj;
//    				return ret;
//    			}
//    		}
//    	}
//		return ret;
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

	public static String getUpdateItemToSell(JSONObject params){
		String ret1="",ret2="";
		ret1 = "UPDATE `Shafa`.`items` SET `isSold`='1' WHERE `id`='"+params.get("itemID")+"';";
		ret2 = "INSERT INTO `Shafa`.`sold` (`itemID`, `soldToID`) VALUES ("+params.get("itemID")+", "+params.get("userID")+");\n";
		return ret1+"\n"+ret2;
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
		String lat=String.valueOf(params.get("lat"));
		String lng=String.valueOf(params.get("lng"));

		String ret="SELECT DISTINCT " +
				"    baskets.userID, " +
				"    items.*, " +
				"    (6371 * ACOS(COS(RADIANS('"+lat+"')) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS('"+lng+"')) + SIN(RADIANS('"+lat+"')) * SIN(RADIANS(lat)))) AS distance "+
				"FROM " +
				"    Shafa.baskets " +
				"        INNER JOIN " +
				"    items ON baskets.userID = "+userID+"; ";
		return ret;
    }

	public static String getItemsGetQuery(JSONObject params) throws Exception{
		String userId=String.valueOf(params.get("userID"));

		String ret="SELECT DISTINCT * FROM Shafa.items " +
				"WHERE owner_id = " + userId + " ;";
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


