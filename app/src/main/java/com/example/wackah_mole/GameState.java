package com.example.wackah_mole;

/**
 * <p>
 * Game state used by the mole's brain
 * </p>
 *
 * @author John Brittain
 */
public class GameState {
    private final int molePosition;
    private final boolean playerRecentlyMissed;
    private final boolean playerRecentlyHit;

    /**
     * Creates a new GameState
     * @param molePosition Position of the mole
     * @param playerHit Indicates if the player hit the mole
     * @param playerMiss Indicates if the player missed the mole
     */
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
