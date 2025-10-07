package com.example.wackah_mole;

import android.util.Log;

/**
 * This class represents a mole in digital form
 * The Mole can move throughout the hole network, hide, and attack
 * The Mole is a smart Mole, and has a digital brain (MoleBrain) which
 * is implemented as a reinforced learning model. Overtime the Mole learns how to
 * not got hit by the player, while hitting you.
 */
public class Mole {
    private final int id;
    private static int nextId = 0; // Static to keep track of id throughout multiple moles
    private int currentPosition;
    private boolean isVisible;
    private boolean isAttacking;
    private boolean canBeHit;
    private long visibleSince = -1;
    private static final long MOLE_VISIBLE_DURATION = 900;


    // Declare the Moles AI
    private final MoleBrain brain;

    /**
     * Public constructor for a new Mole
     */
    public Mole(){
        this.id = nextId++;
        this.currentPosition = 0;
        this.isVisible = false;
        this.isAttacking = false;
        this.canBeHit = false;
        this.brain = new MoleBrain();
    }

    /**
     * Update mole according to the brain's decision
     * @param gameState the current game state to update
     * @return the chosen state
     */
    public MoleBrain.Action update(GameState gameState) {
        // Each mole must store it's own lastState, and lastAction

        // If mole is visible and has been visible too long, hide it automatically
        if (isVisible && visibleSince > 0 &&
                (System.currentTimeMillis() - visibleSince) > MOLE_VISIBLE_DURATION) {
            isVisible = false;
            isAttacking = false;
            canBeHit = true;
            visibleSince = -1;
            Log.d("Mole", "Auto-hide after timeout");
            return MoleBrain.Action.HIDE;
        }

        // Decide next action
        MoleBrain.Action lastAction = brain.decideAction(gameState);

        switch (lastAction) {
            case HIDE:
                isVisible = false;
                isAttacking = false;
                canBeHit = true;
                visibleSince = -1;
                break;
            case ATTACK:
                isVisible = true;
                isAttacking = true;
                canBeHit = false;
                visibleSince = System.currentTimeMillis();
                break;
            default:
                isVisible = true;
                isAttacking = false;
                canBeHit = true;
                currentPosition = getHole(lastAction);
                visibleSince = System.currentTimeMillis();
                break;
        }

        Log.d("Mole", "Action: " + lastAction);
        return lastAction;
    }


    public void giveReward(GameState prevState, MoleBrain.Action action, double reward, GameState newState) {
        brain.updateQtable(prevState, action, reward, newState);
    }

    /**
     * Returns the current hole position based on the action given
     * ex. POPUP_HOLE_0 returns 0, POPUP_HOLE_4 returns 4
     * @param action the current action of the mole
     * @return position
     */
    private int getHole(MoleBrain.Action action){
        return action.ordinal() - MoleBrain.Action.POPUP_HOLE_0.ordinal();
    }


    // Public getters
    public int getPosition() {
        return currentPosition;
    }
    public int getId() {
        return id;
    }
    public boolean isVisible() {
        return isVisible;
    }
    public boolean isAttacking() {return isAttacking;}
    public boolean canBeHit() {return canBeHit;}
}
