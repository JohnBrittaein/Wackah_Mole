package com.example.wackah_mole;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;

import java.util.Random;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private final ImageButton[] moleViews = new ImageButton[15]; // Array to hold all mole ImageButtons

    GameViewModel GameModel = new GameViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initMoles();

        hideMoles();

        GameModel.StartGame();


        final Observer<List<MoleViewState>> MoleObserver = new Observer<List<MoleViewState>>() {
            @Override
            public void onChanged(List<MoleViewState> MoleStates) {
                hideMoles();//Hide all moles
                //iterate through mole list and change visibility states

                for (MoleViewState mole : MoleStates){
                    if(mole.isVisible) {
                        showMole(mole.position);
                    }

                    Log.i("moles", "moles " + Integer.toString(mole.position));

                }
            }
        };
        GameModel.getMoleStates().observe(this, MoleObserver);
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
                R.id.mole_14,
                R.id.mole_15
        };

        for (int i = 0; i < moleIds.length; i++) {
            moleViews[i] = findViewById(moleIds[i]);

            moleViews[i].setEnabled(true);

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
            mole.setAlpha(0f);
        }
    }

    /**
     * Shows a mole specified by the index
     * @param index index of mole to show
     */
    private void showMole(int index) {
        if (index >= 0 && index < moleViews.length) {
            moleViews[index].setAlpha(1f);
        }
    }

    /**
     * Hides a mole specified by the index
     * @param index index of mole to hide
     */
    private void hideMole(int index) {
        if (index >= 0 && index < moleViews.length) {
            moleViews[index].setAlpha(0f);
        }
    }

    /**
     * Pop ups a mole specified by the index
     * @param index index of mole to hide
     */
    private void popUpMole(int index) {
        ImageButton mole = moleViews[index];

        // Make the mole visible and start from hidden position
        mole.setVisibility(View.VISIBLE);
        mole.setEnabled(true);
        mole.setTranslationY(200f); // start "hidden" below

        // Animate mole up and leave it there
        mole.animate()
                .translationY(0f) // move mole up
                .setDuration(1000)
                .setInterpolator(new BounceInterpolator())
                .start();
    }

    /**
     * Called whenever a Mole View OnClick is called and successful
     * @param view
     */
    public void hitMole(View view){
        ImageButton mole = (ImageButton) view;
        int resourceId = mole.getId();
        int position = moleViewIDToPosition(resourceId);

        if (mole.getAlpha() == 1f) {
            mole.setImageResource(R.drawable.angry_mole);
            if (position >= 0) {
                GameModel.handlePlayerAction(true, false, position);
            }

            Log.d("Hit", "hitMole, position=" + position);
        } else {
            if (position >= 0) {
                GameModel.handlePlayerAction(false, false, position);
            }
            Log.d("Missed", "Missed Mole, position=" + position);
        }
    }

    /**
     * Helper method to extract the position of the Image Button(Mole View) that was hit
     * @param resourceId internal id of the Image Button(Mole View)
     * @return position of the Mole View
     */
    private int moleViewIDToPosition(int resourceId){
        String resourceName = getResources().getResourceEntryName(resourceId);
        String positionString = resourceName.substring(resourceName.indexOf('_') + 1);
        int position = -1;

        try {
            position = Integer.parseInt(positionString) - 1;
        } catch (NumberFormatException e) {
            Log.e("HitMole", "Failed to parse position from resource name", e);
        }

        return position; // Positions are 0 indexed
    }
}

