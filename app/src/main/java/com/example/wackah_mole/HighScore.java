package com.example.wackah_mole;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

    public class HighScore extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            // Init Start Button
            Button returnbtn = findViewById(R.id.return_btn);

            // Click Listener for Start Button
            super.onCreate(savedInstanceState);

            returnbtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        }

}
