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
 * Created by Yinon on 19/05/2017.
 */
public class GetMessages implements HttpHandler {
    public void handle(HttpExchange he) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + ":Get messages, started handling");

        JSONArray jsonArr = null;
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        JSONObject postData=null;
        try {
            postData= Constants.parseQuery(query);
        } catch (Exception e) {
            System.out.println(dateFormat.format(date) + ":Error");
            e.printStackTrace();
        }

        try {
            query=Constants.getSelectMessagesQuery(postData);
            jsonArr = getConversations(MySQLQueryExecutor.getInstance().getMessages(query));
        } catch (Exception e) {
            System.out.println(dateFormat.format(date) + ":Error");
            e.printStackTrace();
        }

        String encoding = "UTF-8";
        String ret = jsonArr!=null?jsonArr.toString():"";
        he.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);

        he.sendResponseHeaders(200, ret.toString().getBytes().length);
        OutputStream os = he.getResponseBody();
        os.write(ret.getBytes());
        os.close();
        System.out.println(dateFormat.format(date) + ":Get messages, finished handling");
    }
    private JSONArray getConversations(JSONArray jsonArr) {
        JSONArray ret=new JSONArray();
        Iterator<Object> it = jsonArr.iterator();
        while(it.hasNext()){
            JSONObject jObj=(JSONObject)it.next();
            JSONObject info=new JSONObject();
            info.put("fromUserName",jObj.get("fromUserName"));
            info.put("fromUserId",jObj.get("fromUserId"));
            info.put("fromUserImage",jObj.get("fromUserImg"));
            info.put("toUserName",jObj.get("toUserName"));
            info.put("toUserId",jObj.get("toUserId"));
            info.put("toUserImage",jObj.get("toUserImg"));
            String sessionID=""+
                    Math.max(Integer.parseInt(jObj.get("fromUserId").toString()),Integer.parseInt(jObj.get("toUserId").toString()))+
                    "_" +
                    Math.min(Integer.parseInt(jObj.get("fromUserId").toString()),Integer.parseInt(jObj.get("toUserId").toString()));

            info.put("sessionId",sessionID);
            info.put("regardingItem",jObj.get("regardingItem"));
            it.remove();
            String aSide=jObj.get("fromUserId").toString();
            String bSide=jObj.get("toUserId").toString();
            JSONArray msgList=new JSONArray();
            while(it.hasNext()) {
                JSONObject jconvObj=(JSONObject)it.next();
                if((jconvObj.get("fromUserId").toString().equals(aSide) &&
                        jconvObj.get("toUserId").toString().equals(bSide)) ||
                       (jconvObj.get("toUserId").toString().equals(aSide) &&
                        jconvObj.get("fromUserId").toString().equals(bSide))){
                    JSONObject messageObj=new JSONObject();
                    messageObj.put("messageId",jconvObj.get("messageId"));
                    messageObj.put("sender",jconvObj.get("fromUserName"));
                    messageObj.put("receiver",jconvObj.get("toUserName"));
                    messageObj.put("date",jconvObj.get("messageDate"));
                    messageObj.put("text",jconvObj.get("messageStr"));
                    msgList.add(messageObj);
                    it.remove();
                }
            }
            if(msgList.size()>0){
                JSONObject conv=new JSONObject();
                conv.put("info",info);
                conv.put("messageList",msgList);
                ret.add(conv);
            }
            it = jsonArr.iterator();
        }
        return ret;
    }
}
