package nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;

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
        demo5();
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
