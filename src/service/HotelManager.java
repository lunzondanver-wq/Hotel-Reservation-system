package service;

import model.*;
import java.util.ArrayList;
import java.util.List;

/** All business logic lives here. The GUI only calls these methods. */
public class HotelManager {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Guest> guests = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private int nextReservationNo = 1;

    public HotelManager() { seedData(); }

    private void seedData() {
        try {
            addRoom("Standard", "101", "Garden view");
            addRoom("Standard", "102", "");
            addRoom("Deluxe", "201", "Sea view");
            addRoom("Suite", "301", "Top floor");
            addGuest("G001", "Ai Santos", "09171234567");
        } catch (HotelException e) { /* seed data is valid */ }
    }

    // ---------- ROOMS ----------
    public void addRoom(String type, String number, String description) throws HotelException {
        if (findRoom(number) != null) throw new HotelException("Room already exists: " + number);
        try {
            Room room;
            switch (type) {
                case "Standard" -> room = new StandardRoom(number, description);
                case "Deluxe" -> room = new DeluxeRoom(number, description);
                case "Suite" -> room = new SuiteRoom(number, description);
                default -> throw new HotelException("Unknown room type.");
            }
            rooms.add(room);   // POLYMORPHISM: list holds the parent type
        } catch (IllegalArgumentException e) { throw new HotelException(e.getMessage()); }
    }

    public List<Room> getRooms() { return rooms; }

    public List<Room> searchRooms(String keyword) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            String status = r.isAvailable() ? "available" : "booked";
            if (r.getRoomNumber().contains(k) || r.getRoomType().toLowerCase().contains(k)
                    || r.getDescription().toLowerCase().contains(k) || status.contains(k))
                result.add(r);
        }
        return result;
    }

    public void updateRoom(String number, String description) throws HotelException {
        Room r = requireRoom(number);
        try { r.setDescription(description); }
        catch (IllegalArgumentException e) { throw new HotelException(e.getMessage()); }
    }

    public void deleteRoom(String number) throws HotelException {
        Room r = requireRoom(number);
        if (!r.isAvailable()) throw new HotelException("Cannot delete: room is currently booked.");
        rooms.remove(r);
    }

    // ---------- GUESTS ----------
    public void addGuest(String id, String name, String phone) throws HotelException {
        if (findGuest(id) != null) throw new HotelException("Guest ID already exists: " + id);
        try { guests.add(new Guest(id, name, phone)); }
        catch (IllegalArgumentException e) { throw new HotelException(e.getMessage()); }
    }

    public List<Guest> getGuests() { return guests; }

    public void updateGuest(String id, String name, String phone) throws HotelException {
        Guest g = requireGuest(id);
        try { g.setName(name); g.setPhone(phone); }
        catch (IllegalArgumentException e) { throw new HotelException(e.getMessage()); }
    }

    public void deleteGuest(String id) throws HotelException {
        Guest g = requireGuest(id);
        for (Reservation r : reservations)
            if (r.getGuest() == g && r.getStatus() == Reservation.Status.BOOKED)
                throw new HotelException("Cannot delete: guest has an active reservation.");
        guests.remove(g);
    }

    // ---------- RESERVATIONS ----------
    public Reservation book(String guestId, String roomNumber, int nights, int guestCount) throws HotelException {
        Guest g = requireGuest(guestId);
        Room room = requireRoom(roomNumber);
        if (!room.isAvailable()) throw new HotelException("Room " + roomNumber + " is already booked.");
        try {
            Reservation res = new Reservation(String.format("R%03d", nextReservationNo), g, room, nights, guestCount);
            room.markBooked();
            nextReservationNo++;
            reservations.add(res);
            return res;
        } catch (IllegalArgumentException | IllegalStateException e) { throw new HotelException(e.getMessage()); }
    }

    public List<Reservation> getReservations() { return reservations; }

    public void checkOut(String reservationId) throws HotelException {
        try { requireReservation(reservationId).checkOut(); }
        catch (IllegalStateException e) { throw new HotelException(e.getMessage()); }
    }

    public void cancel(String reservationId) throws HotelException {
        try { requireReservation(reservationId).cancel(); }
        catch (IllegalStateException e) { throw new HotelException(e.getMessage()); }
    }

    public String getBill(String reservationId) throws HotelException {
        return requireReservation(reservationId).getBill();
    }

    // ---------- helpers ----------
    public Room findRoom(String number) {
        if (number == null) return null;
        for (Room r : rooms) if (r.getRoomNumber().equals(number.trim())) return r;
        return null;
    }
    public Guest findGuest(String id) {
        if (id == null) return null;
        for (Guest g : guests) if (g.getId().equalsIgnoreCase(id.trim())) return g;
        return null;
    }
    private Room requireRoom(String number) throws HotelException {
        Room r = findRoom(number);
        if (r == null) throw new HotelException("Room not found: " + number);
        return r;
    }
    private Guest requireGuest(String id) throws HotelException {
        Guest g = findGuest(id);
        if (g == null) throw new HotelException("Guest not found: " + id);
        return g;
    }
    private Reservation requireReservation(String id) throws HotelException {
        for (Reservation r : reservations) if (r.getId().equals(id)) return r;
        throw new HotelException("Reservation not found: " + id);
    }
}