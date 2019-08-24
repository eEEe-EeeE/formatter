package normalization.bean;

public class Type {

    public static final String ADDRESS = "address";

    public static final String ADDRESSGROUP = "addressGroup";

    public static final String SERVICE = "service";

    public static final String SERVICEGROUP = "serviceGroup";

    public static final String ABSOLUTETIME = "absoluteTime";

    public static final String PERIODICTIME = "periodicTime";

    public static final String INTERFACE = "interface";

    public static final String ZONE = "zone";

    public static final String POLICY = "policy";

    public static final String PARTITION_HYBRID = "hybrid";

    public static final String UNKNOWN = "unknown";

    private String analyticalType;

    public Type(String value) {
        this.analyticalType = value;
    }

    public String getValue() {
        return analyticalType;
    }

    public void setValue(String analyticalType) {
        this.analyticalType = analyticalType;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Type) {

            Type other = (Type) obj;

            return getValue().equals(other.getValue());
        }

        return false;
    }

}
