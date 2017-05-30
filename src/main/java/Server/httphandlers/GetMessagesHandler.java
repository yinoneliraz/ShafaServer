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
import java.util.Iterator;

/**
 * Created by Yinon on 19/05/2017.
 */
public class GetMessagesHandler implements HttpHandler {
    public void handle(HttpExchange he) throws IOException {
        JSONArray jsonArr = null;
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        JSONObject postData=null;
        try {
            postData= Constants.parseQuery(query);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try {
            query=Constants.getSelectMessagesQuery(postData);
            jsonArr = getConversations(MySQLQueryExecutor.getInstance().getMessages(query));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String encoding = "UTF-8";
        String ret = jsonArr!=null?jsonArr.toString():"";
        he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);

        he.sendResponseHeaders(200, ret.toString().getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(ret.getBytes());
        os.close();
    }
    private JSONArray getConversations(JSONArray jsonArr) {
        JSONArray ret=new JSONArray();
        Iterator<Object> it = jsonArr.iterator();
        while(it.hasNext()){
            JSONArray conv=new JSONArray();
            JSONObject jObj=(JSONObject)it.next();
            it.remove();
            String aSide=jObj.get("fromUserId").toString();
            String bSide=jObj.get("toUserId").toString();
            while(it.hasNext()) {
                JSONObject jconvObj=(JSONObject)it.next();
                if((jconvObj.get("fromUserId").toString().equals(aSide) &&
                        jconvObj.get("toUserId").toString().equals(bSide)) ||
                        (jconvObj.get("toUserId").toString().equals(aSide) &&
                                jconvObj.get("fromUserId").toString().equals(bSide))){
                    conv.add(jconvObj);
                    it.remove();
                }
            }
            if(conv.size()>0){
                ret.add(conv);
            }
            it = jsonArr.iterator();
        }
        return ret;
    }
}
