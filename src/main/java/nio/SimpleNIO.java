package nio;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.Arrays;

public class SimpleNIO {
    public static void main(String[] args) {
        //1.base demo
//        demo1();

        //2.fileInputStream的getChannel;
//        demo2();

        //3.fileOutputStream的getChannel;
//        demo3();

        //4.文件复制
//        demo4();

        //5.Slice Buffer
//        demo5();

        //6.MappedByteBuffer
        demo6();

        //7.gathering scattering
        demo7();
    }

    private static void demo7() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8899));

            int messageLength = 2 + 3 + 4;

            ByteBuffer[] buffers = new ByteBuffer[3];

            buffers[0] = ByteBuffer.allocate(2);
            buffers[1] = ByteBuffer.allocate(3);
            buffers[2] = ByteBuffer.allocate(4);

            SocketChannel socketChannel = serverSocketChannel.accept();

            while (true) {

                int bytesRead = 0;
                for (long r = socketChannel.read(buffers); bytesRead < messageLength; r = socketChannel.read(buffers)) {
                    bytesRead += r;
                    System.out.println("bytesRead:" + bytesRead);
                }

                Arrays.stream(buffers).map(buffer -> "position: " + buffer.position() + ", limit: " + buffer.limit()).
                        forEach(System.out::println);

                //读取客户端数据后再写回客户端
                Arrays.asList(buffers).forEach(Buffer::flip);

                int bytesWrite = 0;
                for (long w = socketChannel.write(buffers); bytesWrite < messageLength; w = socketChannel.write(buffers)) {
                    bytesWrite += w;
                }

                Arrays.asList(buffers).forEach(Buffer::clear);

                System.out.println("bytesRead: " + bytesRead + ", bytesWritten: " + bytesWrite + ", messageLength: " + messageLength);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void demo6() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("src/resources/file/input.txt", "rw")) {
            FileChannel fileChannel = randomAccessFile.getChannel();

            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

            mappedByteBuffer.put(0, (byte) 'a');
            mappedByteBuffer.put(3, (byte) 'b');

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void demo5() {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        for (int i = 0; i < buffer.capacity(); ++i) {
            buffer.put((byte) i);
        }

        buffer.position(2);
        buffer.limit(6);

        ByteBuffer sliceBuffer = buffer.slice();
        for (int i = 0; i < sliceBuffer.capacity(); ++i) {
            byte b = sliceBuffer.get(i);
            b *= 2;
            sliceBuffer.put(i, b);
        }

        buffer.position(0);
        buffer.limit(buffer.capacity());

        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
    }

    private static void demo4() {
        try (FileInputStream inputStream = new FileInputStream("src/resources/file/input.txt");
             FileOutputStream outputStream = new FileOutputStream("src/resources/file/output.txt")) {

            FileChannel inputChannel = inputStream.getChannel();
            FileChannel outputChannel = outputStream.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1);
            for (int len = inputChannel.read(buffer); len != -1; len = inputChannel.read(buffer)) {
                buffer.flip();
                outputChannel.write(buffer);
                buffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void demo3() {
        try (FileOutputStream fou = new FileOutputStream("src/resources/file/demo.txt")) {
            FileChannel fileChannel = fou.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte[] message = "hello java".getBytes();

            for (int i = 0; i < message.length; i++) {
                buffer.put(message[i]);
            }

            buffer.flip();
            fileChannel.write(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void demo2() {
        try (FileInputStream fileInputStream = new FileInputStream("src/resources/file/demo.txt")) {
            FileChannel fileChannel = fileInputStream.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            fileChannel.read(buffer);
            buffer.flip();

            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                System.out.println((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void demo1() {
        IntBuffer buffer = IntBuffer.allocate(10);
        for (int i = 0; i < buffer.capacity(); i++) {
            int number = new SecureRandom().nextInt(20);
            buffer.put(number);
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
    }

}
