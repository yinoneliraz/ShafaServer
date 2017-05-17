package MySQL;

import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/** * Simple Java program to connect to MySQL database running on localhost and * 
running SELECT and INSERT query to retrieve and add data. * @author Javin Paul */ 
public class MySQLQueryExecutor { 
	// JDBC URL, username and password of MySQL server
	private static final String url = "jdbc:mysql://shafa1.ce1sh3jg1tvc.eu-west-1.rds.amazonaws.com:3306";
	private static final String user = "root"; 
	private static final String password = "root";
	// JDBC variables for opening and managing connection 
	private static Connection con; 
	private static Statement stmt; 
	static MySQLQueryExecutor instance=null;
	private MySQLQueryExecutor(){
	}
	
	public static MySQLQueryExecutor getInstance(){
		if(instance==null){
			instance=new MySQLQueryExecutor();
		}
		return instance;
	}

	private static Connection getRemoteConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbName = "menagerie";
			String userName = "root";
			String password = "621adova";//:3306
			String hostname = "shafa1.ce1sh3jg1tvc.eu-west-1.rds.amazonaws.com";
			String port = "3306";
			String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
			Connection con = DriverManager.getConnection(jdbcUrl);
			return con;
		}
		catch (Exception e) { System.out.println("Error"); }
		return null;
	}

	public JSONArray getAllRecords() throws Exception{
		ResultSet rs=null;
		String query = "SELECT name, image,userName, size,from, price, lat ,lng, description ,swap "
		+ "( 3959 * acos( cos( radians('%s') ) * cos( radians( lat ) ) * cos( radians( lng ) - radians('%s') ) + sin( radians('%s') ) * sin( radians( lat ) ) ) ) "
		+ "AS distance "
		+ "FROM items HAVING distance < '%s' ORDER BY distance LIMIT 0 , 20";

		JSONArray jsonArr=new JSONArray();
		try { 
			// opening database connection to MySQL server 
			con = getRemoteConnection();// DriverManager.getConnection(url, user, password);
			
			// getting Statement object to execute query 
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeQuery(query); 
            while(rs.next()){
            	JSONObject json=new JSONObject();
            	json.put("name", rs.getString("Name"));
            	json.put("address",rs.getString("Address"));
            	jsonArr.add(json);
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
			con = getRemoteConnection();// DriverManager.getConnection(url, user, password);
			
			// getting Statement object to execute query 
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeQuery(query);

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

				} catch (Exception e) {
					e.printStackTrace();
				}
            	jsonArr.add(json);
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
		int rs=0;
		try { 
			// opening database connection to MySQL server 
			con = DriverManager.getConnection(url, user, password);
			// getting Statement object to execute query 
			stmt = con.createStatement(); 
			// executing SELECT query 
			rs = stmt.executeUpdate(sql); 
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
		return rs;
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