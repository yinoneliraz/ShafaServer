package Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import Server.httphandlers.BasketHandler;
import Server.httphandlers.InsertHandler;
import Server.httphandlers.MyBagHandler;
import Server.httphandlers.GetHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import MySQL.MySQLQueryExecutor;
public class Main {
	static MySQLQueryExecutor mySQLQueryExecutor=MySQLQueryExecutor.getInstance();
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/get",  new GetHandler());
        server.createContext("/insert", new InsertHandler());
        server.createContext("/basket",  new BasketHandler());
        server.createContext("/mybag", new MyBagHandler());
        server.createContext("/myitems",  new GetMyItems());
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