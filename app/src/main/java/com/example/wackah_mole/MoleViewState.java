package com.example.wackah_mole;

/**
 * <p>
 * Class which represents only the UI important Mole information
 * </p>
 *
 * @author John Brittain
 */
public class MoleViewState {
    private final int id;
    public final int position;
    public final boolean isVisible;
    public final boolean isAttacking;
    public boolean canBeHit;

    /**
     * Creates a new MoleViewState, containing information utilized by the UI
     * @param id id of the view state
     * @param position position of the ViewState
     * @param isVisible indicates if a mole is visible
     * @param isAttacking indicates if a mole is attacking
     * @param canBeHit indicates if a mole can be hit or not
     */
    public MoleViewState(int id, int position, boolean isVisible, boolean isAttacking, boolean canBeHit){
        this.id = id;
        this.position = position;
        this.isVisible = isVisible;
        this.isAttacking = isAttacking;
        this.canBeHit = canBeHit;
    }

    /**
     * Returns the position of a mole
     * @return position
     */
    public int getPosition(){
        return position;
    }

    /**
     * Returns if a mole is visible
     * @return isVisible
     */
    public boolean isVisible(){
        return isVisible;
    }

    /**
     * Returns if a mole is attacking
     * @return isAttacking
     */
    public boolean isAttacking(){
        return isAttacking;
    }

    /**
     * Returns canBeHIt
     * @return canBeHIt
     */
    public boolean canBeHit(){return canBeHit;}

    /**
     * Sets canBeHit
     * @param canBeHit Indicates if a mole can be hit or not
     */
    public void setCanBeHit(boolean canBeHit) {this.canBeHit = canBeHit;}
}
