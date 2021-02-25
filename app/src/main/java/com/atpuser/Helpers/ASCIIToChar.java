package com.atpuser.Helpers;

import java.util.ArrayList;
import java.util.List;

public class ASCIIToChar {
    // |
    private final static String BASE_SEPERATOR = "124";
    private final static String CHAR_SEPERATOR = ":";

    public static List<String> convert(String data)
    {
        List<String> information = new ArrayList<>();

        String decoded = "";

        String[] splitData = data.split(BASE_SEPERATOR);

        for(String new_data : splitData) {
            String character[] = new_data.split(CHAR_SEPERATOR);

            decoded = "";

            for(String c : character) {
                if(!c.isEmpty()) {
                    char charString = (char) Integer.parseInt(c);
                    decoded  += charString;
                }
            }
            information.add(decoded);
        }

        return information;

    }
}
