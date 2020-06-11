package com.wxt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class chatServer {

    private  int PORT = 8888;
    private final String QUIT  = "quit";
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private ConcurrentHashMap<Integer, Writer>clients;

    public chatServer() {
        this.executorService = new ThreadPoolExecutor(10,10,0,
                TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(10));
        this.clients = new ConcurrentHashMap<Integer, Writer>();
    }

    public  void  start()
    {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("server开启，prot: "+ PORT);
            while (true)
            {
                Socket socket = serverSocket.accept();
                System.out.println("客服端连接了端口: " + socket.getPort());

                this.executorService.execute(new chatServerHander(this,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }

    public void addClient(Socket socket) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clients.put(socket.getPort(),writer);
        System.out.println("客户端 " + socket.getPort() + "已上线");
    }


    public synchronized void forWard(Socket socket, String msg) throws IOException {
        for (int port: clients.keySet())
        {
            if (port!=socket.getPort())
            {
                Writer writer = clients.get(port);
                writer.write(msg);
                writer.flush();
            }
        }
    }

    public synchronized void removeClient(Socket socket) throws IOException {
        if (socket!=null)
        {
            int port = socket.getPort();
            if (clients.containsKey(port))
            {
                clients.get(port).close();
                clients.remove(port);
                System.out.println(port + "已下线");
            }
        }
    }

    public void  close()
    {
        if (serverSocket!=null)
        {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        chatServer chatServer = new chatServer();
        chatServer.start();

    }


}
