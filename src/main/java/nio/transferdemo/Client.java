package nio.transferdemo;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open();
             FileChannel fileChannel = new FileInputStream("src/resources/file/demo.txt").getChannel()) {
            socketChannel.connect(new InetSocketAddress("localhost", 8899));
            fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
