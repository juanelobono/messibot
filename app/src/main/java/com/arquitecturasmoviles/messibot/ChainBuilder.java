package com.arquitecturasmoviles.messibot;

import android.util.ArraySet;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChainBuilder {

    public static final int START_CHAIN = 0x7E;
    private CheckSum checkSum;
    public ChainBuilder() {
        this.checkSum = new CheckSum();
    }

    public byte[] makeChain(int frameType, byte[] data) {
        byte[] mbsLbs = BytesUtility.getMSBandLSB(data);

        byte[] chain = new byte[] {START_CHAIN, mbsLbs[0], mbsLbs[1] };
        ArrayList<Byte> chain2 = new ArrayList<Byte>();
        chain2.add((byte)START_CHAIN);
        chain2.add(mbsLbs[0]);
        chain2.add(mbsLbs[1]);
        chain2.add((byte)frameType);

        for (int i = 0; i < data.length; i++) {
            chain2.add(data[i]);
        }

        int checkSum = this.checkSum.CalculateCheckSum(data);

        chain2.add((byte) checkSum);

        return chain;
    }
}
