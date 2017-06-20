package SendData;

import MySQL.MySQLQueryExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Yinon on 19/06/2017.
 */
public class FireBase {
    ExecutorService exec= Executors.newFixedThreadPool(10);
    private static FireBase instance=null;

    public static FireBase getInstance(){
        if(instance==null){
            instance=new FireBase();
        }
        return instance;
    }

    private FireBase(){
    }

    public void sendMessage(String uID,String title,String msg){
        FireBaseMessage fMSG=new FireBaseMessage(uID,title,msg);
        exec.execute(fMSG);
    }

    public String getUserTokenByFacebookID(String fbID){
        return MySQLQueryExecutor.getInstance().getFireBaseToken(fbID);
    }
}
