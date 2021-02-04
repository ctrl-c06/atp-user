package com.atpuser.Helpers;


public class StringToASCII {
    public static String convert(String data)
    {
        StringBuilder sb = new StringBuilder();
        char[] letters = data.toCharArray();
        for (char ch : letters) {
            sb.append((byte) ch + ":");
        }
        return String.valueOf(sb);
    }
}
