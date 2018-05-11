package com.arquitecturasmoviles.messibot;

public class CheckSum {
    String CalculateCheckSum( byte[] bytes ){
        short CheckSum = 0, i = 0;

        for( i = 0; i < bytes.length; i++){
            CheckSum += (short)(bytes[i] & 0xFF);
        }

        return Integer.toHexString(CheckSum);
    }

    String CalculateCheckSum( Integer[] bytes ){
        Integer CheckSum = 0, i = 0;

        for( i = 0; i < bytes.length; i++ ){
            CheckSum += bytes[i];
        }

        return Integer.toHexString(CheckSum);
    }
}
