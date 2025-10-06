package com.example.wackah_mole;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private final ImageButton[] moleViews = new ImageButton[15]; // Array to hold all mole ImageButtons
    private List<MoleViewState> previousMoles;
    private final MutableLiveData<Integer> Score = new MutableLiveData<>();
    private GameViewModel GameModel = new GameViewModel();
    private EditText gameScore;
    private Drawable angryMole;
    private Drawable normalMole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize score
        Score.setValue(0);
        gameScore = findViewById(R.id.score);
        gameScore.setText("Score: 0");

        // Load Mole drawables, cache to use later
        angryMole = ContextCompat.getDrawable(this, R.drawable.angry_mole);
        normalMole = ContextCompat.getDrawable(this, R.drawable.mole);

        initMoles();
        hideMoles();

        // Observe score updates
        final Observer<Integer> updateScore = score -> gameScore.setText("Score: " + score);
        Score.observe(this, updateScore);

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
    private void updateMoleViews(List<MoleViewState> newStates) {
        if (newStates == null || newStates.isEmpty()) return;

        // Increase size when new moles are added
        if (previousMoles == null || previousMoles.size() != newStates.size()) {
            previousMoles = new ArrayList<>(newStates);
            redrawAllMoles(newStates);
            return;
        }

        // Compare previous vs new states and only the changed ones
        for (int i = 0; i < newStates.size(); i++) {
            MoleViewState oldState = previousMoles.get(i);
            MoleViewState newState = newStates.get(i);

            if (oldState.isVisible != newState.isVisible) {
                if (newState.isVisible) popUpMole(i);
                //else if (newState.isAttacking) moleAttack(i);
                else hideMole(i);
            }
        }

        previousMoles = new ArrayList<>(newStates);
    }

    /**
     * Redraws all moles from scratch (used if the mole list size changes).
     */
    private void redrawAllMoles(List<MoleViewState> states) {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).isVisible) showMole(i);
            else hideMole(i);
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
        mole.setTranslationY(200f);
        mole.setImageDrawable(normalMole);
        // Play going up sound
        mole.animate()
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new BounceInterpolator())
                .start();
    }

    /**
     * Pop-down animation for a mole
     * @param index
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

        if (mole.getAlpha() == 1f) {
            mole.setImageDrawable(angryMole);
            // Play hit sound here
            Score.postValue(Score.getValue() + 1);
            if (position >= 0) GameModel.handlePlayerAction(true, false, position);
            Log.d("Game", "Hit mole at position " + position);
        } else {
            // Play missed sound here
            if (position >= 0) GameModel.handlePlayerAction(false, false, position);
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
