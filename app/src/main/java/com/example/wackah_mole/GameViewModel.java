package com.example.wackah_mole;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameViewModel extends ViewModel {

    // Initialize the moles
    List<Mole> moles = new ArrayList<>();
    public boolean playerHitRecently = false;
    public boolean playerMissedRecently = false;

    // Difficulty
    private final int SCORE_THRESH = 300;
    private final double TIME_MULT = 0.01;

    private int lastMoleScore = -1; // Keeps track of the last score in which as mole was added

    // Schedule and time keeping variables
    private long startTime = 0L;
    private long endTime = 0L;
    private long elaspedTime = 0L;
    private static final long BASE_INTERVAL_MS = 1500;
    private static final long MIN_INTERVAL_MS = 1200;
    private long currentInterval = BASE_INTERVAL_MS;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> ticker;

    // Game state Variables
    private final HashMap<Integer, GameState> prevStates = new HashMap<>();
    private final HashMap<Integer, MoleBrain.Action> lastActions = new HashMap<>();

    // LiveData for UI to observe
    private final MutableLiveData<Integer> _score = new MutableLiveData<>(0);
    public LiveData<Integer> score = _score;
    private final MutableLiveData<Map<Integer, MoleViewState>> moleViewStates = new MutableLiveData<>();
    public LiveData<Map<Integer, MoleViewState>> getMoleStates() { return moleViewStates; }
    private final Map<Integer, MoleViewState> lastPostedStates = new HashMap<>();
    public final MutableLiveData<Integer> _moleCount = new MutableLiveData<>(0);
    public LiveData<Integer> moleCount = _moleCount;


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
        Map<Integer, MoleViewState> updatedMoleViewStates = new HashMap<>();
        boolean hasChanged = false;

        // Check score and add a mole for each level
        int score =_score.getValue() != null ? _score.getValue() : 0;
        int moleCount = _moleCount.getValue() != null ? _moleCount.getValue() : 0;
        if (score % SCORE_THRESH == 0 && score != lastMoleScore){
            lastMoleScore = score;
            addMole();
            moleCount++;
            _moleCount.postValue(moleCount);
            Log.d("Game Debug", "A new mole has been added to the game");
        }

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
                    mole.isAttacking(),
                    mole.canBeHit()
            );
            updatedMoleViewStates.put(mole.getPosition(), newViewState);
        }

        // Only post the new state if the state has changed
        if (lastPostedStates.size() != updatedMoleViewStates.size()) {
            hasChanged = true;
        } else {
            for (Map.Entry<Integer, MoleViewState> entry : updatedMoleViewStates.entrySet()) {
                int key = entry.getKey();
                MoleViewState newState = entry.getValue();
                MoleViewState oldState = lastPostedStates.get(key);

                if (oldState == null || oldState.isVisible != newState.isVisible || oldState.isAttacking != newState.isAttacking) {
                    hasChanged = true;
                    break;
                }
            }
        }

        if (hasChanged) {
            moleViewStates.postValue(updatedMoleViewStates);
            lastPostedStates.clear();
            lastPostedStates.putAll(updatedMoleViewStates);
        }

        playerHitRecently = false;
        playerMissedRecently = false;
    }

    public void StartGame() {
        startTime = System.currentTimeMillis();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        currentInterval = BASE_INTERVAL_MS;
        scheduleNextTick();
    }


    private void scheduleNextTick() {
        if (scheduler == null || scheduler.isShutdown()) return;

        ticker = scheduler.schedule(() -> {
            try {
                gameTick();

                double score = _score.getValue() != null ? _score.getValue() : 0;

                if (currentInterval > MIN_INTERVAL_MS) {
                    currentInterval -= (long) (TIME_MULT * score);
                    currentInterval = Math.max(currentInterval, MIN_INTERVAL_MS);
                    Log.d("Game Debug","The new thread interval is: " + currentInterval);
                }

                scheduleNextTick();  // Re-schedule again with new interval

            } catch (Exception e) {
                Log.e("Thread", "Thread error: " + e.getMessage());
            }
        }, currentInterval, TimeUnit.MILLISECONDS);
    }



    /**
     * Called to stop the ViewModel game thread
     */
    public void StopGame(){
        endTime = System.currentTimeMillis();
        elaspedTime = endTime - startTime;
        if (ticker != null) ticker.cancel(true);
        if (scheduler != null) scheduler.shutdownNow();
    }

    /**
     * Called after the player reacts (taps mole or misses)
     */
    public void handlePlayerAction(boolean moleWasHit, boolean moleAttackedPlayer, int position) {
        Log.d("GameDebug", "handlePlayerAction() called. Hit: " + moleWasHit + ", Missed: " + moleAttackedPlayer + ", pos: " + position);

        Mole mole = null;
        for (Mole m : moles) {
            if (m.getPosition() == position) {
                mole = m;
                break;
            }
        }
        if (mole == null) {
            Log.d("GameDebug", "No mole found at position " + position);
            return;
        }

        int moleId = mole.getId();

        double reward;
        int currentScore = _score.getValue() != null ? _score.getValue() : 0;

        if (moleWasHit) {
            currentScore += 100;
            reward = -1.0;
        } else if (moleAttackedPlayer) {
            currentScore -= 50;
            reward = 1.0;
        } else {
            reward = 0.5;
        }

        _score.postValue(currentScore);

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

