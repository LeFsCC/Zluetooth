package com.app.zluetooth.Utils;


import com.app.zluetooth.Exception.ZlueToothException;

import java.util.ArrayList;

// 把字符串转为比特序列，16进制
public class StringAndBinary {
    private String src;
    private int[] b;
    private int bits;
    private static ArrayList<Integer> demodulated;

//    得到src二进制序列
    public StringAndBinary(String src) {
        this.src = src;
        this.bits = RigidData.number_of_carriers;
        generate();
    }

    public StringAndBinary(ArrayList<Integer> demodulated) {
        this.demodulated = demodulated;
        this.bits = RigidData.number_of_carriers;
    }

    public void generate() {
        String myBinary = toBinary();
        b = new int[myBinary.length()];
        for(int i=0;i<myBinary.length();i++){
            b[i]=Integer.parseInt(String.valueOf(myBinary.charAt(i)));
        }
    }

    public String toBinary() {
        String src=this.src;
        String result = "";
        String charBinary = "";

        for (int i = 0; i < src.length(); i++) {
            charBinary = Integer.toBinaryString(src.charAt(i));//遍历每一个char，得到二进制串
            int binaryLength = charBinary.length();
            if(binaryLength>8){
                throw new ZlueToothException("8位无法表达！");
            }
            else{
                while (charBinary.length()<8){
                    charBinary="0"+charBinary;
                }
                result+=charBinary;

            }
        }
        return result;
    }

    public String getString() {
        String bin = arToString();
        StringBuilder b = new StringBuilder();
        int len = bin.length();
        int i = 0;
        while (i + RigidData.number_of_carriers <= len) {
            char c = convert(bin.substring(i, i + RigidData.number_of_carriers));
            i += RigidData.number_of_carriers;
            b.append(c);
        }
        return b.toString();
    }

    private char convert(String bs) {
        return (char) Integer.parseInt(bs, 2);
    }

    public String arToString() {
        String r = "";
        for (int i : demodulated) {
            r +=i+"";

        }
        return r;
    }

    public int[] getB() {
        return b;
    }
}
