package com.arquitecturasmoviles.messibot;

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
    private BluetoothDevice mBluetoothDevice=null;


    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionService mBluetoothConnection;

    private TextView tvDirection;
    private TextView tvAngle;
    private TextView tvStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final JoystickView joystick = findViewById(R.id.joystick);

        tvDirection = findViewById(R.id.tvDirection);
        tvAngle = findViewById(R.id.tvAngle);
        tvStrength = findViewById(R.id.tvStrength);

        mBluetoothDevice=getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

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

    /*
     *   Esta clase está destinada a utilizarse de cronómetro para la terminación automática y asíncrona
     *   de conexiones bluetooth dados los milisegundos requeridos para que esta tarea se lleve a cabo.
     *
     * */
    public final class Reminder {
        Timer timer;

        public Reminder(int milliseconds) {
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

                //  Terminar el hilo del timer
                timer.cancel();
            }
        }
    }


    //  Este método permite desvincular las conexiones bluetooth pasada cierta cantidad de milisegundos
    protected void terminateBluetoothConnection(int milliseconds) {

        Reminder reminder = new Reminder(milliseconds);
        Toast.makeText(JoystickActivity.this, "Se desvincularán todos los dispositivos en " + milliseconds / 1000 + " segundos.", Toast.LENGTH_SHORT).show();

    }
}