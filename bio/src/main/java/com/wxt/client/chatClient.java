package com.wxt.client;

import java.io.*;
import java.net.Socket;

public class chatClient {

    private final int PORT = 8888;
    private final String IP = "127.0.0.1";
    private BufferedWriter writer;
    private BufferedReader reader;
    Socket socket;

    public void start()
    {
        try {
            socket = new Socket(IP,PORT);
            System.out.println("连接成功!");
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new chatClientHander(this)).start();

            String msg = null;
            while (!socket.isInputShutdown() && (msg = reader.readLine())!=null)
            {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }

    public void send(String msg) {
        if (!socket.isOutputShutdown())
        {
            try {
                writer.write(msg + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close()
    {
        if (writer!=null)
        {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

       chatClient client = new chatClient();
       client.start();
    }
}
