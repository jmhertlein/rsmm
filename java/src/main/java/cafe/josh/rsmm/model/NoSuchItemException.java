package cafe.josh.rsmm.model;

/**
 * Created by joshua on 3/25/16.
 */
public class NoSuchItemException extends Exception {
    public NoSuchItemException(String name) {
        super("No such item with name " + name);
    }

    public NoSuchItemException(int id) {
        super("No such item with id " + id);
    }
}
