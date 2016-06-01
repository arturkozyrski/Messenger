package com.kozyrski.MiniMessenger;

import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {
    Socket socket;
    BufferedReader bufferedReader;
    PrintWriter printWriter;
    Thread readerThread = new Thread(new IncomingReader());
    Thread receiveGuestList = new Thread();
    Gui gui;
    String nickName;
    boolean nameSent;

    ClientMain(Gui gui, String nickName) {
        this.nickName = nickName;
        this.gui = gui;
        setUpNetworking();
    }
    private void setUpNetworking() {
        try {
            socket = new Socket("127.0.0.1", 5001);
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(streamReader);
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.flush();
            nameSent = true;
            readerThread.start();
            System.out.println("Connection established");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = bufferedReader.readLine()) != null) {
                    System.out.println("read " + message);

                    if (message.startsWith("[")) {
                        message = message.replaceAll(",", "");
                        message = message.replaceAll("\\[", "").replaceAll("\\]", "");
                        String[] nickNames = message.split("\\s+");
                        gui.addGuest(nickNames);
                    } else {
                        try {
                            gui.addMessage(message);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public PrintWriter getWriter() {
        return printWriter;
    }

    public BufferedReader getReader() {
        return bufferedReader;
    }

    public String toString() {
        return nickName;
    }

    public boolean isConnected() {

        return false;
    }


}


