package com.arquitecturasmoviles.messibot;

import android.widget.Toast;

public class CheckSum {
    int CalculateCheckSum(int[] bytes) {
        int checkSum = 0, check = 0;

        for(int i = 0; i < bytes.length; i++){
            checkSum += bytes[i];
        }

        if (checkSum > 255) {
            checkSum = 255 - (checkSum & 0xFF);
            check = checkSum;
        } else {
            check = 255 - checkSum;
        }

        return check;
    }

    public static int convert(int number)
    {
        return Integer.valueOf(String.valueOf(number), 16);
    }
}
