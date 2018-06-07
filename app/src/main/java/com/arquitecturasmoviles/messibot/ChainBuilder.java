package com.arquitecturasmoviles.messibot;

import android.util.ArraySet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ChainBuilder {

    public static final int START_CHAIN = 126;
    private CheckSum checkSum;
    public ChainBuilder() {
        this.checkSum = new CheckSum();
    }

    public List<Integer> makeChain(int frameType, int[] data) {
        int[] dataCheckSum = new int[data.length + 1];

        for (int i = 0; i < data.length; i++) {
            dataCheckSum[i] = data[i];
        }

        int w = dataCheckSum.length - 1;
        dataCheckSum[w] = frameType;
        int[] mbsLbs = BytesUtility.getMSBandLSB(data);

        List<Integer> chain2 = new ArrayList<>();
        chain2.add(START_CHAIN);
        chain2.add(mbsLbs[0]);
        chain2.add(mbsLbs[1]);
        chain2.add(frameType);

        for (int i = 0; i < data.length; i++) {
            chain2.add(data[i]);
        }

        int checkSum = this.checkSum.CalculateCheckSum(dataCheckSum);

        chain2.add(checkSum);

        return chain2;
    }
}
