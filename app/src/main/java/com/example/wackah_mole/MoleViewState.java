package com.example.wackah_mole;

/**
 * Class which represents only the UI important Mole information
 */
public class MoleViewState {
    private final int id;
    public final int position;
    public final boolean isVisible;
    public final boolean isAttacking;
    public boolean canBeHit;

    public MoleViewState(int id, int position, boolean isVisible, boolean isAttacking, boolean canBeHit){
        this.id = id;
        this.position = position;
        this.isVisible = isVisible;
        this.isAttacking = isAttacking;
        this.canBeHit = canBeHit;
    }

    public int getPosition(){
        return position;
    }
    public boolean isVisible(){
        return isVisible;
    }
    public boolean isAttacking(){
        return isAttacking;
    }
    public boolean canBeHit(){return canBeHit;}
    public void setCanBeHit(boolean canBeHit) {this.canBeHit = canBeHit;}
}
