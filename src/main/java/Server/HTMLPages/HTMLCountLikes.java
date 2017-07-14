package Server.HTMLPages;

import MySQL.MySQLQueryExecutor;
import Server.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Yinon on 15/07/2017.
 */
public class HTMLCountLikes implements HttpHandler {
    public void handle(HttpExchange he) throws IOException {
        JSONArray jsonArr = new JSONArray();
        String query;
        String ret;
        try {
            query= Constants.HTMLCountLikes();
            jsonArr = MySQLQueryExecutor.getInstance().enumItems(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ret="<html><head></head><body><table>" +
                "<th>Item Name</th> <th>User Name</th><th>Likes</th> ";
        for(Object obj:jsonArr){
            ret+="<tr>";
            JSONObject temp=(JSONObject) obj;
            Object name=temp.get("userName");
            String userName=name==null ? "" : name.toString();
            ret+="<td style=\"text-align:center\">"+temp.get("name").toString()+"</td>"+ " <td style=\"text-align:center\">"+userName+"</td>"+" <td style=\"text-align:center\">"+temp.get("count").toString()+"</td> ";
            ret+="</tr>";

        }
        ret+="</table></body></html>";
        String encoding = "UTF-8";
        he.getResponseHeaders().set("Content-Type", "text/html; charset=" + encoding);

        he.sendResponseHeaders(200, ret.toString().getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(ret.getBytes());
        os.close();
    }

}
