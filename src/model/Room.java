package model;

/**
 * ABSTRACTION: abstract parent class, cannot be instantiated.
 * ENCAPSULATION: private fields, controlled access through validated methods.
 */
public abstract class Room implements Billable {
    private final String roomNumber;
    private String description;
    private boolean available = true;

    protected Room(String roomNumber, String description) {
        if (roomNumber == null || !roomNumber.trim().matches("\\d{3,4}"))
            throw new IllegalArgumentException("Room number must be 3-4 digits (e.g. 101).");
        this.roomNumber = roomNumber.trim();
        setDescription(description);
    }

    // Each child MUST define these (POLYMORPHISM)
    public abstract String getRoomType();
    public abstract double getRatePerNight();
    public abstract int getMaxGuests();
    public abstract String getAmenities();

    // Shared by all children (INHERITANCE)
    protected void validateNights(int nights) {
        if (nights <= 0) throw new IllegalArgumentException("Nights must be at least 1.");
    }
    public void markBooked() {
        if (!available) throw new IllegalStateException("Room is already booked.");
        available = false;
    }
    public void markAvailable() { available = true; }

    public String getRoomNumber() { return roomNumber; }
    public String getDescription() { return description; }
    public boolean isAvailable() { return available; }
    public void setDescription(String description) {
        String d = description == null ? "" : description.trim();
        if (d.length() > 50) throw new IllegalArgumentException("Description must be 50 characters or less.");
        this.description = d;
    }

    @Override public String toString() { return roomNumber + " (" + getRoomType() + ")"; }
}