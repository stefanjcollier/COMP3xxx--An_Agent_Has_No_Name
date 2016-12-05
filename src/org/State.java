package org;

/**
 * Store useful simulation data, to be accessed during strategy computation.
 *
 * e.g. CI := competing index
 *
 * Created by vlad on 04/12/2016.
 */
public class State {

    private static State INSTANCE;

    private State() {};



    public static State getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new State();
        }
        return INSTANCE;
    };
}
