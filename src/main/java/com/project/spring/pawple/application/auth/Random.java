package com.project.spring.pawple.application.auth;

public final class Random {

    private Random() {}

    public static long getRandomNumber(long min, long max) {
        return min + (long)(Math.random() * ((max - min) + 1L));
    }

    public static String getRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        int charsLength = chars.length();
        for(int i = 0; i < length; i++) {
            int index = (int)(Math.random() * charsLength);
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

}
