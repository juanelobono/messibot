package com.arquitecturasmoviles.messibot;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickActivity extends Activity {

    private final int MAX_STRENGTH = 255;
    private final int MIN_STRENGTH = 0;


    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionService mBluetoothConnection;

    private TextView tvDirection;
    private TextView tvAngle;
    private TextView tvStrength;

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

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (angle == 0){
                    tvDirection.setText(R.string.EastDirection);
                }else if(angle > 0 && angle < 90) {
                    tvDirection.setText(R.string.NorthEastDirection);
                }else if(angle == 90){
                    tvDirection.setText(R.string.NorthDirection);
                }else if(angle > 90 && angle < 180 ){
                    tvDirection.setText(R.string.NorthWestDirection);
                }else if(angle  == 180 ){
                    tvDirection.setText(R.string.WestDirection);
                }else if(angle > 180 && angle < 270 ){
                    tvDirection.setText(R.string.SouthWestDirection);
                }else if(angle == 270 ){
                    tvDirection.setText(R.string.SouthDirection);
                }else if(angle > 270 && angle < 360 ){
                    tvDirection.setText(R.string.SouthEastDirection);
                }

                tvAngle.setText(angle + "º");
                tvStrength.setText(getStrengthToDecimal(strength) + " en decimal.");
            }
        });
    }


    public int getStrengthToDecimal(int strength){
        //MAX_STRENGTH = 100%
        //MIN_STRENGTH = 0&
        //Se obtiene el decimal de 0 a 255 por regla de 3 simple.
        return (int) Math.ceil((strength * MAX_STRENGTH) / 100);

    }
}