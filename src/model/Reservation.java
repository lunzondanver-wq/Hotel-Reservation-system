package model;

import java.time.LocalDate;

/** Links a Guest and a Room. Computes the total through the Room's own rule. */
public class Reservation {
    public enum Status { BOOKED, CHECKED_OUT, CANCELLED }

    private final String id;
    private final Guest guest;
    private final Room room;
    private final int nights;
    private final int guestCount;
    private final LocalDate checkIn;
    private final double total;
    private Status status = Status.BOOKED;

    public Reservation(String id, Guest guest, Room room, int nights, int guestCount) {
        if (nights < 1 || nights > 365) throw new IllegalArgumentException("Nights must be between 1 and 365.");
        if (guestCount < 1) throw new IllegalArgumentException("At least 1 guest is required.");
        if (guestCount > room.getMaxGuests())
            throw new IllegalArgumentException(room.getRoomType() + " rooms allow up to " + room.getMaxGuests() + " guests.");
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.nights = nights;
        this.guestCount = guestCount;
        this.checkIn = LocalDate.now();
        this.total = room.calculateTotal(nights);   // POLYMORPHISM: Room type decides the formula
    }

    public void checkOut() {
        if (status != Status.BOOKED) throw new IllegalStateException("Reservation is already " + status + ".");
        status = Status.CHECKED_OUT;
        room.markAvailable();
    }
    public void cancel() {
        if (status != Status.BOOKED) throw new IllegalStateException("Reservation is already " + status + ".");
        status = Status.CANCELLED;
        room.markAvailable();
    }

    public String getBill() {
        return "===== HOTEL BILL =====\n"
             + "Reservation: " + id + "\n"
             + "Guest: " + guest.getName() + "\n"
             + "Room: " + room.getRoomNumber() + " (" + room.getRoomType() + ")\n"
             + "Check-in: " + checkIn + "\n"
             + "Nights: " + nights + " x PHP " + String.format("%.2f", room.getRatePerNight()) + "\n"
             + "Guests: " + guestCount + "\n"
             + "Status: " + status + "\n"
             + "TOTAL: PHP " + String.format("%.2f", total) + "\n";
    }

    public String getId() { return id; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public int getNights() { return nights; }
    public int getGuestCount() { return guestCount; }
    public LocalDate getCheckIn() { return checkIn; }
    public double getTotal() { return total; }
    public Status getStatus() { return status; }
}