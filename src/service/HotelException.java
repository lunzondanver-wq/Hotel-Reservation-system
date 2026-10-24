package service;

/** Custom checked exception for business-rule errors. */
public class HotelException extends Exception {
    public HotelException(String message) { super(message); }
}