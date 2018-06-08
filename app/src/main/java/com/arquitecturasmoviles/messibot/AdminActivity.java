package com.arquitecturasmoviles.messibot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class AdminActivity extends AppCompatActivity {

    private EditText etNewPass;
    private Button btnNewPass;
    private String password = "123456";
    private String newPassword;
    private ChainBuilder chainBuilder;
    private SendDataService sendDataService;
    private BluetoothDevice mBTDevice;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin);

        etNewPass = findViewById(R.id.etNewPass);
        btnNewPass = findViewById(R.id.btnNewPass);

        mBTDevice = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (mBTDevice == null){
            Toast.makeText(AdminActivity.this, "No hay dispositivos conectados.",
                    Toast.LENGTH_LONG).show();
        }else{
            try {
                sendDataService = new SendDataService(mBTDevice);
            } catch (IOException e) {
                Toast.makeText(AdminActivity.this, "Error al establecer la comunicación",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


        final ArrayList<String> arrayPass= new ArrayList<>();

        arrayPass.add("123456");
        arrayPass.add("654321");
        arrayPass.add("123789");
        arrayPass.add("987321");
        arrayPass.add("456789");
        arrayPass.add("987654");
        arrayPass.add("564738");
        arrayPass.add("837465");
        arrayPass.add("091256");
        arrayPass.add("506932");

        btnNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etNewPass.getText().toString().equals(password)) {
                    Random random = new Random();
                    int index = random.nextInt(arrayPass.size());
                    newPassword = arrayPass.get(index);

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.activity_show_password, null);

                    final TextView tvShowPassword = mView.findViewById(R.id.tvShowPassword);
                    tvShowPassword.setText(newPassword);
                    Button btnAceptar = mView.findViewById(R.id.btnAceptar);

                    btnAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                sendDataService.write(setNewPassword(etNewPass.getText().toString()));
                            } catch (IOException e) {
                                Toast.makeText(AdminActivity.this, "Error al establecer la comunicación",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });

                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();

                } else {
                    Toast.makeText(AdminActivity.this, "Ingrese una contraseña",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public byte[] setNewPassword(String newPassword)
    {
        this.chainBuilder = new ChainBuilder();

        byte[] passByte = newPassword.getBytes();
        int[] passInt = this.chainBuilder.getIntBytes(passByte);

        byte[] chain = this.chainBuilder.makeChain(FrameType.CHANGE_PASSWORD.getValue(), passInt);
        return chain;
    }
}
