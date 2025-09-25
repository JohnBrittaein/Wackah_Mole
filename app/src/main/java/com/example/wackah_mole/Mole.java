package com.example.wackah_mole;

/**
 * This class represents a mole in digital form
 * The Mole can move throughout the hole network, hide, and attack
 * The Mole is a smart Mole, and has a digital brain (MoleBrain) which
 * is implemented as a reinforced learning model. Overtime the Mole learns how to
 * not got hit by the player, while hitting you.
 */
public class Mole {
    private int currentPosition;
    private boolean isVisible;
    private boolean isAttacking;

    // Declare the Moles AI
    private MoleBrain brain;

    /**
     * Public constructor for a new Mole
     */
    public Mole(){
        this.currentPosition = 0;
        this.isVisible = false;
        this.isAttacking = false;
        this.brain = new MoleBrain();
    }

    public MoleBrain.Action update(GameState gameState){
        // Mole decides an action
        MoleBrain.Action action = brain.decideAction(gameState);

        // Applies the action
        switch (action) {
            case HIDE:
                isVisible = false;
                isAttacking = false;
                currentPosition = getHole(action);
                break;
            case ATTACK:
                isVisible = true;
                isAttacking = true;
                currentPosition = getHole(action);
                break;
            default:
                isVisible = true;
                isAttacking = false;
                currentPosition = getHole(action);
                break;
        }

        return action;
    }

    public void giveReward(GameState prevState, MoleBrain.Action action, double reward, GameState newState) {
        brain.updateQtable(prevState, action, reward, newState);
    }

    /**
     * Returns the current hole position based on the action given
     * ex. POPUP_HOLE_0 returns 0, POPUP_HOLE_4 returns 4
     * @param action
     * @return position
     */
    private int getHole(MoleBrain.Action action){
        return action.ordinal() - MoleBrain.Action.POPUP_HOLE_0.ordinal();
    }

    public int getPosition() {
        return currentPosition;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isAttacking() {
        return isAttacking;
    }
}
