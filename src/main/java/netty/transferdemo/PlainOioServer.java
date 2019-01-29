package netty.transferdemo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Oio
 */
public class PlainOioServer {
    public void server(int port) throws IOException {
        ServerSocket socket = new ServerSocket(port);
        for (; ; ) {
            Socket clientSocket = socket.accept();
            System.out.println("Accepted connection from " + clientSocket);
            new Thread(() -> {
                try (OutputStream outputStream = clientSocket.getOutputStream()) {
                    outputStream.write("hello".getBytes());
                    outputStream.flush();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
