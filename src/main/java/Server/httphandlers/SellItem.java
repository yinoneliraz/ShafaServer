package Server.httphandlers;

import MySQL.MySQLQueryExecutor;
import Server.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yinon on 07/06/2017.
 */
public class SellItem implements HttpHandler {
    public void handle(HttpExchange he) throws IOException {
        int retVal = 0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + ":Sell item, started handling");

        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        JSONObject params = new JSONObject();
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        try {
            params = Constants.parseQuery(query);

        } catch (Exception e) {
            System.out.println(dateFormat.format(date) + ":Error");
            e.printStackTrace();
        }
        try {
            query = Constants.getUpdateItemToSell(params,1);
            retVal = MySQLQueryExecutor.getInstance().executeSQL(query);
            if(retVal==1) {
                query = Constants.getUpdateItemToSell(params, 2);
                retVal = MySQLQueryExecutor.getInstance().executeSQL(query);
            }
        } catch (Exception e) {
            System.out.println(dateFormat.format(date) + ":Error");
            e.printStackTrace();
        }
        String encoding = "UTF-8";
        he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);
        JSONObject retJson=new JSONObject();
        retJson.put("output",retVal==1 ? "success" : "fail");

        he.sendResponseHeaders(200, retJson.toString().getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(retJson.toString().getBytes());
        os.close();
        System.out.println(dateFormat.format(date) + ":Sell item, finished handling");
    }
}
