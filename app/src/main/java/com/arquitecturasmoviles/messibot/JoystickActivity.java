package com.arquitecturasmoviles.messibot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickActivity extends Activity {

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
    BluetoothDevice mBTDevice;
    private TextView tvDirection;
    private TextView tvAngle;
    private TextView tvStrength;
    private SendDataService sendDataService;
    private ChainBuilder chainBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
                        byte[] chainData;

                        try {
                            switch(getButtonDirection(angle, strength)) {
                                case TOP:
                                    int[] strengthArrayTop = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.PROGRESS.getValue(), strengthArrayTop);
                                    sendDataService.write(chainData);
                                    break;
                                case LEFT:
                                    int[] strengthArrayLeft = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.LEFT.getValue(), strengthArrayLeft);
                                    sendDataService.write(chainData);
                                    break;
                                case RIGHT:
                                    int[] strengthArrayRight = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.RIGHT.getValue(), strengthArrayRight);
                                    sendDataService.write(chainData);
                                    break;
                                case BOTTOM:
                                    int[] strengthArrayBack = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.BACK.getValue(), strengthArrayBack);
                                    sendDataService.write(chainData);
                                    break;
                                case TOP_LEFT:
                                    int[] strengthArrayTopLeft = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.PROGRESS.getValue(), strengthArrayTopLeft);
                                    sendDataService.write(chainData);

                                    chainData = chainBuilder.makeChain(FrameType.LEFT.getValue(), strengthArrayTopLeft);
                                    sendDataService.write(chainData);
                                    break;
                                case TOP_RIGHT:
                                    int[] strengthArrayTopRight = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.PROGRESS.getValue(), strengthArrayTopRight);
                                    sendDataService.write(chainData);

                                    chainData = chainBuilder.makeChain(FrameType.RIGHT.getValue(), strengthArrayTopRight);
                                    sendDataService.write(chainData);
                                    break;
                                case BOTTOM_LEFT:
                                    int[] strengthArrayBottomLeft = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.BACK.getValue(), strengthArrayBottomLeft);
                                    sendDataService.write(chainData);

                                    chainData = chainBuilder.makeChain(FrameType.LEFT.getValue(), strengthArrayBottomLeft);
                                    sendDataService.write(chainData);
                                    break;
                                case BOTTOM_RIGHT:
                                    int[] strengthArrayBottomRight = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.BACK.getValue(), strengthArrayBottomRight);
                                    sendDataService.write(chainData);

                                    chainData = chainBuilder.makeChain(FrameType.RIGHT.getValue(), strengthArrayBottomRight);
                                    sendDataService.write(chainData);
                                    break;
                                case CENTER:
                                    int[] strengthCenter = new int[] {strength};

                                    chainData = chainBuilder.makeChain(FrameType.STOP.getValue(), strengthCenter);
                                    sendDataService.write(chainData);
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        tvAngle.setText(angle + "º");
                        tvStrength.setText(getStrengthToDecimal(strength) + " en decimal.");
                    }
                },
                200
        );
    }

    private int getStrengthToDecimal(int strength)
    {
        int MAX_STRENGTH = 255;
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
}
