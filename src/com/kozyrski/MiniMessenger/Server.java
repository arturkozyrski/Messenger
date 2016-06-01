package com.kozyrski.MiniMessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

    Socket clientSocket = null;
    ArrayList clientOutputStreams;
    ServerSocket serverSocket;
    ArrayList nickNames;
    boolean isConnected;

    public static void main(String[] args) {
        new Server().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList();
        nickNames = new ArrayList();
        try {
            serverSocket = new ServerSocket(5001);
            while (true) {
                clientSocket = serverSocket.accept();
                PrintWriter clientPrintWriter = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(clientPrintWriter);
                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ClientHandler implements Runnable {
        BufferedReader messageFromClient;
        Socket socket;
        String nick = "";

        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                messageFromClient = new BufferedReader(isReader);
                nick = messageFromClient.readLine();
                nickNames.add(nick);
                sendNickNames(nickNames);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;

            try {
                while ((message = messageFromClient.readLine()) != null) {
                    System.out.println("read " + message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                nickNames.remove(nick);
                sendNickNames(nickNames);

            }

        }
    }
    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public void sendNickNames(ArrayList list) {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(list);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
