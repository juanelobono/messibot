package com.arquitecturasmoviles.messibot;

public final class BytesUtility {
    private BytesUtility(){

    }

    public static int[] getMSBandLSB(int[] data){
        int length = data.length + 1;
        int[] Bytes = new int[2];
        Bytes[1] = (length & 0xFF);           // Least Significant Byte
        Bytes[0] = ((length & 0xFF00) >> 8);  // Most Significan Byte
        return Bytes;
    }
}
