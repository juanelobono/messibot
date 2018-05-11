package com.arquitecturasmoviles.messibot;

public class CheckSum {
    int CalculateCheckSum( byte[] bytes ){
        int CheckSum = 0, i = 0;

        for( i = 0; i < bytes.length; i++){
            CheckSum += (bytes[i] & 0xFF);
        }

        return CheckSum;
    }

    String CalculateCheckSum( Integer[] bytes ){
        Integer CheckSum = 0, i = 0;

        for( i = 0; i < bytes.length; i++ ){
            CheckSum += bytes[i];
        }

        return Integer.toHexString(CheckSum);
    }
}
