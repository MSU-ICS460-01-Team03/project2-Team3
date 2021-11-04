package edu.metrostate;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Test {
    public static void main(String[] args) {
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
    }
}
