package nio.chatdemo;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 服务的接收数据，通知所有的客户端
 */
public class NioServer {
    private static Map<String, SocketChannel> clientMap = new HashMap<>();

    private static Selector selector;

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8899));

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    doAccept(selectionKey);
                } else if (selectionKey.isReadable()) {
                    doRead(selectionKey);
                }
            }
        }
    }

    private static void doAccept(SelectionKey selectionKey) {
        try {
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            String key = "【" + UUID.randomUUID().toString() + "】";
            clientMap.put(key, client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void doRead(SelectionKey selectionKey) {
        SocketChannel client = null;
        try {
            client = (SocketChannel) selectionKey.channel();

            ByteBuffer readBuf = ByteBuffer.allocate(1024);
            int len = client.read(readBuf);

            if (len > 0) {
                readBuf.flip();

                Charset charset = Charset.forName("UTF-8");
                String receivedMessage = String.valueOf(charset.decode(readBuf).array());
                System.out.println(client + ": " + receivedMessage);

                String senderKey = null;

                //获取当前客户端对应的key
                for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                    if (client == entry.getValue()) {
                        senderKey = entry.getKey();
                        break;
                    }
                }

                //给所有的客户端回写消息
                for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                    SocketChannel value = entry.getValue();
                    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                    writeBuffer.put((senderKey + ": " + receivedMessage).getBytes());
                    writeBuffer.flip();
                    value.write(writeBuffer);
                }
            }
        } catch (Exception e) {
            assert client != null;
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println(client + " is close");
        }
    }
}
