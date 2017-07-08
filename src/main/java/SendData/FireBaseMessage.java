package SendData;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

/**
 * Created by Yinon on 18/06/2017.
 */
public class FireBaseMessage implements Runnable{
    public final static String AUTH_KEY_FCM ="AIzaSyAA0-j6_sasevWXzq4BT4ppkiOT2KpErRU";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    String userID,title,msg,os;
    int badge;

    public FireBaseMessage(String userID, String title, String msg, String os,int badge){
        this.userID=userID;
        this.title=title;
        this.msg=msg;
        this.os=os;
        this.badge=badge;
    }

    public void push(String userDeviceIdKey, String title, String message) throws Exception{

        String authKey = AUTH_KEY_FCM;   // You FCM AUTH key
        String FMCurl = API_URL_FCM;

        URL url = new URL(FMCurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization","key="+authKey);
        conn.setRequestProperty("Content-Type","application/json");

        JSONObject json = new JSONObject();
        if(os.equals("ios")){
            JSONObject notif = new JSONObject();
            JSONObject info = new JSONObject();
            notif.put("title",title);
            notif.put("body",message);
            notif.put("sound","default");
            notif.put("priority","high");
            notif.put("badge",badge);
            notif.put("show_in_foreground",true);
            json.put("notification",notif);
            json.put("to",userDeviceIdKey.trim());
        }
        else {
            JSONObject notif = new JSONObject();
            JSONObject info = new JSONObject();
            notif.put("title", title);
            notif.put("body", message);
            notif.put("color", "pink");
            notif.put("lights", "true");
            notif.put("sound", "default");
            info.put("badge", "1");
            info.put("title", title); // Notification title
            info.put("body", message); // Notification body
            info.put("type", "message");
            json.put("data", info);
            json.put("to", userDeviceIdKey.trim());
            json.put("notification", notif);
        }
        System.out.println(json.toString());

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(json.toString());
        wr.flush();
        conn.getInputStream();
    }

    public void run(){
        try {
            push(userID, title, msg);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
