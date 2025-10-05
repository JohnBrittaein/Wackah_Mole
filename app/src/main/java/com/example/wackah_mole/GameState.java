package com.example.wackah_mole;

/**
 * Game state used by the mole's brain
 */
public class GameState {
    private final int molePosition;
    private final boolean playerRecentlyMissed;
    private final boolean playerRecentlyHit;

    public GameState(int molePosition, boolean playerHit, boolean playerMiss) {
        this.molePosition = molePosition;
        this.playerRecentlyHit = playerHit;
        this.playerRecentlyMissed = playerMiss;
    }

    public int getStateIndex() {
        return molePosition;
    }

    public boolean getRecentlyHit(){
        return playerRecentlyHit;
    }

    public boolean getRecentlyMissed(){
        return playerRecentlyMissed;
    }

}
