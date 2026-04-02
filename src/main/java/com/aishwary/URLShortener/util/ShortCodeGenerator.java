package com.aishwary.URLShortener.util;

import java.util.Random;

public class ShortCodeGenerator {
    private static final String CHAR_SET = "abcdefghijklmnopqrstuvwxyz";

    public static String generateCode() {
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int ind = rand.nextInt(CHAR_SET.length());
            code.append(CHAR_SET.charAt(ind));
        }
        return code.toString();
    }
}
