package com.casemgr.utils;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Base62Utils {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(62);

    public static String encode(String input) {
        // 转换为字节数组
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        // 构造正的 BigInteger（确保不会出现负数）
        BigInteger num = new BigInteger(1, bytes);
        StringBuilder sb = new StringBuilder();
        // 如果数值为 0，直接返回字符集第一个字符（"0"）
        if (num.equals(BigInteger.ZERO)) {
            return String.valueOf(ALPHABET.charAt(0));
        }
        // 依次取余并除以 BASE
        while (num.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = num.divideAndRemainder(BASE);
            num = divRem[0];
            int remainder = divRem[1].intValue();
            sb.append(ALPHABET.charAt(remainder));
        }
        // 余数的顺序是逆序的，需要反转
        return sb.reverse().toString();
    }

    public static String decode(String base62) {
        BigInteger num = BigInteger.ZERO;
        for (int i = 0; i < base62.length(); i++) {
            int index = ALPHABET.indexOf(base62.charAt(i));
            if (index < 0) {
                throw new IllegalArgumentException("Not valid Base62 char：" + base62.charAt(i));
            }
            num = num.multiply(BASE).add(BigInteger.valueOf(index));
        }
        // 将大整数还原为字节数组
        byte[] bytes = num.toByteArray();
        // 如果由于符号位产生了额外的 0 字节，则需要去掉
        if (bytes.length > 1 && bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
