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

    // Schedule and time keeping variables
    private long StartTime = 0L;
    private long EndTime = 0L;
    private long ElaspedTime = 0L;
    private static final long BASE_INTERVAL_MS = 2000;
    private static final long MIN_INTERVAL_MS = 400;
    private long currentInterval = BASE_INTERVAL_MS;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> ticker;

    // Game state Variables
    private HashMap<Integer, GameState> prevStates = new HashMap<>();
    private HashMap<Integer, MoleBrain.Action> lastActions = new HashMap<>();

    // LiveData for UI to observe
    private final MutableLiveData<List<MoleViewState>> moleViewStates = new MutableLiveData<>();
    public LiveData<List<MoleViewState>> getMoleStates() {
        return moleViewStates;
    }
    private List<MoleViewState> lastPostedStates = new ArrayList<>();


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
        boolean hasChanged = false;

        for (Mole mole : moles) {
            GameState gameState = new GameState(
                    mole.getPosition(),
                    playerHitRecently,
                    playerMissedRecently
            );

            prevStates.put(mole.getId(), gameState);
            lastActions.put(mole.getId(), mole.update(gameState));

            MoleViewState newViewState = new MoleViewState(
                    mole.getId(),
                    mole.getPosition(),
                    mole.isVisible(),
                    mole.isAttacking()
            );
            updatedMoleViewStates.add(newViewState);
        }

        // Only post the new state if the state has changed
        if (lastPostedStates.size() != updatedMoleViewStates.size()) {
            hasChanged = true;
        } else {
            for (int i = 0; i < updatedMoleViewStates.size(); i++) {
                MoleViewState oldState = lastPostedStates.get(i);
                MoleViewState newState = updatedMoleViewStates.get(i);
                if (oldState.isVisible != newState.isVisible || oldState.isAttacking != newState.isAttacking) {
                    hasChanged = true;
                    break;
                }
            }
        }

        if (hasChanged) {
            moleViewStates.postValue(updatedMoleViewStates);
            lastPostedStates = new ArrayList<>(updatedMoleViewStates);
        }

        playerHitRecently = false;
        playerMissedRecently = false;
    }

    /**
     * Creates a thread for the View Model and start the game
     */
    public void StartGame() {
        StartTime = System.currentTimeMillis();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        ticker = scheduler.scheduleWithFixedDelay(() -> {
            try {
                gameTick();

                if (currentInterval > MIN_INTERVAL_MS) {
                    currentInterval -= 50;
                    rescheduleThread();
                }

            } catch (Exception e) {
                Log.e("Thread", "Thread error: " + e.getMessage());
            }
        }, 0, currentInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the thread running game tick and reschedules it with a fast time interval
     */
    private void rescheduleThread() {
        if (ticker != null && !ticker.isCancelled()) {
            ticker.cancel(false);
            ticker = scheduler.scheduleWithFixedDelay(this::gameTick, 0, currentInterval, TimeUnit.MILLISECONDS);
            Log.d("GameSpeed", "Speed increased: " + currentInterval + "ms");
        }
    }

    /**
     * Called to stop the ViewModel game thread
     */
    public void StopGame(){
        EndTime = System.currentTimeMillis();
        ElaspedTime = EndTime - StartTime;
        if (ticker != null) ticker.cancel(true);
        if (scheduler != null) scheduler.shutdownNow();
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

