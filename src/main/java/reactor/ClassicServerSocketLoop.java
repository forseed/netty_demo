package reactor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 传统的服务端
 */
public class ClassicServerSocketLoop implements Runnable {
    private final int PORT;
    private final static int MAX_INPUT = 1024;

    public ClassicServerSocketLoop(int port) {
        PORT = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            while (!Thread.interrupted()) {
                new Thread(new Handler(ss.accept())).start();
                // or, single-threaded, or a thread pool
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Handler implements Runnable {
        final Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //read
                byte[] input = new byte[MAX_INPUT];
                socket.getInputStream().read(input);
                //process
                byte[] output = process(input);

                //write
                socket.getOutputStream().write(output);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private byte[] process(byte[] input) {
            // TODO: process
            return null;
        }
    }
}
