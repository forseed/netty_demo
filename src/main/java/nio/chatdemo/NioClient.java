package nio.chatdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioClient {
    private static Selector selector;

    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress("localhost", 8899));

            while (true) {
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isConnectable()) {
                        doConnect(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        doRead(selectionKey);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void doRead(SelectionKey selectionKey) {
        try {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int count = client.read(readBuffer);
            if (count > 0) {
                String receivedMessage = new String(readBuffer.array(), 0, count);
                System.out.println(receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void doConnect(SelectionKey selectionKey) {
        try {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            System.out.println("client->" + client);

            if (client.isConnectionPending()) {
                client.finishConnect();

                //连接成功
                ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                writeBuffer.put((LocalDateTime.now() + " 连接成功").getBytes());
                writeBuffer.flip();
                client.write(writeBuffer);

                //键盘录入
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(() -> {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                        for (String line = br.readLine(); line != null; line = br.readLine()) {
                            writeBuffer.clear();
                            writeBuffer.put(line.getBytes());
                            writeBuffer.flip();
                            client.write(writeBuffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            client.register(selector, SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
