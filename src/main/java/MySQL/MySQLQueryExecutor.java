package MySQL;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.*;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/** * Simple Java program to connect to MySQL database running on localhost and * 
running SELECT and INSERT query to retrieve and add data. * @author Javin Paul */ 
public class MySQLQueryExecutor { 
	// JDBC URL, username and password of MySQL server
	// JDBC variables for opening and managing connection 
	private static Connection con; 
	private static Statement stmt; 
	static MySQLQueryExecutor instance=null;
	private MySQLQueryExecutor(){
		;
	}
	
	public static MySQLQueryExecutor getInstance(){
		if(instance==null){
			instance=new MySQLQueryExecutor();
		}
		return instance;
	}

	private static Connection getRemoteConnection() {
		try {
			return ConnectionManager.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean closeConnection(){
		//close connection ,stmt and resultset here
		try {
			con.close();
			return true;
		}
		catch(SQLException se) {
			return false;
		}
	}

	private boolean cleanUp(ResultSet rs, Statement stmt){
		try {
			stmt.close();
		}
		catch(SQLException se) {
			return false;
		}
		try {
			if(rs!=null)
				rs.close();
			return true;
		}
		catch(SQLException se) {
			return false;
		}

	}

	public JSONArray getMessages(String query){
		con = getRemoteConnection();
		ResultSet rs;
		JSONArray jsonArr=new JSONArray();
		try {
			// opening database connection to MySQL server
			// getting Statement object to execute query
			stmt = con.createStatement();
			// executing SELECT query
			rs = stmt.executeQuery(query);
			while(rs.next()){
				JSONObject json=new JSONObject();
				json.put("messageId", rs.getString("messageId"));
				json.put("fromUserId", rs.getString("fromUserId"));
				json.put("toUserId", rs.getString("toUserId"));
				json.put("fromUserName", rs.getString("fromUserName"));
				json.put("toUserName", rs.getString("toUserName"));
				json.put("messageStr",rs.getString("messageStr"));
				json.put("regardingItem",rs.getString("regardingItem"));
				json.put("messageDate",rs.getString("messageDate"));
				json.put("itemName",rs.getString("name"));
				jsonArr.add(json);
			}
			rs.close();
		}
		catch (SQLException sqlEx) {
			System.out.println(sqlEx.toString());
			return null;
		}
		finally {
			cleanUp(null,stmt);
			closeConnection();
		}
		return jsonArr;
	}

	public JSONObject getItem(String query){
		con = getRemoteConnection();
		ResultSet rs=null;
		JSONObject json=new JSONObject();

		try {
			// getting Statement object to execute query
			System.out.println("Got single item request\nExecuting");
			stmt = con.createStatement();
			// executing SELECT query
			rs = stmt.executeQuery(query);
			System.out.println("Got results\nParsing");

			System.out.println("Finished parsing\nCreating JSON");

			if(!rs.next())
				return null;
			try {
				json.put("id", rs.getString("id"));
				json.put("name", rs.getString("name"));
				json.put("image", rs.getString("image"));
				json.put("size", rs.getString("size"));
				json.put("description", rs.getString("description"));
				json.put("userName", rs.getString("userName"));
				try{
					json.put("distance", rs.getString("distance"));
				}
				catch(java.sql.SQLException ex){

				}
				json.put("swap",rs.getString("swap"));
				json.put("price", rs.getString("price"));
				json.put("from", rs.getString("from"));
				json.put("itemType", rs.getString("itemType"));
				System.out.println("JSON is ready");
			} catch (Exception e) {
				System.out.println("Failure getting single item:");
				e.printStackTrace();
			}
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure getting single item:");
			sqlEx.printStackTrace();
			return null;
		}
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		return json;
	}

	public String getFireBaseToken(String fbID){
		con = getRemoteConnection();
		ResultSet rs=null;
		String res="";
		try {
			System.out.println("Got FireBae token request\nExecuting");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select fireBaseToken from users where userID="+fbID+";");
			System.out.println("Got results\nParsing");
			while(rs.next()){
				res=rs.getString("fireBaseToken");
			}
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure getting items:");
			sqlEx.printStackTrace();
		}
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		System.out.println("Finished parsing");
		return res;
	}

	public JSONObject getSpeciealUsers(String query){
		con = getRemoteConnection();
		ResultSet rs=null;
		JSONObject json=null;
		try {
			// getting Statement object to execute query
			System.out.println("Got items request\nExecuting");
			stmt = con.createStatement();
			// executing SELECT query
			rs = stmt.executeQuery(query);
			System.out.println("Got results\nParsing");

			while(rs.next()){
				json=new JSONObject();
				try {
					json.put("lat", rs.getString("lat"));
					json.put("lng", rs.getString("lng"));

				} catch (Exception e) {
					System.out.println("Failure getting items:");
					e.printStackTrace();
				}
			}
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure getting items:");
			sqlEx.printStackTrace();
		}
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		System.out.println("Finished parsing");
		return json;
	}

	public JSONArray getItems(String query){
		con = getRemoteConnection();
		ResultSet rs=null;
		JSONArray jsonArr=new JSONArray();
		try { 
			// getting Statement object to execute query
			System.out.println("Got items request\nExecuting");
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeQuery(query);
			System.out.println("Got results\nParsing");

            while(rs.next()){
            	JSONObject json=new JSONObject();
            	try {
            		json.put("id", rs.getString("id"));
            		json.put("name", rs.getString("name"));
            		json.put("image", rs.getString("image"));
            		json.put("size", rs.getString("size"));
            		json.put("description", rs.getString("description"));
            		json.put("userName", rs.getString("userName"));
            		try{
						json.put("distance", rs.getString("distance"));
					}
					catch(java.sql.SQLException ex){

					}
					json.put("swap",rs.getString("swap"));
            		json.put("price", rs.getString("price"));
					json.put("from", rs.getString("from"));
					json.put("owner_id", rs.getString("owner_id"));
					json.put("isSold", rs.getString("isSold"));
					json.put("itemType", rs.getString("itemType"));
					jsonArr.add(json);

				} catch (Exception e) {
					System.out.println("Failure getting items:");
					e.printStackTrace();
				}
            }
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure getting items:");
			sqlEx.printStackTrace();
		} 
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		System.out.println("Finished parsing");
		return jsonArr;
	}
	
	public int executeSQL(String sql){
		int rs=0;
		con = getRemoteConnection();
		try {
			// getting Statement object to execute query
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeUpdate(sql);
		} 
		catch (SQLException sqlEx) {
			System.out.println(sqlEx.toString());
		}
		finally {
			cleanUp(null,stmt);
			closeConnection();
		} 
		return rs;
	}

	public int getMessageCount(String query) {
		con = getRemoteConnection();
		ResultSet rs=null;
		int ret=-1;
		try {
			// getting Statement object to execute query
			System.out.println("Got count messages request\nExecuting");
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			System.out.println("Got results\nParsing");

			System.out.println("Finished parsing\nCreating JSON");

			if(!rs.next())
				return -1;
			try {
				ret=rs.getInt("Count");
			} catch (Exception e) {
				System.out.println("Failure count messages:");
				e.printStackTrace();
			}
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure count messages:");
			sqlEx.printStackTrace();
			return -1;
		}
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		return ret;
	}

	public int getUserBadge(String query) {
		con = getRemoteConnection();
		ResultSet rs=null;
		int ret=-1;
		try {
			// getting Statement object to execute query
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(!rs.next())
				return -1;
			try {
				ret=rs.getInt("badge");
			} catch (Exception e) {
				System.out.println("Failure get get:");
				e.printStackTrace();
			}
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure get get:");
			sqlEx.printStackTrace();
			return -1;
		}
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		return ret;
	}

	public String getOS(String query) {
		con = getRemoteConnection();
		ResultSet rs=null;
		String ret="";
		try {
			// getting Statement object to execute query
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(!rs.next())
				return "";
			try {
				ret=rs.getString("os");
			} catch (Exception e) {
				System.out.println("Failure get get:");
				e.printStackTrace();
			}
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure get get:");
			sqlEx.printStackTrace();
			return "";
		}
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		return ret;
	}

	public JSONArray getRelevantUsers(String query) {
		con = getRemoteConnection();
		ResultSet rs=null;
		JSONArray jsonArr=new JSONArray();
		try {
			// getting Statement object to execute query
			System.out.println("Got items request\nExecuting");
			stmt = con.createStatement();
			// executing SELECT query
			rs = stmt.executeQuery(query);
			System.out.println("Got results\nParsing");

			while(rs.next()){
				JSONObject json=new JSONObject();
				try {
					json.put("userID", rs.getString("fromUserID"));
					json.put("userName", rs.getString("fromUserName"));
					jsonArr.add(json);

				} catch (Exception e) {
					System.out.println("Failure getting items:");
					e.printStackTrace();
				}
			}
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure getting items:");
			sqlEx.printStackTrace();
		}
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		System.out.println("Finished parsing");
		return jsonArr;
	}

	public JSONArray enumItems(String query) {
		con = getRemoteConnection();
		ResultSet rs=null;
		JSONArray jsonArr=new JSONArray();
		try {
			// getting Statement object to execute query
			System.out.println("Got items request\nExecuting");
			stmt = con.createStatement();
			// executing SELECT query
			rs = stmt.executeQuery(query);
			System.out.println("Got results\nParsing");
			jsonArr=convert(rs);
		}
		catch (SQLException sqlEx) {
			System.out.println("Failure getting items:");
			sqlEx.printStackTrace();
		}
		finally {
			cleanUp(rs,stmt);
			closeConnection();
		}
		System.out.println("Finished parsing");
		return jsonArr;
	}

	public static JSONArray convert( ResultSet rs ) throws SQLException, JSONException
	{
		JSONArray json = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();

		while(rs.next()) {
			int numColumns = rsmd.getColumnCount();
			JSONObject obj = new JSONObject();

			for (int i=1; i<numColumns+1; i++) {
				String column_name = rsmd.getColumnName(i);

				if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
					obj.put(column_name, rs.getArray(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
					obj.put(column_name, rs.getInt(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
					obj.put(column_name, rs.getBoolean(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
					obj.put(column_name, rs.getBlob(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
					obj.put(column_name, rs.getDouble(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
					obj.put(column_name, rs.getFloat(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
					obj.put(column_name, rs.getInt(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
					obj.put(column_name, rs.getString(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
					obj.put(column_name, rs.getInt(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
					obj.put(column_name, rs.getInt(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
					obj.put(column_name, rs.getDate(column_name));
				}
				else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
					obj.put(column_name, rs.getTimestamp(column_name));
				}
				else{
					obj.put(column_name, rs.getObject(column_name));
				}
			}
			json.add(obj);
		}

		return json;
	}

}