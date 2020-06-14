import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ChatClient {

    private final String HOST = "127.0.0.1";
    private final int PORT = 8888;
    private AsynchronousSocketChannel ClientChannel = null;

    public void start()
    {
        try {
            ClientChannel = AsynchronousSocketChannel.open();
            Future<Void>future = ClientChannel.connect(new InetSocketAddress(HOST, PORT));
            future.get();

            BufferedReader consolReader = new BufferedReader(new InputStreamReader(System.in));
            while (true)
            {
                String input = consolReader.readLine();
                byte[]inputBytes = input.getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(inputBytes);
                Future<Integer>writeResult = ClientChannel.write(buffer);
                writeResult.get();

                buffer.flip();
                Future<Integer>readResult = ClientChannel.read(buffer);
                readResult.get();

                String echo = new String(buffer.array());
                buffer.clear();

                System.out.println(echo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            try {
                ClientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
