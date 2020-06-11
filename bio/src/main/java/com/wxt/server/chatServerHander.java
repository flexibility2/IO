package com.wxt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class chatServerHander implements Runnable{

    private chatServer chatServer;
    private Socket socket;

    public chatServerHander(chatServer chatServer,Socket socket)
    {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            this.chatServer.addClient(socket);
            String msg = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((msg=(reader.readLine()))!=null)
            {
                System.out.println("客户端:[" + socket.getPort() + "]" + msg);
                String fwmsg = "客户端:[" + socket.getPort() + "]" + msg + "\n";
                this.chatServer.forWard(socket,fwmsg);
                if ("quit".equals(msg))
                {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                this.chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
