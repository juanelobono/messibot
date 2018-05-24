package com.arquitecturasmoviles.messibot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

public class AdminActivity extends AppCompatActivity {

    private EditText etNewPass;
    private Button btnNewPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin);

        etNewPass = findViewById(R.id.etNewPass);
        btnNewPass = findViewById(R.id.btnNewPass);

        final ArrayList<String> arrayPass= new ArrayList<>();

        arrayPass.add("123456");
        arrayPass.add("654321");
        arrayPass.add("123789");
        arrayPass.add("987321");
        arrayPass.add("456789");
        arrayPass.add("987654");
        arrayPass.add("564738");
        arrayPass.add("837465");

        btnNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random random = new Random();
                int index = random.nextInt(arrayPass.size());
                String password = arrayPass.get(index);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_show_password, null);

                final TextView tvShowPassword = mView.findViewById(R.id.tvShowPassword);
                tvShowPassword.setText(password);
                Button btnAceptar = mView.findViewById(R.id.btnAceptar);

                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });

    }
}
