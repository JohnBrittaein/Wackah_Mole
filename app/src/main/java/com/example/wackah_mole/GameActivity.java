package com.example.wackah_mole;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private final ImageButton[] moleViews = new ImageButton[15]; // Array to hold all mole ImageButtons
    private ProgressBar HealthBar;
    private int missedMoles = 0;
    private Map<Integer, MoleViewState> previousMoles = new HashMap<>();
    private GameViewModel GameModel;
    private EditText gameScore;
    private Drawable angryMole;
    private Drawable normalMole;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        HealthBar = findViewById(R.id.healthBar);
        GameModel = new ViewModelProvider(this).get(GameViewModel.class);

        // Initialize score
        gameScore = findViewById(R.id.score);
        gameScore.setText("Score: 0");

        // Load Mole drawables, cache to use later
        angryMole = ContextCompat.getDrawable(this, R.drawable.angry_mole);
        normalMole = ContextCompat.getDrawable(this, R.drawable.mole);

        initMoles();
        hideMoles();

        // Observe score updates
        GameModel.score.observe(this, score -> gameScore.setText("Score:" + score));

        // Observe mole state updates
        GameModel.getMoleStates().observe(this, this::updateMoleViews);

        GameModel.StartGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (GameModel != null) {
            GameModel.StopGame();
        }
    }

    /**
     * Initializes the ImageButton array with their XML IDs.
     */
    private void initMoles() {
        int[] moleIds = {
                R.id.mole_1, R.id.mole_2, R.id.mole_3,
                R.id.mole_4, R.id.mole_5, R.id.mole_6,
                R.id.mole_7, R.id.mole_8, R.id.mole_9,
                R.id.mole_10, R.id.mole_11, R.id.mole_12,
                R.id.mole_13, R.id.mole_14, R.id.mole_15
        };

        for (int i = 0; i < moleIds.length; i++) {
            moleViews[i] = findViewById(moleIds[i]);
            if (moleViews[i] == null) {
                Log.w("initMoles", "Mole " + (i + 1) + " not found (ID: " + moleIds[i] + ")");
            } else {
                moleViews[i].setEnabled(true);
                moleViews[i].setImageDrawable(normalMole);
            }
        }
    }

    /**
     * Updates mole views
     */
    private void updateMoleViews(Map<Integer, MoleViewState> newStates) {
        if (newStates == null || newStates.isEmpty()) return;

        if (previousMoles.size() != newStates.size()) {
            redrawAllMoles(newStates);
            return;
        }

        for (Map.Entry<Integer, MoleViewState> entry : newStates.entrySet()) {
            int position = entry.getKey();
            MoleViewState newState = entry.getValue();
            MoleViewState oldState = previousMoles.get(position);

            boolean wasVisible = oldState != null && oldState.isVisible;
            boolean isVisible = newState.isVisible;

            if (wasVisible != isVisible) {
                if (isVisible) popUpMole(position);
                else hideMole(position);

                // Track missed moles
                if (!isVisible) {
                    missedMoles++;
                    HealthBar.setProgress(Math.max(0, 100 - 20 * missedMoles));
                    if (missedMoles > 4) {
                        Intent intent = new Intent(GameActivity.this, HighScore.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }

        previousMoles = new HashMap<>(newStates);
    }


    /**
     * Redraws all moles from scratch (used if the mole list size changes).
     */
    private void redrawAllMoles(Map<Integer, MoleViewState> newStates) {
        previousMoles = new HashMap<>(newStates);
        for (Map.Entry<Integer, MoleViewState> entry : newStates.entrySet()) {
            if (entry.getValue().isVisible) showMole(entry.getKey());
            else hideMole(entry.getKey());
        }
    }

    /**
     * Hides all moles on screen.
     */
    private void hideMoles() {
        for (ImageButton mole : moleViews) {
            if (mole != null) mole.setAlpha(0f);
        }
    }

    /**
     * Shows a mole specified by index.
     */
    private void showMole(int index) {
        if (isValidIndex(index)) {
            moleViews[index].setAlpha(1f);
            moleViews[index].setImageDrawable(normalMole);
        }
    }

    /**
     * Hides a mole specified by index.
     */
    private void hideMole(int index) {
        if (isValidIndex(index)) {
            moleViews[index].setAlpha(0f);
            moleViews[index].setImageDrawable(normalMole);
        }
    }

    /**
     * Pop-up animation for a mole.
     */
    private void popUpMole(int index) {
        if (!isValidIndex(index)) return;
        ImageButton mole = moleViews[index];
        mole.setAlpha(1f);
        mole.setTranslationY(50f);
        mole.setImageDrawable(normalMole);
      
        mole.animate()
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new BounceInterpolator())
                .start();
        mole.setAlpha(1f);
        mole.setEnabled(true);
    }

    /**
     * Pop-down animation for a mole
     * @param index index of the mole to pop down
     */
    private void popDownMole(int index) {
        if (!isValidIndex(index)) return;
        // Play going down sound
        ImageButton mole = moleViews[index];
    }

    /**
     * Handles mole hits.
     */
    public void hitMole(View view) {
        ImageButton mole = (ImageButton) view;
        int position = moleViewIDToPosition(mole.getId());

        if (position < 0 || previousMoles == null) {
            Log.w("Game", "Invalid mole click at position " + position);
            return;
        }

        MoleViewState hitMole = previousMoles.get(position);
        if (hitMole == null) {
            Log.w("Game", "No mole state found at position " + position);
            return;
        }

        if (hitMole.isVisible && hitMole.canBeHit()) {
            hitMole.setCanBeHit(false);
            mole.setImageDrawable(angryMole);
            // Play hit sound here
            GameModel.playerHitRecently = true;
            missedMoles--;
            GameModel.handlePlayerAction(true, false, position);
            Log.d("Game", "Hit mole at position " + position);
        } else if (hitMole.isVisible && !hitMole.canBeHit()) {
            mole.setAlpha(0.5f);
            Log.d("Game", "Mole was already hit at " + position);
        } else {
            // Play missed sound here
            GameModel.handlePlayerAction(false, false, position);
            Log.d("Game", "Missed mole at position " + position);
        }
    }

    /**
     * Converts a mole view's ID to its position in the array.
     */
    private int moleViewIDToPosition(int resourceId) {
        String resourceName = getResources().getResourceEntryName(resourceId);
        String positionString = resourceName.substring(resourceName.indexOf('_') + 1);
        try {
            return Integer.parseInt(positionString) - 1; // zero-indexed
        } catch (NumberFormatException e) {
            Log.e("Game", "Failed to parse position from resource: " + resourceName, e);
            return -1;
        }
    }

    /**
     * Checks if index is within bounds.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < moleViews.length && moleViews[index] != null;
    }
}
