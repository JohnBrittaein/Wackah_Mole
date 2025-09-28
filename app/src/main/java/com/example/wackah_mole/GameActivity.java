package com.example.wackah_mole;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class GameActivity extends AppCompatActivity {

    private final ImageButton[] moleViews = new ImageButton[14]; // Array to hold all mole ImageButtons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initMoles();

        hideMoles();
    }

    /**
     * Initializes the Image Button moleViews and assigns there associated ID from the xml
     */
    private void initMoles(){
        int[] moleIds = {
                R.id.mole_1,
                R.id.mole_2,
                R.id.mole_3,
                R.id.mole_4,
                R.id.mole_5,
                R.id.mole_6,
                R.id.mole_7,
                R.id.mole_8,
                R.id.mole_9,
                R.id.mole_10,
                R.id.mole_11,
                R.id.mole_12,
                R.id.mole_13,
                R.id.mole_14
        };

        for (int i = 0; i < moleIds.length; i++) {
            moleViews[i] = findViewById(moleIds[i]);

            if (moleViews[i] == null) {
                Log.w("initMoles", "Mole " + (i + 1) + " not found (ID: " + moleIds[i] + ")");
            }
        }
    }

    /**
     * Hides all moles on screen
     */
    private void hideMoles(){
        for (ImageButton mole : moleViews){
            mole.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Shows a mole specified by the index
     * @param index index of mole to show
     */
    private void showMole(int index) {
        if (index >= 0 && index < moleViews.length) {
            moleViews[index].setVisibility(View.VISIBLE);
            moleViews[index].setEnabled(true);
        }
    }

    /**
     * Hides a mole specified by the index
     * @param index index of mole to hide
     */
    private void hideMole(int index) {
        if (index >= 0 && index < moleViews.length) {
            moleViews[index].setVisibility(View.INVISIBLE);
            moleViews[index].setEnabled(false);
        }
    }
}

