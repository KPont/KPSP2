package echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Utils;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private static final List<ClientHandler> clients = new ArrayList();
//    private static String clientsOnline = "";
    private static final ArrayList<String> clientNames = new ArrayList();

    public static void stopServer() {
        keepRunning = false;
    }

//    private static void handleClient(Socket socket) throws IOException {
//        Scanner input = new Scanner(socket.getInputStream());
//        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//
//        String message = input.nextLine(); //IMPORTANT blocking call
//        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
//        while (!message.equals(ProtocolStrings.STOP)) {
//            writer.println(message.toUpperCase());
//            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));
//            message = input.nextLine(); //IMPORTANT blocking call
//        }
//        writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
//        socket.close();
//        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
//    }
    public static void removeHandler(ClientHandler ch) {
        clients.remove(ch);
    }

    public static void send(String msg) {
//        String clientsOnline = "";
        if (msg.contains("ONLINE")) {
            String clientsOnline = "";
            clientNames.add(clients.get(clients.size()-1).getClientName());
            for (int i = 0; i < clients.size(); i++) {
//                clientsOnline += clients.get(0).getClientName();
                if (i >= 1) {
                    clientsOnline += ",";
                }
//                clientNames.add(clients.get(i).getClientName());
                clientsOnline += clientNames.get(i);
//                clientsOnline += clients.get(i).getClientName();
            }
            for (int i = 0; i < clients.size(); i++) {
                System.out.println(msg);
                clients.get(i).send(msg + clientsOnline);
            }
        } else if (msg.contains("SEND")) {
            String regex = "#";
            String regex2 = ",";
            String[] prot = msg.split(regex);
            String[] prot2 = prot[1].split(regex2);
            if (prot2[0].equals("*")) {
                for (int i = 0; i < clients.size(); i++) {
                    msg = "MESSAGE" + regex + prot[3] + regex + prot[2];
                    clients.get(i).send(msg);
                }
            } else {

                for (int i = 0; i < prot2.length; i++) {
                    for (int i2 = 0; i2 < clients.size(); i2++) {
                        if (prot2[i].equals(clients.get(i2).getClientName())) {
                            msg = "MESSAGE" + regex + prot[3] + regex + prot[2];
                            clients.get(i2).send(msg);
                        }
                    }
                }
            }
        } 
        else if (msg.contains("CLOSE")){
            String regex = "#";
            String[] prot = msg.split(regex);        
            String clientsOnline = "";
            System.out.println(prot.length);
            clientNames.remove(prot[1]);
            System.out.println(prot[1]);
            for (int i = 0; i < clientNames.size(); i++) {
//                clientsOnline += clients.get(0).getClientName();
                if (i >= 1) {
                    clientsOnline += ",";
                }
//                clientNames.add(clients.get(i).getClientName());
                clientsOnline += clientNames.get(i);
//                clientsOnline += clients.get(i).getClientName();
            }
            for (int i = 0; i < clientNames.size(); i++) {
//                System.out.println(msg);
                msg = "ONLINE#" + clientsOnline;
                clients.get(i).send(msg);
            }
        }
        
        else {
            for (int i = 0; i < clients.size(); i++) {
                System.out.println(msg);
                clients.get(i).send(msg);
            }
        }
    }

    private void runServer() {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");

        Logger
                .getLogger(EchoServer.class
                        .getName()).log(Level.INFO, "Server started. Listening on: " + port + ", bound to: " + ip);

        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            while (keepRunning) {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                ClientHandler ch = new ClientHandler(socket);
                clients.add(ch);
                System.out.println(clients.size());
                ch.start();

            }
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        try {
            String logFile = properties.getProperty("logFile");
            Utils
                    .setLogFile(logFile, EchoServer.class
                            .getName());
            new EchoServer()
                    .runServer();
        } catch (Exception e) {
        } finally {
            Utils.closeLogger(EchoServer.class
                    .getName());
        }

//        new EchoServer().runServer();
    }
}
