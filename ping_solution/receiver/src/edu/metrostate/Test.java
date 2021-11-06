package edu.metrostate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Test {
    public static void main(String[] args) throws IOException {
        String test = "test";
        byte[] bytes = test.getBytes();
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        // System.out.println(crc32.getValue());
        // String b = Integer.toBinaryString(8);
        // System.out.println(b);
        short num = 23;
        short nob = (short) ((Math.floor(Math.log(num) / Math.log(2))) + 1);
        // System.out.println(Math.log(8) / Math.log(2));
        // System.out.println(nob);
        // int an = ((1 << nob) - 1)^ num;
        short an = (short) (((1 << nob) - 1) ^ num);
        System.out.println(an);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fis = new FileInputStream("panda.jpg");
            bis = new BufferedInputStream(fis);
            // int i = 0;
            // while (bis.available() > 0) {
            // i++;
            // // System.out.print((char) bis.read());
            // bis.read();
            // }
            // System.out.println("i = " + i);
            fos = new FileOutputStream("panda2.jpg");
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[12345];
            int count = 0;
            int t = 0;
            // while ((count = bis.read(b)) != -1) {
            // System.out.println("count= " + count);
            // t += count;
            // bos.write(b);
            // bos.flush();
            // }
            // System.out.println("t= " + t);

            while ((count = fis.read(b)) != -1) {
                if (count < b.length)
                    break;
                System.out.println("count= " + count);
                System.out.println("b.length= " + b.length);
                // if (count < 10) {
                // fos.write(b, 0, 6);
                // fos.flush();
                // continue;
                // }
                t += count;
                fos.write(b, 0, b.length);
                fos.flush();
            }
            System.out.println("t= " + t);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fis != null)
                fis.close();
            if (bis != null)
                bis.close();
            if (fos != null)
                fos.close();
            if (bos != null)
                bos.close();
        }

    }
}
