package socket.demo;

import java.io.*;
import java.net.Socket;

/**
 * Socket
 */
public class SocketDemo {

    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 8899);
            socket.setSoTimeout(5000);
            BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader reader = new BufferedReader(new InputStreamReader((System.in)));
            for (String line = reader.readLine(); !"exit".equals(line); line = reader.readLine()) {
                clientWriter.write(line);
                clientWriter.newLine();
                clientWriter.flush();
                System.out.println("receive from server:" + serverReader.readLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
