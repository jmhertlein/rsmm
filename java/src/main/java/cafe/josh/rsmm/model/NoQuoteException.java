package cafe.josh.rsmm.model;

/**
 * Created by joshua on 1/16/16.
 */
public class NoQuoteException extends Exception {
    public NoQuoteException(String item) {
        super("Could not find a quote for item " + item);
    }
}
