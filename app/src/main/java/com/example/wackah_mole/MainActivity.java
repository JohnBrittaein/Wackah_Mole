package com.example.wackah_mole;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Init Start Button
        Button startBtn = findViewById(R.id.start_btn);

        // Init Settings Button
        Button settingsBtn = findViewById(R.id.settings_btn);

        // Init Exit Button
        Button exitBtn = findViewById(R.id.exit_btn);

        // Click Listener for Start Button
        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
        });

        // Click Listener for Settings Button
        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
        });

        // Click Listener for Exit Button
        exitBtn.setOnClickListener(v -> {

        });
    }
}