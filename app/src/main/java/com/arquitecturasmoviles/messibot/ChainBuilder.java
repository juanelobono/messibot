package com.arquitecturasmoviles.messibot;

import java.util.ArrayList;
import java.util.List;

public class ChainBuilder {

    private static final int START_CHAIN = 126;
    private CheckSum checkSum;

    ChainBuilder() {
        this.checkSum = new CheckSum();
    }

    public byte[] makeChain(int frameType, int[] data) {
        int[] dataCheckSum = new int[data.length + 1];

        System.arraycopy(data, 0, dataCheckSum, 0, data.length);

        int w = dataCheckSum.length - 1;
        dataCheckSum[w] = frameType;
        int[] mbsLbs = BytesUtility.getMSBandLSB(data);

        List<Integer> chain2 = new ArrayList<>();
        chain2.add(START_CHAIN);
        chain2.add(mbsLbs[0]);
        chain2.add(mbsLbs[1]);
        chain2.add(frameType);

        for (int aData : data) {
            chain2.add(aData);
        }

        int checkSum = this.checkSum.CalculateCheckSum(dataCheckSum);

        chain2.add(checkSum);

        return convertIntChainToBytes(chain2);
    }

    private byte[] convertIntChainToBytes(List<Integer> originalChain)
    {
        int[] integerChain = convertIntegers(originalChain);

        return getChainTransform(integerChain);
    }

    private int[] convertIntegers(List<Integer> chain)
    {
        int[] ret = new int[chain.size()];

        for (int i=0; i < ret.length; i++) {
            ret[i] = chain.get(i);
        }

        return ret;
    }

    private byte[] getChainTransform(int[] chain)
    {
        byte[] newChainOfbytes = new byte[chain.length];

        for(int i = 0; i < chain.length ; i++) {
            newChainOfbytes[i] = (byte) chain[i];
        }

        return  newChainOfbytes;
    }
}
