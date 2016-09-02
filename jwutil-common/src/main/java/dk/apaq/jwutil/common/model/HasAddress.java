package dk.apaq.jwutil.common.model;


public interface HasAddress {

    String getAddress();

    String getAppartment();

    String getCity();

    String getCountryCode();

    /**
     * Location coordinate as [LONGITUDE,LATITUDE]
     * @return The coordinate.
     */
    double[] getLocation();

    String getPostalCode();

    
    
}