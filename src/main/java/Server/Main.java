package Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import Server.httphandlers.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(4000), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/get",  new GetItemsHandler());
        server.createContext("/insert", new InsertItemHandler());
        server.createContext("/basket",  new InsertItemToBasketHandler());
        server.createContext("/mybag", new GetMyBagHandler());
        server.createContext("/myitems",  new GetMyItems());
        server.createContext("/sendMessage",  new InsertMessageHandler());
        server.createContext("/getMessages",  new GetMessagesHandler());
        server.createContext("/singleItem",  new GetSingleItem());
        server.setExecutor(null);
        server.start();
        System.out.println(server.getAddress());
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