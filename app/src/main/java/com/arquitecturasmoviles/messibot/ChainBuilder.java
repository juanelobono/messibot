package com.arquitecturasmoviles.messibot;

public class ChainBuilder {

    public static final int START_CHAIN = 0x7E;

    public byte[] makeChain(int frameType, byte[] data) {
        byte[] mbsLbs = BytesUtility.getMSBandLSB(data);

        byte[] chain = {START_CHAIN, mbsLbs[0], mbsLbs[1] };

        for (int i = 0; i <= data.length; i++) {
            chain[chain.length + 1] = data[i];
        }

        int checkSum = 0xFF; //TODO: function check sum
        chain[chain.length + 1] = (byte) checkSum;

        return chain;
    }
}
