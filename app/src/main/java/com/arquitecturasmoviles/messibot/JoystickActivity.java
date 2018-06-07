package com.arquitecturasmoviles.messibot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
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
    private ChainBuilder cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final JoystickView joystick = findViewById(R.id.joystick);

        tvDirection = findViewById(R.id.tvDirection);
        tvAngle = findViewById(R.id.tvAngle);
        tvStrength = findViewById(R.id.tvStrength);

        final byte[] tramaIzquieda = new byte[]{(byte)0x7e, (byte)0x00, (byte)0x02,(byte)0x02, (byte)0xFD, (byte)0x00};
        final byte[] tramaAtras = new byte[]{(byte)0x7e, (byte)0x00,(byte)0x02, (byte)0x01, (byte)0xFE, (byte)0x00};
        final byte[] tramaDerecha = new byte[]{(byte)0x7e, (byte)0x00,(byte)0x02, (byte)0x03, (byte)0xFC, (byte)0x00};
        final byte[] tramaAdelante = new byte[]{(byte)0x7e, (byte)0x00,(byte)0x02, (byte)0x04, (byte)0xFB, (byte)0x00};
        byte[] tramaDetener = new byte[]{(byte)0x7e, (byte)0x00,(byte)0x02, (byte)0x04, (byte)0xFB, (byte)0x00};

        mBTDevice = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        Toast.makeText(JoystickActivity.this, "Bluetooth conectado: " + mBTDevice.getName(),
                Toast.LENGTH_LONG).show();

        try {
            sendDataService = new SendDataService(mBTDevice);

        } catch (IOException e) {
            e.printStackTrace();
        }

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                tvDirection.setText(getButtonDirection(angle).toString());
                cb = new ChainBuilder();
                List<Integer> data = null;
                try{
                    switch(getButtonDirection(angle))
                    {
                        case TOP:
                            int[] strengArrayTop = new int[] {strength};
                            ArrayList<String> arrayBytesTop = new ArrayList<>();

                            data = cb.makeChain(FrameType.PROGRESS.getValue(), strengArrayTop);
                            int[] dataArrayIntegerTop = convertIntegers(data);
                            byte[] dataToByteTop = getByteArray(dataArrayIntegerTop);

                            sendDataService.write(dataToByteTop);
                            break;
                        case LEFT:
                            int[] strengArrayLeft = new int[] {strength};
                            ArrayList<String> arrayBytesLeft = new ArrayList<>();

                            data = cb.makeChain(FrameType.LEFT.getValue(), strengArrayLeft);
                            int[] dataArrayIntegerLeft = convertIntegers(data);
                            byte[] dataToByteLeft = getByteArray(dataArrayIntegerLeft);

                            sendDataService.write(dataToByteLeft);
                            break;
                        case RIGHT:
                            int[] strengArrayRigth = new int[] {strength};
                            ArrayList<String> arrayBytesRigth = new ArrayList<>();

                            data = cb.makeChain(FrameType.RIGHT.getValue(), strengArrayRigth);
                            int[] dataArrayIntegerRigth = convertIntegers(data);
                            byte[] dataToByteRigth = getByteArray(dataArrayIntegerRigth);

                            sendDataService.write(dataToByteRigth);
                            break;
                        case BOTTOM:
                            int[] strengArrayBack = new int[] {strength};

                            data = cb.makeChain(FrameType.BACK.getValue(), strengArrayBack);
                            int[] dataArrayIntegerBack = convertIntegers(data);
                            byte[] dataToByteBack = getByteArray(dataArrayIntegerBack);

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

                }catch (IOException e){

                    e.printStackTrace();
                }

                tvAngle.setText(angle + "º");
                tvStrength.setText(getStrengthToDecimal(strength) + " en decimal.");
            }
        }, 500);

    }

    private static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    private static byte[] convertToByte(int[] ints)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(ints.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(ints);

        byte[] arrayBytes = byteBuffer.array();

        return arrayBytes;
    }


    public byte[] getByteArray(int[] array) {
        byte[] bytes = new byte[array.length];

        for(int i = 0; i < array.length ; i++) {
            bytes[i] =(byte) array[i];
        }

        return  bytes;
    }

    public static int convert(int number)
    {
        return Integer.valueOf(String.valueOf(number), 16);
    }

    public int getStrengthToDecimal(int strength){
        //MAX_STRENGTH = 100%
        //MIN_STRENGTH = 0&
        //Se obtiene el decimal de 0 a 255 por regla de 3 simple.
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