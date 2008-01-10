package com.flaptor.util;

/**
 * This enum simply represents the possible states of a class that
 * can be stopped with an intermediate "stopping" state.
 * @see Stoppable
 */
public enum RunningState {
    RUNNING,
    STOPPING,
    STOPPED
}
