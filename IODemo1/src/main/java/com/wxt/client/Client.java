package com.wxt.client;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        final int DEFALUT_PORT = 8888;
        final String HOST  = "127.0.0.1";
        Socket socket  = null;
        try {
            socket = new Socket(HOST,DEFALUT_PORT);
            System.out.println("客户端与服务端连接，使用端口" + socket.getPort());

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true)
            {
                System.out.println("请输入向服务端发送的内容\n");
                msg = consoleReader.readLine();

                writer.write(msg+"\n");
                writer.flush();

                String res = reader.readLine();
                System.out.println("我是服务器："+res + "\n");

                if (msg.equals("quit"))
                {
                    break;
                }

            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("关闭client sock 异常");
            }
        }

    }

}
