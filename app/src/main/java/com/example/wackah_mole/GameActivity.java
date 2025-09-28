package com.example.wackah_mole;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class GameActivity extends AppCompatActivity {

    private final ImageButton[] moles = new ImageButton[14]; // Array to hold all mole ImageButtons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize all moles by finding them by ID
        moles[0] = findViewById(R.id.mole_one);
        moles[1] = findViewById(R.id.mole_two);
        moles[2] = findViewById(R.id.mole_three);
        moles[3] = findViewById(R.id.mole_four);
        moles[4] = findViewById(R.id.mole_five);
        moles[5] = findViewById(R.id.mole_six);
        moles[6] = findViewById(R.id.mole_seven);
        moles[7] = findViewById(R.id.mole_eight);
        moles[8] = findViewById(R.id.mole_nine);
        moles[9] = findViewById(R.id.mole_ten);
        moles[10] = findViewById(R.id.mole_eleven);
        moles[11] = findViewById(R.id.mole_twelve);
        moles[12] = findViewById(R.id.mole_thirteen);
        moles[13] = findViewById(R.id.mole_fourteen);

        // Loop through all moles and hide + disable clicks
        for (ImageButton mole : moles) {
            mole.setVisibility(View.INVISIBLE); // Hide mole
            mole.setEnabled(false);             // Disable clicking
        }

    }

    // Method to show a specific mole
    private void showMole(int index) {
        if (index >= 0 && index < moles.length) {
            moles[index].setVisibility(View.VISIBLE);
            moles[index].setEnabled(true);

            // Click listener for this mole
            moles[index].setOnClickListener(v -> {
                moles[index].setVisibility(View.INVISIBLE);
                moles[index].setEnabled(false);
                // TODO: add score or game logic here
            });
        }
    }

    // Method to hide a specific mole
    private void hideMole(int index) {
        if (index >= 0 && index < moles.length) {
            moles[index].setVisibility(View.INVISIBLE);
            moles[index].setEnabled(false);
        }
    }

}

