package socket.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程下ServerSocket
 */
public class ServerSocketDemo implements Runnable {
    private final String name;
    private final int port;

    public ServerSocketDemo(String name, int port) {
        this.name = name;
        this.port = port;
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress().getHostAddress() + " is connecting");
                executorService.submit(new Task(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Task implements Runnable {
        private final Socket socket;

        public Task(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String ip = socket.getInetAddress().getHostAddress();
                for (String line = serverReader.readLine(); line != null; line = serverReader.readLine()) {
                    System.out.println("receive from " + ip + ":" + line);
                    serverWriter.write(line.toUpperCase());
                    serverWriter.newLine();
                    serverWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println(socket.getInetAddress().getHostAddress() + " is closed");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
