package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import Server.httphandlers.*;
import com.sun.net.httpserver.*;

import javax.net.ssl.*;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            // setup the socket address
            InetSocketAddress address = new InetSocketAddress(4000);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = "621adova".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream("testkey.jks");
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext c = SSLContext.getDefault();
                        SSLEngine engine = c.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // get the default parameters
                        SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                        params.setSSLParameters(defaultSSLParameters);

                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS port");
                    }
                }
            });
            httpsServer.createContext("/get", new GetItems());
            httpsServer.createContext("/insert", new InsertNewItem());
            httpsServer.createContext("/likeItem", new LikeItem());
            httpsServer.createContext("/dislikeItem", new DislikeItem());
            httpsServer.createContext("/mybag", new GetMyBag());
            httpsServer.createContext("/myitems", new GetMyItems());
            httpsServer.createContext("/sendMessage", new SendMessage());
            httpsServer.createContext("/getMessages", new GetMessages());
            httpsServer.createContext("/singleItem", new GetSingleItem());
            httpsServer.createContext("/getMessageCount", new GetMessageCount());
            httpsServer.createContext("/sellItem", new SellItem());
            httpsServer.createContext("/addUser", new AddUser());
            httpsServer.createContext("/editItem", new EditItem());
            httpsServer.createContext("/updateUserToken", new UpdateUserToken());
            httpsServer.createContext("/deleteItem", new DeleteItem());
            httpsServer.createContext("/deleteFromMyBag", new DeleteItemFromMyBag());
            httpsServer.createContext("/getRelevantUsers", new GetRelevantUsers());
            httpsServer.setExecutor(null);
            httpsServer.start();
            System.out.println(httpsServer.getAddress());
        }
         catch (Exception exception) {
            System.out.println("Failed to create HTTPS server on port " + 8000 + " of localhost");
            exception.printStackTrace();

        }
}



    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "This is Get response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


}