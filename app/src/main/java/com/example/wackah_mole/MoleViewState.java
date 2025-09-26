package com.example.wackah_mole;

/**
 * Class which represents only the UI important Mole information
 */
public class MoleViewState {
    private final int id;
    public final int position;
    public final boolean isVisible;
    public final boolean isAttacking;
    public MoleViewState(int id, int position, boolean isVisible, boolean isAttacking){
        this.id = id;
        this.position = position;
        this.isVisible = isVisible;
        this.isAttacking = isAttacking;
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
}
