package cafe.josh.rsmm.model;

public enum RSType {
    RS3("rs3"),
    OSRS("osrs");

    private final String enumString;

    RSType(String enumString) {
        this.enumString = enumString;
    }


    @Override
    public String toString() {
        return this.enumString;
    }

    public String getEnumString() {
        return enumString;
    }
}
