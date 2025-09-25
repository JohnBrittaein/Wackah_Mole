package com.example.wackah_mole;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {

    // Initialize the mole
    Mole mole = new Mole();

    public boolean playerHitRecently = false;
    public boolean playerMissedRecently = false;

    private GameState prevState;
    private MoleBrain.Action lastAction;

    // LiveData for UI to observe
    private final MutableLiveData<Integer> molePosition = new MutableLiveData<>(9); // default = hidden
    private final MutableLiveData<Boolean> moleVisible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> moleAttacking = new MutableLiveData<>(false);

    public LiveData<Integer> getMolePosition() {
        return molePosition;
    }

    public LiveData<Boolean> isMoleVisible() {
        return moleVisible;
    }

    public LiveData<Boolean> isMoleAttacking() {
        return moleAttacking;
    }

    /**
     * Called on each game tick (e.g., via Handler or Timer)
     */
    public void gameTick() {
        GameState gameState = new GameState(
                mole.getPosition(),
                playerHitRecently,
                playerMissedRecently
        );

        prevState = gameState;

        // Let mole decide its action and update
        lastAction = mole.update(gameState);

        // Update LiveData to reflect mole's new state
        molePosition.setValue(mole.getPosition());
        moleVisible.setValue(mole.isVisible());
        moleAttacking.setValue(mole.isAttacking());
    }

    /**
     * Called after the player reacts (taps mole or misses)
     */
    public void handlePlayerAction(boolean moleWasHit, boolean moleAttackedPlayer) {
        double reward;

        if (moleWasHit) {
            reward = -1.0;
        } else if (moleAttackedPlayer) {
            reward = 1.0;
        } else {
            reward = 0.5;
        }

        GameState newState = new GameState(
                mole.getPosition(),
                playerHitRecently,
                playerMissedRecently
        );

        mole.giveReward(prevState, lastAction, reward, newState);
    }
}

