package MySQL;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.*;

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
				json.put("fromUserImg", rs.getString("fromUserImg"));
				json.put("toUserImg", rs.getString("toUserImg"));
				json.put("fromUserName", rs.getString("fromUserName"));
				json.put("toUserName", rs.getString("toUserName"));
				json.put("messageStr",rs.getString("messageStr"));
				json.put("regardingItem",rs.getString("regardingItem"));
				json.put("itemImage",rs.getString("itemImage"));
				json.put("messageDate",rs.getString("messageDate"));
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
}