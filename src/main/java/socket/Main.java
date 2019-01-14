package socket;

public class Main {
    public static void main(String[] args) {
        new Thread(new SocketDemo("alice"), "alice").start();
        new Thread(new ServerSocketDemo("httpdemo", 9999), "httpdemo").start();
    }
}
