package com.wxt.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

public class ChatClient {
    private Selector selector = null;
    private SocketChannel client = null;
    private final String IP = "127.0.0.1";
    private final int PORT = 8888;
    private final int BUFFER_SIZE = 1024;
    private ByteBuffer rBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer wBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private Charset charset = Charset.forName("UTF-8");

    public void start()
    {
        try {
            client = SocketChannel.open();
            client.configureBlocking(false);
            selector = Selector.open();
            client.register(selector, SelectionKey.OP_CONNECT);
            client.connect(new InetSocketAddress(IP,PORT));

            while (true)
            {
                selector.select();
                System.out.println("有事件触发");
                Set<SelectionKey>selectionKeys = selector.selectedKeys();
                for(SelectionKey key:selectionKeys)
                {
                    handles(key);
                }
                selectionKeys.clear();  // 下次轮询可以得到新的selectionKeys
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handles(SelectionKey key) throws IOException {
        // CONNECT -- 连接就绪事件
        if(key.isConnectable())
        {
            SocketChannel client = (SocketChannel) key.channel();
            if (client.isConnectionPending())
            {
                client.finishConnect();
            }
            new Thread(new chatClientHander(this)).start();
            System.out.println("连接成功");
            client.register(selector,SelectionKey.OP_READ);
        }else if (key.isReadable())
        {
            String msg = readMsg((SocketChannel)key.channel());
            if (msg.isEmpty())
            {
                //服务端有错误
                close(key.selector());
            }else
            {
                System.out.println(msg);
            }
        }

    }

    private String readMsg(SocketChannel channel) throws IOException {
        rBuffer.clear();
        while (channel.read(rBuffer)>0);
        rBuffer.flip();
//        return String.valueOf(charset.decode(rBuffer));
        return rBuffer.toString();
    }

    public void send(String msg) throws IOException {
        wBuffer.clear();
//        wBuffer.put(charset.encode( msg));
        wBuffer.put(msg.getBytes());
        wBuffer.flip();
        while (wBuffer.hasRemaining())
        {
            client.write(wBuffer);
        }
        if ("quit".equals(msg))
        {
            client.close();
        }
    }
    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();

    }
}
