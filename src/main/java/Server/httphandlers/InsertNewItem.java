package Server.httphandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Server.Constants;
import org.json.simple.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import MySQL.MySQLQueryExecutor;

public class InsertNewItem implements HttpHandler {

	public void handle(HttpExchange he) throws IOException {
		int retVal=0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date) + ":Insert new item, started handling");

		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        try {
        	params= Constants.parseQuery(query);
		} catch (Exception e) {
			System.out.println(dateFormat.format(date) + ":Error");
			e.printStackTrace();
		}
		double dist= (6371 * Math.acos(Math.cos(Math.toRadians(Double.parseDouble(params.get("lat").toString()))) *
				Math.cos(Math.toRadians(31.250498)) *
				Math.cos(Math.toRadians(34.793083) -
				Math.toRadians(Double.parseDouble(params.get("lng").toString()))) +
				Math.sin(Math.toRadians(Double.parseDouble(params.get("lat").toString()))) *
						Math.sin(Math.toRadians(31.250498))));
		JSONObject retJson=new JSONObject();

		try {
			query = Constants.getSelectSpecialsQuery(params);
			JSONObject ret = MySQLQueryExecutor.getInstance().getSpeciealUsers(query);
			if (ret != null) {
				params.put("lat", ret.get("lat"));
				params.put("lng", ret.get("lng"));
				query = Constants.getInsertQuery(params);
				retVal = MySQLQueryExecutor.getInstance().executeSQL(query);
			}
			else{
				if(dist>6 && dist < 210){
					retJson.put("output","שאפה יקרה,\nבשלב זה לא ניתן לבצע מכירות וקניות מחוץ לב\"ש.\nמבטיחים לעדכן בהקדם");
				}
				else{
					query = Constants.getInsertQuery(params);
					retVal = MySQLQueryExecutor.getInstance().executeSQL(query);
				}
			}

		} catch (Exception e) {
			System.out.println(dateFormat.format(date) + ":Error");
			e.printStackTrace();
		}
		String encoding = "UTF-8";
		he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);
		retJson.put("output", retVal == 1 ? "success" : "fail");
		he.sendResponseHeaders(200, retJson.toString().getBytes().length);
		OutputStream os = he.getResponseBody();
		os.write(retJson.toString().getBytes());
		os.close();
		System.out.println(dateFormat.format(date) + ":Insert new item, finished handling");

	}
}
