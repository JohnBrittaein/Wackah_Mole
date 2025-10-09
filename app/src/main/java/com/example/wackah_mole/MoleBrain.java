package com.example.wackah_mole;

import android.util.Log;

import java.util.Random;

/**
 * The MoleBrain class represents the Mole's ability to learn
 * MoleBrain is implemented in a "Reinforcement Learning" approach
 * Where the state is the grid of holes, and which position the mole is currently in
 * A reward is given with the mole successfully hides from the player, or hits the player back
 * A penalty is given when the mole is hit
 *
 * @author John Brttain
 */
public class MoleBrain {
    private static final int NUM_ACTIONS = Action.values().length;
    private static final int NUM_HOLES = 15;
    private static final double LEARNING_RATE = 0.1;
    private static final double DISCOUNT_FACTOR = 0.9;
    private static final double EPSILON = 0.3;
    private final double[][] qTable = new double[NUM_HOLES][NUM_ACTIONS];
    private final Random random = new Random();

    public enum Action{
        HIDE, 
        ATTACK,
        POPUP_HOLE_0, POPUP_HOLE_1, POPUP_HOLE_2,
        POPUP_HOLE_3, POPUP_HOLE_4, POPUP_HOLE_5,
        POPUP_HOLE_6, POPUP_HOLE_7, POPUP_HOLE_8,
        POPUP_HOLE_9, POPUP_HOLE_10, POPUP_HOLE_11,
        POPUP_HOLE_12, POPUP_HOLE_13,POPUP_HOLE_14
    }

    /**
     * Decides the next action the mole will take
     * @param state current state of the game
     * @return action
     */
    public Action decideAction(GameState state){
        int stateIndex = state.getStateIndex();

        // Uses an epsilon-greedy algorithm which balances between exploitation and exploration
        if (random.nextDouble() < EPSILON) {
            // Exploring: choose a random action
            int randomActionIndex = random.nextInt(NUM_ACTIONS);
            //Log.d("MoleBrain", "Exploring: state=" + stateIndex + "action=" + Action.values()[randomActionIndex]);
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
            //Log.d("MoleBrain", "Exploiting: state=" + stateIndex + "action=" + Action.values()[bestActionIndex]);
            return Action.values()[bestActionIndex];
        }
    }

    /**
     * Performs a Q-learning update on the Q-table for a given state-action pair.
     * <p>
     * This method adjusts the Q-value for the provided {@code (state, action)} using the
     * standard Q-learning update rule:
     * <pre>
     * Q(s, a) ← Q(s, a) + α * (r + γ * max(Q(s', ·)) - Q(s, a))
     * </pre>
     * where:
     * <ul>
     *   <li>α (LEARNING_RATE) is the learning rate</li>
     *   <li>γ (DISCOUNT_FACTOR) is the discount factor</li>
     *   <li>r is the received reward</li>
     *   <li>s' is the next state</li>
     * </ul>
     *
     * @param state      the current game state (s)
     * @param action     the action taken from the current state (a)
     * @param reward     the immediate reward received after taking the action
     * @param nextState  the resulting next game state (s')
     */
    public void updateQtable(GameState state, Action action, Double reward, GameState nextState){
        int stateIndex = state.getStateIndex();
        int actionIndex = action.ordinal();
        int nextStateIndex = nextState.getStateIndex();

        if (actionIndex >= NUM_ACTIONS){
            //Log.w("MoleBrain", "Invalid actionIndex: " + actionIndex);
            return;
        }

        double currentQ = qTable[stateIndex][actionIndex];
        double maxQ = getMaxQ(nextStateIndex);
        double updatedQ = currentQ + LEARNING_RATE * (reward + DISCOUNT_FACTOR * maxQ - currentQ);
        qTable[stateIndex][actionIndex] = updatedQ;
        //Log.d("MoleBrain", "Qupdate s=" + stateIndex + " a=" + action + " r=" + reward + " -> " + updatedQ);
    }

    /**
     * Returns the maximum Q-value among all possible actions for the specified state.
     *
     * @param stateIndex the index of the game state for which to find the maximum Q-value
     * @return the maximum Q-value for the given state
     */
    private double getMaxQ(int stateIndex){
        stateIndex = clampStateIndex(stateIndex);
        double maxQ = qTable[stateIndex][0];
        for(int i = 0; i < NUM_ACTIONS; i++){
            if(qTable[stateIndex][i] > maxQ){
              maxQ = qTable[stateIndex][i];
            }
        }
        return maxQ;
    }

    /**
     * Keep the state index within the valid range
     * @param stateIndex the index of the stateIndex
     * @return state index with range [0 - NUM_HOLES-1]
     */
    private int clampStateIndex(int stateIndex){
        if (stateIndex < 0) return 0;
        if (stateIndex >= NUM_HOLES) return NUM_HOLES - 1;
        return stateIndex;
    }

    //public double[][] loadQTable(){}

    //public double[][] saveQTable(){}

}
