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

    // Each mole must store it's own lastState, and lastAction
    private GameState lastState;
    private MoleBrain.Action lastAction;

    // Declare the Moles AI
    private MoleBrain brain;

    /**
     * Public constructor for a new Mole
     */
    public Mole(){
        this.id = nextId++;
        this.currentPosition = 0;
        this.isVisible = false;
        this.isAttacking = false;
        this.brain = new MoleBrain();
    }

    public MoleBrain.Action update(GameState gameState){
        // Update moles GameState
        this.lastState = gameState;

        // Mole decides an action
        lastAction = brain.decideAction(gameState);

        // Applies the action
        switch (lastAction) {
            case HIDE:
                isVisible = false;
                isAttacking = false;
                currentPosition = getHole(lastAction);
                break;
            case ATTACK:
                isVisible = true;
                isAttacking = true;
                currentPosition = getHole(lastAction);
                break;
            default:
                isVisible = true;
                isAttacking = false;
                currentPosition = getHole(lastAction);
                break;
        }
        Log.d("Action", String.valueOf(lastAction));
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
    public boolean isAttacking() {
        return isAttacking;
    }
}
