package com.arquitecturasmoviles.messibot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private ImageView ivFoto;
    private TextView tvNombre;
    private TextView tvEmail;
    private TextView tvID;
    private Button btnSalir;
    private Button btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();

        ivFoto = findViewById(R.id.ivFoto);
        tvNombre = findViewById(R.id.tvNombre);
        tvEmail = findViewById(R.id.tvEmail);
        tvID = findViewById(R.id.tvID);
        btnSalir = findViewById(R.id.btnSalir);
        btnMenu = findViewById(R.id.btnMenu);

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            setUserData(firebaseUser);
        } else {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut(view);
            }
        });

    }

    private void setUserData(FirebaseUser user) {
        tvNombre.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        tvID.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(ivFoto);
    }

    public void signOut(View v){
        firebaseAuth.signOut();
        finish();
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}
