package model;

/** POLYMORPHISM: 10% discount for 5+ nights, plus a flat concierge fee. */
public class SuiteRoom extends Room {
    private static final double CONCIERGE_FEE = 500.0;   // flat per stay

    public SuiteRoom(String number, String description) { super(number, description); }

    @Override public String getRoomType() { return "Suite"; }
    @Override public double getRatePerNight() { return 5000.0; }
    @Override public int getMaxGuests() { return 5; }
    @Override public String getAmenities() { return "AC, TV, Living area, Breakfast, Concierge"; }

    @Override public double calculateTotal(int nights) {
        validateNights(nights);
        double base = getRatePerNight() * nights;
        if (nights >= 5) base *= 0.90;
        return base + CONCIERGE_FEE;
    }
}