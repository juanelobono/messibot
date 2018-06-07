package com.arquitecturasmoviles.messibot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import io.github.controlwear.virtual.joystick.android.JoystickView;
import static android.content.ContentValues.TAG;

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
        BOTTOM_RIGHT,
        CENTER
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                        tvDirection.setText(getButtonDirection(angle, strength).toString());

                        chainBuilder = new ChainBuilder();
                        List<Integer> chainData;

                        try {
                            switch(getButtonDirection(angle, strength)) {
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
                                    int[] strengthArrayTopLeft = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.PROGRESS.getValue(), strengthArrayTopLeft);
                                    int[] dataArrayIntegerForTopLeftChain = convertIntegers(chainData);
                                    byte[] dataToByteTopLeft = getChainTransform(dataArrayIntegerForTopLeftChain);

                                    sendDataService.write(dataToByteTopLeft);

                                    chainData = chainBuilder.makeChain(FrameType.LEFT.getValue(), strengthArrayTopLeft);
                                    dataArrayIntegerForBackChain = convertIntegers(chainData);
                                    dataToByteBack = getChainTransform(dataArrayIntegerForBackChain);

                                    sendDataService.write(dataToByteBack);
                                    break;
                                case TOP_RIGHT:
                                    int[] strengthArrayTopRight = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.PROGRESS.getValue(), strengthArrayTopRight);
                                    int[] dataArrayIntegerForTopRightChain = convertIntegers(chainData);
                                    byte[] dataToByteTopRight = getChainTransform(dataArrayIntegerForTopRightChain);

                                    sendDataService.write(dataToByteTopRight);

                                    chainData = chainBuilder.makeChain(FrameType.RIGHT.getValue(), strengthArrayTopRight);
                                    dataArrayIntegerForTopRightChain = convertIntegers(chainData);
                                    dataToByteTopRight = getChainTransform(dataArrayIntegerForTopRightChain);

                                    sendDataService.write(dataToByteTopRight);
                                    break;
                                case BOTTOM_LEFT:
                                    int[] strengthArrayBottomLeft = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.BACK.getValue(), strengthArrayBottomLeft);
                                    int[] dataArrayIntegerForBottomLeftChain = convertIntegers(chainData);
                                    byte[] dataToByteBottomLeft = getChainTransform(dataArrayIntegerForBottomLeftChain);

                                    sendDataService.write(dataToByteBottomLeft);

                                    chainData = chainBuilder.makeChain(FrameType.LEFT.getValue(), strengthArrayBottomLeft);
                                    dataArrayIntegerForBottomLeftChain = convertIntegers(chainData);
                                    dataToByteBottomLeft = getChainTransform(dataArrayIntegerForBottomLeftChain);

                                    sendDataService.write(dataToByteBottomLeft);
                                    break;
                                case BOTTOM_RIGHT:
                                    int[] strengthArrayBottomRight = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.BACK.getValue(), strengthArrayBottomRight);
                                    int[] dataArrayIntegerForBottomRightChain = convertIntegers(chainData);
                                    byte[] dataToByteBottomRight = getChainTransform(dataArrayIntegerForBottomRightChain);

                                    sendDataService.write(dataToByteBottomRight);

                                    chainData = chainBuilder.makeChain(FrameType.RIGHT.getValue(), strengthArrayBottomRight);
                                    dataArrayIntegerForBottomRightChain = convertIntegers(chainData);
                                    dataToByteBottomRight = getChainTransform(dataArrayIntegerForBottomRightChain);

                                    sendDataService.write(dataToByteBottomRight);
                                    break;
                                case CENTER:
                                    int[] strengthCenter = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.STOP.getValue(), strengthCenter);
                                    int[] dataArrayIntegerForStopChain = convertIntegers(chainData);
                                    byte[] dataToByteStop = getChainTransform(dataArrayIntegerForStopChain);

                                    sendDataService.write(dataToByteStop);
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        tvAngle.setText(angle + "º");
                        tvStrength.setText(getStrengthToDecimal(strength) + " en decimal.");
                    }
                },
                150
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

    private int getStrengthToDecimal(int strength)
    {
        return (int) Math.ceil((strength * MAX_STRENGTH) / 100);
    }

    private DIRECTION getButtonDirection(int angle, int strength)
    {
        DIRECTION direction = null;

        if (angle >= 0 && angle <= 360 && strength == 0) {
            direction = DIRECTION.CENTER;
        }

        if ((angle >= 0 && angle <= 30) || angle >= 330) {
            direction = DIRECTION.RIGHT;
        } else if (angle > 30 && angle < 60) {
            direction = DIRECTION.TOP_RIGHT;
        } else if (angle >= 60 && angle <= 120) {
            direction = DIRECTION.TOP;
        } else if (angle > 120 && angle < 150) {
            direction = DIRECTION.TOP_LEFT;
        } else if (angle >= 150 && angle <= 210) {
            direction = DIRECTION.LEFT;
        } else if (angle > 210 && angle < 240) {
            direction = DIRECTION.BOTTOM_LEFT;
        } else if (angle >= 240 && angle <= 300) {
            direction = DIRECTION.BOTTOM;
        } else if (angle > 300) {
            direction = DIRECTION.BOTTOM_RIGHT;
        }

        return direction;
    }

    /*
     *  Esta clase está destinada a utilizarse de cronómetro para la terminación automática y asíncrona
     *  de conexiones bluetooth dados los milisegundos requeridos para que esta tarea se lleve a cabo.
     */
    public final class Reminder {
        Timer timer;

        Reminder(int milliseconds) {
            timer = new Timer();
            timer.schedule(new RemindTask(), milliseconds);
        }

        class RemindTask extends TimerTask {
            public void run() {

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        try {
                            Method m = device.getClass()
                                    .getMethod("removeBond", (Class[]) null);
                            m.invoke(device, (Object[]) null);
                            Log.d(TAG, "Conexiones terminadas.");
                        } catch (Exception e) {
                            Log.e("Error terminación.", e.getMessage());
                        }
                    }
                }

                timer.cancel();
            }
        }
    }

    /*
     *  Este método permite desvincular las conexiones bluetooth pasada cierta cantidad de milisegundos
     */
    protected void terminateBluetoothConnection(int milliseconds)
    {
        Reminder reminder = new Reminder(milliseconds);
        Toast.makeText(JoystickActivity.this, "Se desvincularán todos los dispositivos en " + milliseconds / 1000 + " segundos.", Toast.LENGTH_SHORT).show();
    }
}
