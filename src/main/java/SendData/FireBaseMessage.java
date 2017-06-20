package SendData;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

/**
 * Created by Yinon on 18/06/2017.
 */
public class FireBaseMessage implements Runnable{
    public final static String AUTH_KEY_FCM ="AAAAI8yKOrw:APA91bGvcq3GnKeUaFm5Zypjf9GrwsV6QPZySSwhfXWaeW8vc9RRdV3Nd8Va3rwG2-YaXboBGTjHg67YgncIHgzd_F8oGFbpyfbTXzI1aGM__eL9uOHdwv_VhAT2zi_AJFQjJVeGlUjX";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    String userID,title,msg;

    public FireBaseMessage(String userID, String title, String msg){
        this.userID=userID;
        this.title=title;
        this.msg=msg;
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
        json.put("to",userDeviceIdKey.trim());
        JSONObject info = new JSONObject();
        info.put("title", title); // Notification title
        info.put("body", message); // Notification body
        info.put("type", "message");
        json.put("data", info);
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
