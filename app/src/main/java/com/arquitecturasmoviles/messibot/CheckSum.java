package com.arquitecturasmoviles.messibot;

public class CheckSum {
    int CalculateCheckSum(byte[] bytes) {
        int CheckSum = 0, i = 0;

        for( i = 0; i < bytes.length; i++){
            CheckSum += bytes[i];
        }

        if (CheckSum > 255) {
            CheckSum = 255 - (CheckSum & 0xFF);
            return CheckSum;
        }

        return 255 - CheckSum;
    }
}
