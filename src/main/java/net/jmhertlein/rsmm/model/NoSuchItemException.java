package net.jmhertlein.rsmm.model;

/**
 * Created by joshua on 3/25/16.
 */
public class NoSuchItemException extends Exception {
    public NoSuchItemException(String name) {
        super("No such item with name " + name);
    }
}
