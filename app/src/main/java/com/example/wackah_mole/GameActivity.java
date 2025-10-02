package com.example.wackah_mole;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private final ImageButton[] moleViews = new ImageButton[15]; // Array to hold all mole ImageButtons

    private final MutableLiveData<Integer> Score = new MutableLiveData<Integer>();
    GameViewModel GameModel = new GameViewModel();
    private EditText gameScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Score.setValue(0);
        gameScore = findViewById(R.id.score);
        gameScore.setText("0");

        initMoles();

        hideMoles();

        GameModel.StartGame();


        final Observer<List<MoleViewState>> MoleObserver = new Observer<List<MoleViewState>>() {
            @Override
            public void onChanged(List<MoleViewState> MoleStates) {
                hideMoles();//Hide all moles
                //itterate through mole list and change visibility states

                for (MoleViewState mole : MoleStates){
                    if(mole.isVisible) {
                        showMole(mole.position);
                    }

                    Log.i("moles", "moles " +Integer.toString(mole.position));

                }
            }
        };

        final Observer<Integer> updateScore = new Observer<Integer>(){
            @Override
            public void onChanged(Integer Score){
                gameScore.setText("Score: " + Score);
            }
        };
        Score.observe(this, updateScore);
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
  
    public void hitMole(View view){
        ImageButton mole = (ImageButton) view;
        findViewById(mole.getId()).setVisibility(View.INVISIBLE);
        Score.postValue(Score.getValue() + 1);
        Log.d("Hit", "hitMole");
    }
}

