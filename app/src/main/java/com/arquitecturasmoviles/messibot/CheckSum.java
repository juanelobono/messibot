package com.arquitecturasmoviles.messibot;

public class CheckSum {
    int CalculateCheckSum(int[] bytes) {
        int checkSum = 0, check;

        for (int aByte : bytes) {
            checkSum += aByte;
        }

        if (checkSum > 255) {
            checkSum = 255 - (checkSum & 0xFF);
            check = checkSum;
        } else {
            check = 255 - checkSum;
        }

        return check;
    }
}
