package com.atpuser.Helpers;

public class PinGenerator {
    public static String generate()
    {
        int randomPIN = (int) ( Math.random() * 900000 ) + 100000;
        return String.valueOf(randomPIN);
    }
}
