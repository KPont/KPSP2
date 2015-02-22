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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mads
 */
public class EchoWebserver {

    // Defaults
    static int port = 80;
    static String ip = "localhost";
    
    public static void main(String[] args) {
        try {
            
            if (args.length == 2) {
                port = Integer.parseInt(args[0]);
                ip = args[1];
            }
            HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);
            server.createContext("/", new FileHandler());
            server.createContext("/chatlog.html", new LogPageHandler());
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
        public void handle(HttpExchange he) throws IOException {

                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                    sb.append("<head>");
                        sb.append("<title>Echo chat - Home</title>");
                        sb.append("<meta charset='UTF-8'>");
                        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
                        sb.append("<link rel='stylesheet' type='text/css' href='/style.css'>");
                    sb.append("</head>");
                    sb.append("<body>");
                        sb.append("<header>");
                        sb.append("<h1>Echo Chat</h1>");
                            sb.append("<nav>");
                                sb.append("<ul>");
                                    sb.append("<li><a href='/'>Hjem</a></li>");
                                    sb.append("<li class'active'><a href='/online.html'>Online brugere</a></li>");
                                    sb.append("<li><a href='/chatlog.html'>Chat log</a></li>");
                                sb.append("</ul>");
                            sb.append("</nav>");
                        sb.append("</header>");
                        sb.append("<main>");
                            sb.append(EchoServer.getOnlineUsers());
                        sb.append("</main>");
                        sb.append("<footer>");
                            sb.append("<span>"
                                    + "Hjemmeside og Chat system lavet af Alexander D. Lund, Kasper H. Pontoppidan og Mads C. Hansen"
                            + "</span>");
                        sb.append("</footer>");
                    sb.append("</body>");
                sb.append("</html>");
                sb.append("</body>");
                sb.append("</html>");
                String response = sb.toString();
                Headers h = he.getResponseHeaders();
                h.add("Content-Type", "text/html");
                he.sendResponseHeaders(200, response.length());
                try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                    pw.print(response);
                }
        }
    }
    static class LogPageHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException{
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
                sb.append("<head>");
                    sb.append("<title>Echo chat - Home</title>");
                    sb.append("<meta charset='UTF-8'>");
                    sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
                    sb.append("<link rel='stylesheet' type='text/css' href='/style.css'>");
                sb.append("</head>");
                sb.append("<body>");
                    sb.append("<header>");
                    sb.append("<h1>Echo Chat</h1>");
                        sb.append("<nav>");
                            sb.append("<ul>");
                                sb.append("<li><a href='/'>Hjem</a></li>");
                                sb.append("<li><a href='/online.html'>Online brugere</a></li>");
                                sb.append("<li class='active'><a href='/chatlog.html'>Chat log</a></li>");
                            sb.append("</ul>");
                        sb.append("</nav>");
                    sb.append("</header>");
                    sb.append("<main>");
                    byte[] encoded = Files.readAllBytes(Paths.get("chatlog.txt"));
                    sb.append(new String(encoded, "UTF-8"));
                    sb.append("<footer>");
                        sb.append("<span>"
                                + "Hjemmeside og Chat system lavet af Alexander D. Lund, Kasper H. Pontoppidan og Mads C. Hansen"
                        + "</span>");
                    sb.append("</footer>");
                sb.append("</body>");
            sb.append("</html>");
            String response = sb.toString();
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");
            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response);
            }
        }
    }
    
}
