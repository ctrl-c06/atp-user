package com.atpuser.Helpers;

public class PinGenerator {
    public static String generate()
    {
        int randomPIN = (int) ( Math.random() * 999999) + 100000;
        return String.valueOf(randomPIN);
    }
}
