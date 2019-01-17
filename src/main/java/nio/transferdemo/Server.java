package nio.transferdemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类似socket demo,用Channel和ByteBuffer实现的简单版本
 */
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8899));
            ExecutorService service = Executors.newSingleThreadExecutor();
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                service.submit(new Task(socketChannel));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Task implements Runnable {
        private final SocketChannel socketChannel;

        Task(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(2);
                int size = 0;
                for (int len = socketChannel.read(buffer); len != -1; len = socketChannel.read(buffer)) {
                    size += len;
                    System.out.println("receive from client :" + len);
                    buffer.clear();
                }
                System.out.println("count:" + size);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("client error");
            } finally {
                System.out.println("client exit...");
            }
        }
    }
}
