/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author Kasper
 */
public class ClientHandler extends Thread {

    Socket socket;
    Scanner scan;
    PrintWriter pw;
    String name = "";

    public ClientHandler(Socket socket) {
        try {
            scan = new Scanner(socket.getInputStream());
            pw = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getClientName() {
        return name;
    }

    public void send(String message) {
        pw.println(message);
//        System.out.println(message);
    }

    public void run() {
        String message = scan.nextLine(); //IMPORTANT blocking call
        String regex = "#";
        String regex2 = ",";
        String returnMessage = "";

        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
        while (!returnMessage.equals(ProtocolStrings.STOP)) {
            String[] prot = message.split(regex);
            switch (prot[0]) {
                case "CONNECT":
                    returnMessage = "ONLINE" + regex;
                    this.name = prot[1];
                    break;
                case "SEND":
                    returnMessage = message + regex + this.name;
                    break;
                case "CLOSE":
                    returnMessage = message + this.name;
//                    returnMessage = 
                    EchoServer.removeHandler(this);
                    break;
                default:
                    returnMessage = message;
                    break;
            }
            EchoServer.send(returnMessage);
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));
            message = scan.nextLine(); //IMPORTANT blocking call
        }
        pw.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
    }
}
