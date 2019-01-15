package io;

import java.io.*;

public class SimpleIO {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
//        PicCopy();
        WriteToFile();
    }

    /**
     * 复制一个图片
     */
    public static void PicCopy() {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("src/resources/images/miku.jpg"));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("src/resources/images/mikucopy.jpg"))) {

            byte[] buf = new byte[1024];
            for (int len = bis.read(buf); len != -1; len = bis.read(buf)) {
                bos.write(buf, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存键盘录入
     */
    public static void WriteToFile() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/resources/file/demo.txt", true)))) {

            for (String line = br.readLine(); !"exit".equals(line); line = br.readLine()) {
                bw.write(line);
                bw.newLine();
                bw.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
