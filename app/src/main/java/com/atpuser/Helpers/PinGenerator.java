package com.atpuser.Helpers;

public class PinGenerator {
    public static String generate()
    {
        int randomPIN = (int) ( Math.random() * 998) + 100;
        return String.valueOf(randomPIN);
    }
}
