package com.app.zluetooth.Utils;


import java.util.ArrayList;

// 把字符串转为比特序列，16进制
public class StringHanlder {
    private String src;
    private int[] b;
    private int bits;

    private static ArrayList<Integer> demodulated;

//    得到src二进制序列
    public StringHanlder(String src) { //Constructor For Getting Binary Sequence
        this.src = src;
        this.bits = RigidData.number_of_carriers;
        generate();
    }

    public StringHanlder(ArrayList<Integer> demodulated) {
        this.demodulated = demodulated;
        this.bits = RigidData.number_of_carriers;
    }

    public void generate() {
        String temp = toBinary();
        System.out.println("This is Binary String temp: " + temp);
        System.out.println("This is the size of  Binary String temp: " + temp.length());
        b = new int[temp.length()];
        for (int i = 0; i < temp.length() - 1; i++) {
            b[i] = Integer.parseInt(temp.substring(i, i + 1));
        }
    }

    public String toBinary() {
        String result = "";
        String tmpStr;
        int tmpInt;
        System.out.println("This is String temp: " + src);
        char[] messChar = src.toCharArray();//转为char序列
        System.out.println("This is messChar: " + messChar);

        for (int i = 0; i < messChar.length; i++) {
            tmpStr = Integer.toBinaryString(messChar[i]);//遍历每一个char，得到二进制串
            tmpInt = tmpStr.length();//获得长度
            if (tmpInt != bits) {//不等于16位
                tmpInt = bits - tmpInt;
                if (tmpInt == bits) {//0位
                    result += tmpStr;
                } else if (tmpInt > 0) {//不足16位前面补0
                    for (int j = 0; j < tmpInt; j++) {
                        result += "0";
                    }
                    result += tmpStr;
                } else {//超过16位
                    System.err.println("argument 'bits' is too small");
                }
            } else {//刚好16位
                result += tmpStr;
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
