package com.example.wackah_mole;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private final ImageButton[] moleViews = new ImageButton[15]; // Array to hold all mole ImageButtons
    private ProgressBar healthBar;
    private int missedMoles = 0;
    private Map<Integer, MoleViewState> previousMoles = new HashMap<>();
    private GameViewModel gameModel;
    private EditText gameScore;
    private EditText moleCountText;
    private Drawable angryMole;
    private Drawable normalMole;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        healthBar = findViewById(R.id.healthBar);
        gameModel = new ViewModelProvider(this).get(GameViewModel.class);
        Button exitButton = findViewById(R.id.exit_button);

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // Initialize score
        gameScore = findViewById(R.id.score);
        gameScore.setText("Score: 0");
        SharedPreferences highScores_Manager = this.getSharedPreferences("Highscores", Context.MODE_PRIVATE);

        EditText highScore;
        highScore = findViewById(R.id.high_score);
        highScore.setText("High Score: " + highScores_Manager.getInt(getString(R.string.HighScore1Key),0));

        // Initialize mole count
        moleCountText = findViewById(R.id.mole_count);
        moleCountText.setText("  Moles: 1");

        // Load Mole drawables, cache to use later
        angryMole = ContextCompat.getDrawable(this, R.drawable.angry_mole);
        normalMole = ContextCompat.getDrawable(this, R.drawable.mole);


        initMoles();
        hideMoles();

        // Observe score updates
        gameModel.score.observe(this, score -> gameScore.setText("Score: " + score));

        // Observe mole count updates
        gameModel.moleCount.observe(this, moleCount -> moleCountText.setText("  Moles: " + moleCount));

        // Observe mole state updates
        gameModel.getMoleStates().observe(this, this::updateMoleViews);

        gameModel.StartGame();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (gameModel != null) {
//            gameModel.StopGame();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (gameModel != null) {
//            gameModel.StartGame();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (gameModel != null) {
//            gameModel.StopGame();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameModel != null) {
            gameModel.StopGame();
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
                //Log.w("initMoles", "Mole " + (i + 1) + " not found (ID: " + moleIds[i] + ")");
            } else {
                moleViews[i].setEnabled(true);
                moleViews[i].setImageDrawable(normalMole);
            }
        }
    }

    /**
     * Updates mole views
     * @param newStates map of new states to update moles from
     */
    private void updateMoleViews(Map<Integer, MoleViewState> newStates) {
        if (newStates == null || newStates.isEmpty()) return;

        // Redraw the moles if the size changes (E.g a new mole is added)
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
                if (isVisible) {popUpMole(position); newState.setCanBeHit(true);}
                else hideMole(position);

                // Track missed moles
                if (!isVisible) {
                    missedMoles++;
                    healthBar.setProgress(Math.max(0, 100 - 20 * missedMoles));
                    if (missedMoles == 4) {
                        // Wed
                        healthBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    } else if (missedMoles == 2){
                        // Yewwow
                        healthBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
                    } else if (missedMoles == 3){
                        // Organe
                        healthBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(255,128,0)));
                    } else {
                        // Gween
                        healthBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                    }
                    shakeView(findViewById(R.id.myImageView));
                    if (missedMoles > 4) {
                        Intent intent = new Intent(GameActivity.this, HighScore.class);
                        intent.putExtra("Score",gameModel.score.getValue());
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }

        previousMoles.clear();
        previousMoles.putAll(newStates);
    }


    /**
     * Shakes the screen violently
     * @param view view you want to shake
     */
    public void shakeView(View view) {
        Animation shake = new TranslateAnimation(-10, 10, 0, 0);
        shake.setDuration(500); // duration of one shake cycle
        shake.setInterpolator(new CycleInterpolator(5)); // how many times it shakes
        view.startAnimation(shake);
    }


    /**
     * Redraws all moles from scratch (used if the mole list size changes).
     * @param newStates map of MoleViewStates to redraw the moles from
     */
    private void redrawAllMoles(Map<Integer, MoleViewState> newStates) {
        previousMoles = new HashMap<>(newStates);
        for (Map.Entry<Integer, MoleViewState> entry : newStates.entrySet()) {
            if (entry.getValue().isVisible) popUpMole(entry.getKey());
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
     * @param index hole position where to show the mole
     */
    private void showMole(int index) {
        if (isValidIndex(index)) {
            moleViews[index].setAlpha(1f);
            moleViews[index].setImageDrawable(normalMole);
        }
    }

    /**
     * Hides a mole specified by index.
     * @param index hole position where to hide the mole
     */
    private void hideMole(int index) {
        if (isValidIndex(index)) {
            moleViews[index].setAlpha(0f);
            moleViews[index].setImageDrawable(normalMole);
        }
    }

    /**
     * Pop-up animation for a mole and sound effect.
     * @param index hole position where to show the mole
     */
    private void popUpMole(int index) {
        if (!isValidIndex(index)) return;
        ImageButton mole = moleViews[index];
        shakeView(mole);
        mole.setAlpha(1f);
        mole.setTranslationY(50f);
        mole.setImageDrawable(normalMole);

        // Play sound
        playSound(R.raw.woosh);

        //Animate the mole
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
     * @param view ImageButton which was clicked
     */
    public void hitMole(View view) {
        ImageButton mole = (ImageButton) view;
        int position = moleViewIDToPosition(mole.getId());

        if (position < 0 || previousMoles == null) {
            //Log.w("Game", "Invalid mole click at position " + position);
            return;
        }

        MoleViewState hitMole = previousMoles.get(position);
        if (hitMole == null) {
            //Log.w("Game", "No mole state found at position " + position);
            return;
        }

        if (hitMole.isVisible && hitMole.canBeHit()) {
            playSound(R.raw.bonksoundeffectupdated);
            hitMole.setCanBeHit(false);
            mole.setImageDrawable(angryMole);
            gameModel.playerHitRecently = true;
            missedMoles--;
            gameModel.handlePlayerAction(true, false, position);
            //Log.d("Game", "Hit mole at position " + position);
        } else if (hitMole.isVisible && !hitMole.canBeHit()) {
            mole.setAlpha(0.5f);
            //Log.d("Game", "Mole was already hit at " + position);
        } else {
            // Play missed sound here
            gameModel.handlePlayerAction(false, false, position);
            //Log.d("Game", "Missed mole at position " + position);
        }
    }

    /**
     * Converts a mole view's ID to its position in the array.
     * @param resourceId the resource ID of an image button
     */
    private int moleViewIDToPosition(int resourceId) {
        String resourceName = getResources().getResourceEntryName(resourceId);
        String positionString = resourceName.substring(resourceName.indexOf('_') + 1);
        try {
            return Integer.parseInt(positionString) - 1; // zero-indexed
        } catch (NumberFormatException e) {
            //Log.e("Game", "Failed to parse position from resource: " + resourceName, e);
            return -1;
        }
    }

    /**
     * Checks if index is within bounds.
     * @param index index to check
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < moleViews.length && moleViews[index] != null;
    }

    /**
     * plays a sound from the raw resource
     * @param Id the id of the sound
     */
    private void playSound(int Id){
     try{
        MediaPlayer mediaPlayer = MediaPlayer.create(this, Id);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    } catch (IllegalStateException e) {
        //Log.e("game", "IllegalStateException");
    }
    }
}
