package Server.httphandlers;

import MySQL.MySQLQueryExecutor;
import Server.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Yinon on 10/07/2017.
 */
public class GetRelevantUsers implements HttpHandler {
    public void handle(HttpExchange he) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + ":Get relevant users , started handling");

        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params=new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();

        try {
            params= Constants.parseQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            query=Constants.getRelevantUsersQuery(params);
        } catch (Exception e) {
            System.out.println(dateFormat.format(date) + ":Error");
            e.printStackTrace();
        }

        JSONArray jsonArr;
        jsonArr=MySQLQueryExecutor.getInstance().getRelevantUsers(query);
        String ret=jsonArr.toString();
        he.sendResponseHeaders(200, ret.getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(ret.toString().getBytes());
        os.close();
        System.out.println(dateFormat.format(date) + ":Get relevant users, finished handling");

    }
}
