package com.wxt.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

public class ChatServer {
    private final int PORT = 8888;
    private ServerSocketChannel serverSocketChannel = null;
    private final int BUFFER_SIZE  = 1024;

    private ByteBuffer rBuffer;
    private ByteBuffer wBuffer;
    private Selector selector;
    private Charset charset = Charset.forName("UTF-8");
    public void start()
    {
        //启动服务器
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            System.out.println("已经启动服务器，绑定端口： " + serverSocketChannel.socket().getLocalPort());

            rBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            wBuffer = ByteBuffer.allocate(BUFFER_SIZE);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 轮询       处理事件//select()返回触发的事件个数   ???
            while (true)
            {
                selector.select();
                //注册事件
                System.out.println("已有事件被触发");
                //得到触发的事件key
                Set<SelectionKey>selectionKeys = selector.selectedKeys();
                for (SelectionKey key:selectionKeys)
                {
                    handles(key);
                }
                selectionKeys.clear();    //  手动清空处理多的key,如果不clear的话,处理过的key还在里面
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handles(SelectionKey key) throws IOException {
        if (key.isAcceptable())
        {
            // 注意是 SocketChannel 即客户端Channel
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(),SelectionKey.OP_READ);
            System.out.println("客户端：" + socketChannel.socket().getPort() + "已连接");

        }
        else if(key.isReadable())
        {
            String msg = readMsg((SocketChannel)key.channel());
            if (msg.isEmpty())
            {
                key.cancel();
                key.selector().wakeup();
            }else
            {
                forwardMsg((SocketChannel)key.channel(),msg);
            }
        }
    }

    private void forwardMsg(SocketChannel client,  String msg) throws IOException {
        Set<SelectionKey>keys = selector.keys();
        for (SelectionKey selectionKey:keys)
        {
            //Channel channel1 = (Channel) selectionKey.channel();
            if (selectionKey.channel() instanceof ServerSocketChannel)continue; // 跳过服务器
            if(selectionKey.isValid() && !selectionKey.channel().equals(client))
            {
                wBuffer.clear();
//                wBuffer.put(charset.encode("客户 [" + client.socket().getPort() + "] " + msg));
                wBuffer.put(msg.getBytes());
                wBuffer.flip();
                while (wBuffer.hasRemaining())
                {
                    ((SocketChannel)selectionKey.channel()).write(wBuffer);  // 把buffer里面的数据给 socketChannel
                }
            }

        }
    }

    private String readMsg(SocketChannel channel) throws IOException {
        rBuffer.clear();
        while (channel.read(rBuffer)>0);
        rBuffer.flip();
        return rBuffer.toString();
    }

    public static void main(String[] args) {

        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }


}
