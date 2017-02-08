package SendData;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import Server.Constants;


public class post {
	public static void main(String[] args) throws Exception{
		sendPost();
	}
	
    private static void sendPost() throws Exception {
	    String url = String.format("http://localhost:%1$d/insert", Constants.port);
	    URL obj = new URL(url);
	    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
	    //add reuqest header
	    con.setRequestMethod("POST");
	    con.setRequestProperty("User-Agent", Constants.USER_AGENT);
	    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	
	    String urlParameters = String.format(Constants.INSERT_FORMAT_POST, "Nice Shoes","hahagana 11",
	    									1,"Shoes","42",120.0,"very nice shoes",32.831331,35.079918);
	
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
	
	    while ((inputLine = in.readLine()) != null) {
	        response.append(inputLine);
	    }
	    in.close();
	
	    //print result
	    System.out.println(response.toString());

	}
	
}
