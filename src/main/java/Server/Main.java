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
        server.createContext("/get",  new GetItems());
        server.createContext("/insert", new InsertNewItem());
        server.createContext("/likeItem",  new LikeItem());
        server.createContext("/dislikeItem",  new DislikeItem());
        server.createContext("/mybag", new GetMyBag());
        server.createContext("/myitems",  new GetMyItems());
        server.createContext("/sendMessage",  new SendMessage());
        server.createContext("/getMessages",  new GetMessages());
        server.createContext("/singleItem",  new GetSingleItem());
        server.createContext("/getMessageCount",  new GetMessageCount());
        server.createContext("/sellItem",  new SellItem());
        server.createContext("/addUser",  new AddUser());
        server.createContext("/editItem",  new EditItem());
        server.createContext("/updateUserToken",  new UpdateUserToken());
        server.createContext("/deleteItem",  new DeleteItem());
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