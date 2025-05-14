package com.project.spring.skillstack.utility;

public final class Random {

    private Random() {}

    // 숫자 랜덤: 최소값(min)과 최대값(max)을 받아 그 사이의 랜덤한 정수를 반환하는 메서드
    public static long getRandomNumber(long min, long max) {
        return min + (long)(Math.random() * ((max - min) + 1L));
    }

    // 문자열 랜덤: 지정된 길이(length)를 받아 파일명으로 사용할 수 있는 문자로만 이루어진 랜덤 문자열을 반환하는 메서드
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
