package MySQL;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject; 
/** * Simple Java program to connect to MySQL database running on localhost and * 
running SELECT and INSERT query to retrieve and add data. * @author Javin Paul */ 
public class MySQLQueryExecutor { 
	// JDBC URL, username and password of MySQL server 
	private static final String url = "jdbc:mysql://localhost:3306/menagerie"; 
	private static final String user = "root"; 
	private static final String password = "root"; 
	// JDBC variables for opening and managing connection 
	private static Connection con; 
	private static Statement stmt; 
	String tableName="";
	public MySQLQueryExecutor(String tableName){
		this.tableName=tableName;
	}
	
	public JSONArray getAllRecords() throws JSONException{
		ResultSet rs=null;
		String query = "SELECT name, image,userName, size,from, price, lat ,lng, description ,swap "
		+ "( 3959 * acos( cos( radians('%s') ) * cos( radians( lat ) ) * cos( radians( lng ) - radians('%s') ) + sin( radians('%s') ) * sin( radians( lat ) ) ) ) "
		+ "AS distance "
		+ "FROM items HAVING distance < '%s' ORDER BY distance LIMIT 0 , 20";

		JSONArray jsonArr=new JSONArray();
		try { 
			// opening database connection to MySQL server 
			con = DriverManager.getConnection(url, user, password);
			
			// getting Statement object to execute query 
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeQuery(query); 
            while(rs.next()){
            	JSONObject json=new JSONObject();
            	json.put("name", rs.getString("Name"));
            	json.put("address",rs.getString("Address"));
            	jsonArr.put(json);
            }
            rs.close();
		} 
		catch (SQLException sqlEx) { 
			sqlEx.printStackTrace(); 
		} 
		finally { 
			//close connection ,stmt and resultset here 
			try { 
				con.close();
			} 
			catch(SQLException se) {
				/*can't do anything */
				return jsonArr;
			} 
			try {
				stmt.close(); 
			} 
			catch(SQLException se) { 
				/*can't do anything */ 
			} 
			try { 
				rs.close(); 
			} 
			catch(SQLException se) { 
				/*can't do anything */ 
			} 
		} 
		return jsonArr;
	}
	public JSONArray getItems(String query){
		ResultSet rs=null;
		JSONArray jsonArr=new JSONArray();
		try { 
			// opening database connection to MySQL server 
			con = DriverManager.getConnection(url, user, password);
			
			// getting Statement object to execute query 
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeQuery(query); 
            while(rs.next()){
            	JSONObject json=new JSONObject();
            	try {
            		json.put("name", rs.getString("name"));
            		json.put("image", rs.getString("image"));
            		json.put("size", rs.getString("size"));
            		json.put("description", rs.getString("description"));
            		json.put("userName", rs.getString("userName"));
            		json.put("distance", rs.getString("distance"));
					json.put("swap",rs.getString("swap"));
            		json.put("price", rs.getString("price"));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	jsonArr.put(json);
            }
            rs.close();
		} 
		catch (SQLException sqlEx) { 
			sqlEx.printStackTrace(); 
		} 
		finally { 
			//close connection ,stmt and resultset here 
			try { 
				con.close();
			} 
			catch(SQLException se) {
				/*can't do anything */
				return jsonArr;
			} 
			try {
				stmt.close(); 
			} 
			catch(SQLException se) { 
				/*can't do anything */ 
			} 
			try { 
				rs.close(); 
			} 
			catch(SQLException se) { 
				/*can't do anything */ 
			} 
		} 
		return jsonArr;
	}
	
	public int executeSQL(String sql){
		int count=0;
		ResultSet rs=null;
		try { 
			// opening database connection to MySQL server 
			con = DriverManager.getConnection(url, user, password);
			// getting Statement object to execute query 
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeQuery(sql); 
			while (rs.next()) {
				count = rs.getInt(1); 
			} 
		} 
		catch (SQLException sqlEx) { 
			sqlEx.printStackTrace(); 
		} 
		finally { 
			//close connection ,stmt and resultset here 
			try { 
				con.close();
			} 
			catch(SQLException se) {
				/*can't do anything */
				return -1;
			} 
			try {
				stmt.close(); 
			} 
			catch(SQLException se) { 
				/*can't do anything */ 
			} 
		} 
		return count;
	}
	
	public int countItems() { 
		String query = "select count(*) from markers"; 
		int count=0;
		ResultSet rs=null;
		try { 
			// opening database connection to MySQL server 
			con = DriverManager.getConnection(url, user, password);
			// getting Statement object to execute query 
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeQuery(query); 
			while (rs.next()) {
				count = rs.getInt(1); 
			} 
		} 
		catch (SQLException sqlEx) { 
			sqlEx.printStackTrace(); 
		} 
		finally { 
			//close connection ,stmt and resultset here 
			try { 
				con.close();
			} 
			catch(SQLException se) {
				/*can't do anything */
				return -1;
			} 
			try {
				stmt.close(); 
			} 
			catch(SQLException se) { 
				/*can't do anything */ 
			} 
		} 
		return count;
	} 
}