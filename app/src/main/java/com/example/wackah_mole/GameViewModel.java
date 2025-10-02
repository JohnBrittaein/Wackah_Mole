package com.example.wackah_mole;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameViewModel extends ViewModel {

    // Initialize the moles
    List<Mole> moles = new ArrayList<>();
    public boolean playerHitRecently = false;
    public boolean playerMissedRecently = false;

    private long StartGameTickFlag = 0L;
    private long StartTime = 0L;
    private long GameTickInterval = 0L;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> ticker;

    private HashMap<Integer, GameState> prevStates = new HashMap<>();
    private HashMap<Integer, MoleBrain.Action> lastActions = new HashMap<>();

    // LiveData for UI to observe
    private final MutableLiveData<List<MoleViewState>> moleViewStates = new MutableLiveData<>();

    public LiveData<List<MoleViewState>> getMoleStates() {
        return moleViewStates;
    }

    /**
     * Public constructor of GameViewModel
     */
    public GameViewModel() {
        int STARTING_MOLE_COUNT = 1; // change this if you want more moles to start
        for (int i = 0; i < STARTING_MOLE_COUNT; i++) {
            moles.add(new Mole());
        }
    }

    /**
     * Call to add a mole to the GameViewModel
     */
    public void addMole() {
        moles.add(new Mole());
    }

    /**
     * Gets the # of moles in the GameViewModel
     * @return moles.size()
     */
    public int getMoleCount(){
        return moles.size();
    }

    /**
     * Called on each game tick (e.g., via Handler or Timer)
     */
    public void gameTick() {
        List<MoleViewState> updatedMoleViewStates = new ArrayList<>();

        // Loop through all the moles
        for (Mole mole : moles){
            // update gameState
            GameState gameState = new GameState(
                    mole.getPosition(),
                    playerHitRecently,
                    playerMissedRecently
            );

            // Save per-mole state/action using ID
            prevStates.put(mole.getId(), gameState);
            lastActions.put(mole.getId(), mole.update(gameState));

            // update MoleViewStates
            updatedMoleViewStates.add(new MoleViewState(
                    mole.getId(),
                    mole.getPosition(),
                    mole.isVisible(),
                    mole.isAttacking()
            ));
        }

        moleViewStates.postValue(updatedMoleViewStates);

        playerHitRecently = false;
        playerMissedRecently = false;
    }

    /**
     * Called on each game tick (e.g., via Handler or Timer)
     */
    public void StartGame (){
        StartTime = System.currentTimeMillis();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        ticker = scheduler.scheduleWithFixedDelay(() -> {
            try {
                if(StartGameTickFlag < System.currentTimeMillis()) {
                    gameTick();
                    StartGameTickFlag = System.currentTimeMillis() + GameTickInterval;//+ game rate interval
                }
            }
            catch (Exception e){
                Log.e("Thread", "Thread error");
            }

        }, 0, 2000 , TimeUnit.MILLISECONDS);
    }


    /**
     * Called after the player reacts (taps mole or misses)
     */
    public void handlePlayerAction(boolean moleWasHit, boolean moleAttackedPlayer, int position) {
        if (position < 0 || position >= moles.size()) return;

        Mole mole = moles.get(position);
        int moleId = mole.getId();

        double reward;
        if (moleWasHit) {
            reward = -1.0;
        } else if (moleAttackedPlayer) {
            reward = 1.0;
        } else {
            reward = 0.5;
        }

        GameState prevState = prevStates.get(moleId);
        MoleBrain.Action lastAction = lastActions.get(moleId);

        if (prevState == null || lastAction == null) return;

        GameState newState = new GameState(
                mole.getPosition(),
                playerHitRecently,
                playerMissedRecently
        );

        mole.giveReward(prevState, lastAction, reward, newState);
    }
}

