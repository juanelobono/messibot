package com.arquitecturasmoviles.messibot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
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
                byte[] data = null;
                try{
                    switch(getButtonDirection(angle))
                    {
                        case TOP:
                            break;
                        case LEFT:
                            break;
                        case RIGHT:
                            break;
                        case BOTTOM:

                            break;
                        case TOP_LEFT:
                            sendDataService.write(tramaIzquieda);
                            break;
                        case TOP_RIGHT:
                            sendDataService.write(tramaDerecha);
                            break;
                        case BOTTOM_LEFT:
                            data = cb.makeChain(FrameType.CHANGE_PASSWORD.getValue(), HexUtility.convertIntegerToHexadecimal(strength).getBytes());
                          sendDataService.write(data);
                            break;
                        case BOTTOM_RIGHT:
//                            sendDataService.write(tramaAdelante);
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