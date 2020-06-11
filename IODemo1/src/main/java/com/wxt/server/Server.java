package com.wxt.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        final int DEFAULT_PORT = 8888;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器，监听端口：" + DEFAULT_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(String.format("用于和客服端通信的socket [ %d ]", socket.getPort()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = null;
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                while ((msg=(reader.readLine()))!=null)
                {
                    System.out.println("服务器读取了："+ msg);
                    writer.write("我读过了" +msg + "这句话" + "\n");
                    writer.flush();
                    if (msg.equals("quit"))
                    {
                        break;
                    }
                }
                reader.close();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("sock 创建异常");
        }finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("serverSock 关闭异常");
            }
        }


    }

}
