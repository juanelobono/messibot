package com.arquitecturasmoviles.messibot;

public class ChainBuilder {

    public static final int START_CHAIN = 0x7E;
    private CheckSum checkSum;
    public ChainBuilder() {
        this.checkSum = new CheckSum();
    }

    public byte[] makeChain(int frameType, byte[] data) {
        byte[] mbsLbs = BytesUtility.getMSBandLSB(data);

        byte[] chain = {START_CHAIN, mbsLbs[0], mbsLbs[1] };

        for (int i = 0; i <= data.length; i++) {
            chain[chain.length + 1] = data[i];
        }

        data[data.length + 1] = (byte) frameType;

        int checkSum = this.checkSum.CalculateCheckSum(data);

        chain[chain.length + 1] = (byte) checkSum;

        return chain;
    }
}
