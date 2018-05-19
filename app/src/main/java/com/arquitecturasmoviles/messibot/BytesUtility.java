package com.arquitecturasmoviles.messibot;

public final class BytesUtility {
    private BytesUtility(){

    }

    public static byte[] getMSBandLSB(byte[] data){
        int length = data.length;
        byte[] Bytes = new byte[2];
        Bytes[1] = (byte) (length & 0xFF);           // Least Significant Byte
        Bytes[0] = (byte) ((length & 0xFF00) >> 8);  // Most Significan Byte
        return Bytes;
    }
}
