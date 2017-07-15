package Server.HTMLPages;

import MySQL.MySQLQueryExecutor;
import Server.Constants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Yinon on 14/07/2017.
 */
public class HTMLUsers implements HttpHandler {

    public void handle(HttpExchange he) throws IOException {
        JSONArray jsonArr = new JSONArray();
        String query;
        String ret;
        try {
            query= Constants.HTMLUsersQuery();
            jsonArr = MySQLQueryExecutor.getInstance().enumItems(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ret="<html><head><style>table, th, td {\n" +
                "   border: 1px solid black;\n" +
                "}</style></head><body><table>" +
                "<th>User ID</th> <th>User Name</th><th>Joined In</th><th>More Details</th> ";
        for(Object obj:jsonArr){
            ret+="<tr>";
            JSONObject temp=(JSONObject) obj;
            Object name=temp.get("userName");
            String userName=name==null ? "" : name.toString();
            ret+="<td style=\"text-align:center\"><a href=\"http://facebook.com/"+temp.get("userID").toString() + "\">Link</a>"+"</td>"+
                    "<td style=\"text-align:center\">"+userName+"</td>"+
                    "<td style=\"text-align:center\">"+temp.get("joinDate").toString()+"</td>"+
                    "<td style=\"text-align:center\"><a href=\"/UserDetails?id=" + temp.get("userID").toString() + "\">Link</a></td>";
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
