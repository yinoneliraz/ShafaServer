package Server;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import MySQL.MySQLQueryExecutor;
public class Main {
	static MySQLQueryExecutor mySQLQueryExecutor=MySQLQueryExecutor.getInstance();
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(Constants.port), 0);
        server.createContext("/get", (HttpHandler) new getHandler());
        server.createContext("/insert", (HttpHandler) new InsertHandler());
        server.createContext("/basket", (HttpHandler) new BasketHandler());
        server.setExecutor(null);
        server.start();
        System.out.println(server.getAddress());
    }


}