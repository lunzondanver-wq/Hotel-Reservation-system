package model;

/** POLYMORPHISM: same method name as StandardRoom, different computation. */
public class DeluxeRoom extends Room {
    private static final double BREAKFAST_FEE = 300.0;   // per night

    public DeluxeRoom(String number, String description) { super(number, description); }

    @Override public String getRoomType() { return "Deluxe"; }
    @Override public double getRatePerNight() { return 2500.0; }
    @Override public int getMaxGuests() { return 3; }
    @Override public String getAmenities() { return "AC, TV, Mini-fridge, Free breakfast"; }

    @Override public double calculateTotal(int nights) {
        validateNights(nights);
        return (getRatePerNight() + BREAKFAST_FEE) * nights;
    }
}