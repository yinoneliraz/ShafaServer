package SendData;

import Server.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;



public class Post {
	public static void main(String[] args) throws Exception{
		sendPost();
	}
	
    private static void sendPost() throws Exception {
	    String url = "https://bgu4u.bgu.ac.il/pls/scwp/!fw.CheckId";
	    URL obj = new URL(url);
	    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	
	    //add reuqest header
	    con.setRequestMethod("POST");
	    con.setRequestProperty("User-Agent", Constants.USER_AGENT);
	    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	
	    String urlParameters = String.format("oc_username=%1$s&oc_password=%2$s&rc_id=%3$s&rc_system=%4$s", "yinonel","aDova216",
	    									"305335911","sc");
	
	    // Send post request
	    con.setDoOutput(true);
	    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	    wr.writeBytes(urlParameters);
	    wr.flush();
	    wr.close();
	
	    int responseCode = con.getResponseCode();
	    System.out.println("\nSending 'POST' request to URL : " + url);
	    System.out.println("Post parameters : " + urlParameters);
	    System.out.println("Response Code : " + responseCode);
	
	    BufferedReader in = new BufferedReader(
	            new InputStreamReader(con.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	//AAAlEuAAfAAA7czAAf
	//AAAlEuAAfAAA7czAAf
	    while ((inputLine = in.readLine()) != null) {
	        response.append(inputLine);
	    }
	    in.close();
	
	    //print result
	    System.out.println(response.toString());

	    
	    
	 // temporary to build request cookie header
	    StringBuilder sb = new StringBuilder();

	    // find the cookies in the response header from the first request
	    List<String> cookies = con.getHeaderFields().get("Set-Cookie");
	    if (cookies != null) {
	        for (String cookie : cookies) {
	            if (sb.length() > 0) {
	                sb.append("; ");
	            }

	            // only want the first part of the cookie header that has the value
	            String value = cookie.split(";")[0];
	            sb.append(value);
	        }
	    }

	    // build request cookie header to send on all subsequent requests
	    String cookieHeader = sb.toString();

	    // with the cookie header your session should be preserved
	    URL regUrl = new URL("https://bgu4u.bgu.ac.il/pls/scwp/!sc.AddCourse");
	    HttpsURLConnection regCon = (HttpsURLConnection) regUrl.openConnection();
	    regCon.setRequestProperty("Cookie", cookieHeader);
	    regCon.setRequestMethod("POST");
	    regCon.setRequestProperty("User-Agent", Constants.USER_AGENT);
	    regCon.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

	    urlParameters = String.format("rc_rowid=%1$s&rn_student_degree=%2$s&rn_department=%3$s&rn_degree_level=%4$s&rn_student_path=%5$s"
	    		+ "&rn_year=%6$s&rn_semester=%7$s&rn_course_institution=%8$s&rn_course_department=%9$s&rn_course_degree_level=%10$s&rn_course=%11$s"
	    		+ "&rn_consult_term=%12$s&rn_course_exists=%13$s&rn_StudentAuthorization_semester=%14$s&rn_CoursesPrintout_semester=%15$s"
	    		+ "&on_group_number_1=%16$s&mainSet=%17$s", 
	    		"AAAlEuAAfAAA7czAAf","1","681","1","3","2017","2","0","681","1","246","1","0","2","2","1","1");
	    
	    regCon.setDoOutput(true);
	    wr = new DataOutputStream(regCon.getOutputStream());
	    wr.writeBytes(urlParameters);
	    wr.flush();
	    wr.close();
	    
	    regCon.connect();
	    responseCode = regCon.getResponseCode();
	    System.out.println("\nSending 'POST' request to URL : " + url);
	    System.out.println("Post parameters : " + urlParameters);
	    System.out.println("Response Code : " + responseCode);
	
	    in = new BufferedReader(
	            new InputStreamReader(regCon.getInputStream()));
	    
	    response = new StringBuffer();

	    while ((inputLine = in.readLine()) != null) {
	        response.append(inputLine);
	    }
	    in.close();
	
	    //print result
	    System.out.println(response.toString());

	}
	
}
