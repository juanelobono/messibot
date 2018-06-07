package com.arquitecturasmoviles.messibot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickActivity extends Activity {

    private final int MAX_STRENGTH = 255;
    private final int MIN_STRENGTH = 0;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private enum DIRECTION {
        LEFT,
        RIGHT,
        BOTTOM,
        TOP,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionService mBluetoothConnection;
    BluetoothSocket socket;
    BluetoothDevice mBTDevice;
    private TextView tvDirection;
    private TextView tvAngle;
    private TextView tvStrength;
    private SendDataService sendDataService;
    private ChainBuilder chainBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final JoystickView joystick = findViewById(R.id.joystick);

        tvDirection = findViewById(R.id.tvDirection);
        tvAngle = findViewById(R.id.tvAngle);
        tvStrength = findViewById(R.id.tvStrength);

        /*
         * final byte[] tramaAdelante = new byte[]{(byte)0x7e, (byte)0x00,(byte)0x02, (byte)0x04, (byte)0xFB, (byte)0x00};
         * final byte[] tramaAtras = new byte[]{(byte)0x7e, (byte)0x00,(byte)0x02, (byte)0x01, (byte)0xFE, (byte)0x00};
         * final byte[] tramaIzquieda = new byte[]{(byte)0x7e, (byte)0x00, (byte)0x02,(byte)0x02, (byte)0xFD, (byte)0x00};
         * final byte[] tramaDerecha = new byte[]{(byte)0x7e, (byte)0x00,(byte)0x02, (byte)0x03, (byte)0xFC, (byte)0x00};
         */

        mBTDevice = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        Toast.makeText(JoystickActivity.this, "Bluetooth conectado: " + mBTDevice.getName(),
                Toast.LENGTH_LONG).show();

        try {
            sendDataService = new SendDataService(mBTDevice);
        } catch (IOException e) {
            e.printStackTrace();
        }

        joystick.setOnMoveListener(
                new JoystickView.OnMoveListener() {
                    @Override
                    public void onMove(int angle, int strength) {
                        tvDirection.setText(getButtonDirection(angle).toString());

                        chainBuilder = new ChainBuilder();
                        List<Integer> chainData;

                        try {
                            switch(getButtonDirection(angle)) {
                                case TOP:
                                    int[] strengthArrayTop = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.PROGRESS.getValue(), strengthArrayTop);
                                    int[] dataArrayIntegerForTopChain = convertIntegers(chainData);
                                    byte[] dataToByteTop = getChainTransform(dataArrayIntegerForTopChain);

                                    sendDataService.write(dataToByteTop);
                                    break;
                                case LEFT:
                                    int[] strengthArrayLeft = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.LEFT.getValue(), strengthArrayLeft);
                                    int[] dataArrayIntegerForLeftChain = convertIntegers(chainData);
                                    byte[] dataToByteLeft = getChainTransform(dataArrayIntegerForLeftChain);

                                    sendDataService.write(dataToByteLeft);
                                    break;
                                case RIGHT:
                                    int[] strengthArrayRight = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.RIGHT.getValue(), strengthArrayRight);
                                    int[] dataArrayIntegerForRightChain = convertIntegers(chainData);
                                    byte[] dataToByteRight = getChainTransform(dataArrayIntegerForRightChain);

                                    sendDataService.write(dataToByteRight);
                                    break;
                                case BOTTOM:
                                    int[] strengthArrayBack = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.BACK.getValue(), strengthArrayBack);
                                    int[] dataArrayIntegerForBackChain = convertIntegers(chainData);
                                    byte[] dataToByteBack = getChainTransform(dataArrayIntegerForBackChain);

                                    sendDataService.write(dataToByteBack);
                                    break;
                                case TOP_LEFT:
                                    break;
                                case TOP_RIGHT:
                                    break;
                                case BOTTOM_LEFT:
                                    break;
                                case BOTTOM_RIGHT:
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        tvAngle.setText(angle + "º");
                        tvStrength.setText(getStrengthToDecimal(strength) + " en decimal.");
                    }
                },
                500
        );
    }

    private static int[] convertIntegers(List<Integer> chain)
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

    public int getStrengthToDecimal(int strength){
        return (int) Math.ceil((strength * MAX_STRENGTH) / 100);
    }

    private DIRECTION getButtonDirection (int angle){
        DIRECTION direction = null;
        if (angle == 0){
            direction = DIRECTION.RIGHT;
        }else if(angle > 0 && angle < 90) {
            direction = DIRECTION.TOP;
        }else if(angle > 90 && angle < 180 ){
            direction = DIRECTION.TOP_LEFT;
        }else if(angle  == 180 ){
            direction = DIRECTION.LEFT;
        }else if(angle > 180 && angle < 270 ){
            direction = DIRECTION.BOTTOM_LEFT;
        }else if(angle == 270 ){
            direction = DIRECTION.BOTTOM;
        }else if(angle > 270 && angle < 360 ){
            direction = DIRECTION.BOTTOM_RIGHT;
        }

        return direction;
    }
}