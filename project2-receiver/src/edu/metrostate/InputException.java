package edu.metrostate;

/**
 * ICS460-01 Fall2021, Project 2, stop and wait, Receiver program - server side.
 * Instructor: Damodar Chetty Write by Team #3: Nalongsone Danddank
 */
public class InputException extends Exception {
    private static final long serialVersionUID = 1L;

    public InputException(String message) {
        super(message);
    }

    public InputException() {
    }

}
