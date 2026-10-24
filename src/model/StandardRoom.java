package model;

/** INHERITANCE: StandardRoom is-a Room. Simple: rate x nights. */
public class StandardRoom extends Room {
    public StandardRoom(String number, String description) { super(number, description); }

    @Override public String getRoomType() { return "Standard"; }
    @Override public double getRatePerNight() { return 1500.0; }
    @Override public int getMaxGuests() { return 2; }
    @Override public String getAmenities() { return "Fan/AC, TV"; }

    @Override public double calculateTotal(int nights) {
        validateNights(nights);
        return getRatePerNight() * nights;
    }
}