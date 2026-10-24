package model;

/** ENCAPSULATION: private fields, validated setters. */
public class Guest {
    private final String id;
    private String name;
    private String phone;

    public Guest(String id, String name, String phone) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Guest ID is required.");
        this.id = id.trim();
        setName(name);
        setPhone(phone);
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Guest name is required.");
        this.name = name.trim();
    }
    public void setPhone(String phone) {
        if (phone == null || !phone.trim().matches("\\d{10,11}"))
            throw new IllegalArgumentException("Phone must be 10-11 digits.");
        this.phone = phone.trim();
    }
    @Override public String toString() { return name + " (" + id + ")"; }
}