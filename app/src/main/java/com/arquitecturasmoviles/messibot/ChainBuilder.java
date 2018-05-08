package com.arquitecturasmoviles.messibot;

public class ChainBuilder {

    public static final int START_CHAIN = 0x7E;

    public byte[] makeChain(int frameType, byte[] data) {
        byte lbs = 0x0; //TODO: LBS function
        byte mbs = 0x0; //TODO: MBS function

        byte[] chain = {START_CHAIN, lbs, mbs };

        for (int i = 0; i <= data.length; i++) {
            chain[chain.length + 1] = data[i];
        }

        int checkSum = 0xFF; //TODO: function check sum
        chain[chain.length + 1] = (byte) checkSum;

        return chain;
    }
}
