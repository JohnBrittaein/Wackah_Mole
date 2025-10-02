package com.example.wackah_mole;

import android.util.Log;

import java.util.Random;

/**
 * The MoleBrain class represents the Mole's ability to learn
 * MoleBrain is implemented in a "Reinforcement Learning" approach
 * Where the state is the grid of holes, and which position the mole is currently in
 * A reward is given with the mole successfully hides from the player, or hits the player back
 * A penalty is given when the mole is hit
 */
public class MoleBrain {
    private static final int NUM_ACTIONS = Action.values().length;
    private static final int NUM_HOLES = 15;
    private static final int NUM_STATES = NUM_HOLES * NUM_ACTIONS;
    private double[][] qTable = new double[NUM_STATES][NUM_ACTIONS];
    private double learningRate = 0.1;
    private double discountFactor = 0.9;
    private double epsilon = 0.1;
    private Random random = new Random();

    public enum Action{
        HIDE, 
        ATTACK,
        POPUP_HOLE_0, POPUP_HOLE_1, POPUP_HOLE_2,
        POPUP_HOLE_3, POPUP_HOLE_4, POPUP_HOLE_5,
        POPUP_HOLE_6, POPUP_HOLE_7, POPUP_HOLE_8,
        POPUP_HOLE_9, POPUP_HOLE_10, POPUP_HOLE_11,
        POPUP_HOLE_12, POPUP_HOLE_13,POPUP_HOLE_14
    }
    public Action decideAction(GameState state){
        int stateIndex = state.getStateIndex();

        // Uses an epsilon-greedy algorithm which balances between exploitation and exploration
        if (random.nextDouble() < epsilon) {
            // Exploring: choose a random action
            int randomActionIndex = random.nextInt(NUM_ACTIONS);
            return Action.values()[randomActionIndex];
        } else {
            // Exploitation: choose the best action
            int bestActionIndex = 0;
            double maxQ = qTable[stateIndex][0];

            for (int i = 1; i < NUM_ACTIONS; i++){
                if(qTable[stateIndex][i] > maxQ){
                    maxQ = qTable[stateIndex][i];
                    bestActionIndex = i;
                }
            }
            return Action.values()[bestActionIndex];
        }
    }
    public void updateQtable(GameState state, Action action, Double reward, GameState nextState){
        int stateIndex = state.getStateIndex();
        int actionIndex = action.ordinal();
        int nextStateIndex = nextState.getStateIndex();

        double currentQ = qTable[stateIndex][actionIndex];
        double maxQ = getMaxQ(nextStateIndex);

        double updatedQ = currentQ + learningRate * (reward + discountFactor * maxQ - currentQ);
        qTable[stateIndex][actionIndex] = updatedQ;
    }

    private double getMaxQ(int stateIndex){
        double maxQ = qTable[stateIndex][0];
        for(int i = 0; i < NUM_ACTIONS; i++){
            if(qTable[stateIndex][i] > maxQ){
              maxQ = qTable[stateIndex][i];
            }
        }
        return maxQ;
    }

    //public double[][] loadQTable(){}

    //public double[][] saveQTable(){}

}
