package Server;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        boolean flag=false;
		for(int i=0;i<sizeArray.size();i++){
			JSONArray temp=(JSONArray)sizeArray.get(i);
			if(temp.size()==0)
				continue;
			flag=true;
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
		if(!flag){
			return "";
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
				"    AND isSold = 0 AND owner_id <> " + params.get("userID") +
				" ORDER BY rand()" +
				" LIMIT " + startingItem + " , " + endingItem + " ;";
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

	public static String getSelectSpecialsQuery(JSONObject params) throws Exception{
		String ret = "SELECT * FROM Shafa.specials where userID='"+ params.get("owner_id")+"';";
		return ret;
	}

	public static String getDeleteItemQuery1(JSONObject params) throws Exception{
		String ret = "INSERT INTO deletedItems select * from items where id = "+params.get("itemID")+";";
		return ret;
	}

	public static String getDeleteItemQuery2(JSONObject params) throws Exception{
		String ret = "DELETE FROM items where id = "+params.get("itemID")+";";
		return ret;
	}


	public static String getDeleteFromMyBagItemQuery1(JSONObject params) throws Exception{
		String ret = "INSERT INTO dislike select * from baskets where itemID = "+params.get("itemID")+" and userID=" + params.get("userID") + ";";
		return ret;
	}

	public static String getDeleteFromMyBagItemQuery2(JSONObject params) throws Exception{
		String ret = "DELETE FROM baskets where itemID = "+params.get("itemID")+" and userID=" + params.get("userID") + ";";
		return ret;
	}

	public static String incBadge(JSONObject params){
		Object temp=params.get("toUserId");
		if(temp==null){
			return "";
		}
		String id=temp.toString();
		String ret = "UPDATE users SET badge = badge + 1 WHERE userID = " + id + ";";
		return ret;
	}

	public static String resetBadge(JSONObject params){
		Object temp=params.get("userId");
		if(temp==null){
			temp=params.get("userID");
		}
		String id=temp.toString();
		String ret = "UPDATE users SET badge = 0 WHERE userID = " + id + ";";
		return ret;
	}

	public static String getBadge(JSONObject params){
		Object temp=params.get("toUserId");
		if(temp==null){
			return "";
		}
		String id=temp.toString();
		String ret = "select badge from users WHERE userID = " + id + ";";
		return ret;
	}

    public static JSONObject parseQuery(String query) throws Exception {
    	JSONObject ret = null;
		JSONParser parser1 = new JSONParser();
		return (JSONObject)parser1.parse(query);
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

	public static String getUpdateItemToSell(JSONObject params,int index){
		if(index==1)
			return "UPDATE `Shafa`.`items` SET `isSold`='1' WHERE `id`='"+params.get("itemID")+"';";
		else
			return "INSERT INTO `Shafa`.`sold` (`itemID`, `soldToID`) VALUES ('"+params.get("itemID")+"', '"+params.get("userID")+"');\n";
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
				"    items ON items.id=baskets.itemID where baskets.userID = "+userID+" order by baskets.likeID desc;";
		return ret;
    }

	public static String getItemsGetQuery(JSONObject params) throws Exception{
		String userId=String.valueOf(params.get("userID"));

		String ret="SELECT DISTINCT * FROM Shafa.items " +
				"WHERE owner_id = " + userId + " order by id desc;";
		return ret;
	}

	public static String getInsertMessageQuery(JSONObject params) throws  Exception{
		String ret="INSERT INTO `Shafa`.`messages`(`fromUserId`,`toUserId`," +
				"`fromUserName`,`toUserName`,`messageStr`,`regardingItem`," +
				" `messageDate`)VALUES('"+params.get("fromUserId")+"','" +
				params.get("toUserId")+"','"+params.get("fromUserName")+
				"','"+params.get("toUserName")+"','"+params.get("messageStr")+"','"
				+params.get("regardingItem")+ "', '"+params.get("messageDate")+"');";
		return ret;
	}

	public static String getSelectMessagesQuery(JSONObject params) throws  Exception{
		String startingMessage=params.get("fromMessageId")==null ? "0" : params.get("fromMessageId").toString();
		String ret=	"SELECT `messages`.`messageId`, `messages`.`fromUserId`, `messages`.`toUserId`, " +
						"`messages`.`fromUserName`, " +
						"`messages`.`toUserName`, `messages`.`messageStr`, `messages`.`regardingItem` " +
						", `messages`.`messageDate`, `items`.`name` " +
					"FROM `Shafa`.`messages` INNER JOIN `items` ON `items`.`id`=`messages`.`regardingItem` " +
					"WHERE (`messages`.`toUserId`='"+params.get("userId")+"' OR " +
					"`messages`.`fromUserId`='"+params.get("userId")+"') AND" +
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

	public static String getInserUserQuery(JSONObject params) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String userName=params.get("userName").toString();
		if(userName==null)
			userName="";
		String ret="INSERT INTO `Shafa`.`users` (`userID`, `joinDate`,`fireBaseToken`, `userName`,`os`) VALUES ("
				+params.get("userID")+", '"+dateFormat.format(date)+"','"+params.get("fireBaseToken")+"','"+userName+"' ,'"+params.get("os")+"' );";
		return ret;
	}

	public static String getEditItemQuery(JSONObject params) {
		String ret="UPDATE `Shafa`.`items` " +
				"SET `name` = '"+params.get("name")+"', " +
				"`size` = '"+params.get("size")+"', " +
				"`price` = '"+params.get("price")+"', " +
				"`description` = '"+params.get("description")+"', " +
				"`image` = '"+params.get("image")+"', " +
				"`itemType` = '"+params.get("itemType")+"', " +
				"`isSold` = '0' " +
				"WHERE `id` = "+params.get("itemID")+";";
		return ret;

	}

	public static String getUpdateUserQuery(JSONObject params) {
		String ret = "UPDATE `Shafa`.`users` SET `os`='"+params.get("os")+"' ,`fireBaseToken` = '"+params.get("fireBaseToken")+"' WHERE `userID` = '"+params.get("userID")+"';";
		return ret;
	}

	public static String getOS(JSONObject params) {
		String ret = "select os from users where userID='"+params.get("toUserId")+"';";
		return ret;
	}

	public static String getRelevantUsersQuery(JSONObject params) {
		return "select distinct fromUserID, fromUserName from messages where regardingItem="+ params.get("itemID")+" and toUserId="+params.get("userID")+";";
	}
}


