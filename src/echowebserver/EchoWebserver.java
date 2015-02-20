/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echowebserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import echoserver.EchoServer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mads
 */
public class EchoWebserver {

    // Defaults
    static int port = 8080;
    static String ip = "localhost";
    
    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                port = Integer.parseInt(args[0]);
                ip = args[1];
            }
            HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);
            server.createContext("/", new FileHandler());
            server.createContext("/chatlog.txt", new FileHandler());
            server.createContext("/online.html", new OnlinePageHandler());
            
            server.setExecutor(null);
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(EchoWebserver.class.getName()).log(Level.SEVERE, null, ex);
        }
        EchoServer.runServer();
    }
    
    static class FileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException{
             String requestURI = he.getRequestURI().toString();
            String fileName = requestURI.substring(requestURI.lastIndexOf("/") + 1);
            String contentFolder = "public/";
            if(fileName.isEmpty()){
                fileName = "index.html";
            }
            else if(fileName.equals("chatlog.txt")){
                contentFolder = "";
                fileName = "chatLog.txt.1";
            }
            
            File file = new File(contentFolder
                    + fileName);
            if (file.exists()) {
                byte[] bytesToSend = new byte[(int) file.length()];
                try {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(bytesToSend, 0, bytesToSend.length);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                Headers h = he.getResponseHeaders();
                switch (fileName.substring(fileName.lastIndexOf(".") + 1)) {
                    case "html":
                        h.add("Content-Type", "text/html");
                        break;
                    case "css":
                        h.add("Content-Type", "text/css");
                        break;
                    case "1":
                        h.add("Content-Type", "text/plain");
                        break;
                    case "txt":
                        h.add("Content-Type", "text/plain");
                        break;
                    case "pdf":
                        h.add("Content-Type", "application/pdf");
                        break;
                    case "jpg":
                        h.add("Content-Type", "image/jpeg");
                        break;
                    case "jar":
                        h.add("Content-Type", "application/zip");
                        break;
                }
                he.sendResponseHeaders(200, bytesToSend.length);
                try (OutputStream os = he.getResponseBody()) {
                    os.write(bytesToSend, 0, bytesToSend.length);
                }
            } else {
                file = new File(contentFolder
                        + "404.html");
                byte[] bytesToSend = new byte[(int) file.length()];
                try {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(bytesToSend, 0, bytesToSend.length);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                Headers h = he.getResponseHeaders();
                he.sendResponseHeaders(200, bytesToSend.length);
                try (OutputStream os = he.getResponseBody()) {
                    os.write(bytesToSend, 0, bytesToSend.length);
                }
            }
        }
    }
    static class OnlinePageHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("<!DOCTYPE html>");
                sb.append("<html>");
                sb.append("<head>");
                sb.append("<title>Echo chat - Home</title>");
                sb.append("<meta charset='UTF-8'>");
                sb.append("</head>");
                sb.append("<body>");
                sb.append(EchoServer.getOnlineUsers());
                sb.append("</body>");
                sb.append("</html>");
                String response = sb.toString();
                Headers h = he.getResponseHeaders();
                h.add("Content-Type", "text/html");
                he.sendResponseHeaders(200, response.length());
                try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                    pw.print(response);
                }
            }   catch (IOException ex) {
                Logger.getLogger(EchoWebserver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    static class LogPageHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("<!DOCTYPE html>");
                sb.append("<html>");
                sb.append("<head>");
                sb.append("<title>Echo chat - Home</title>");
                sb.append("<meta charset='UTF-8'>");
                sb.append("</head>");
                sb.append("<body>");
                sb.append("<h2></h2>");
                sb.append("</body>");
                sb.append("</html>");
                String response = sb.toString();
                Headers h = he.getResponseHeaders();
                h.add("Content-Type", "text/html");
                he.sendResponseHeaders(200, response.length());
                try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                    pw.print(response);
                }
            }   catch (IOException ex) {
                Logger.getLogger(EchoWebserver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
