package gui;

import service.HotelManager;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Hotel Reservation System");
        HotelManager manager = new HotelManager();   // one shared manager for all panels

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Rooms", new RoomPanel(manager));
        tabs.addTab("Guests", new GuestPanel(manager));
        tabs.addTab("Reservations", new ReservationPanel(manager));

        // Reload the data of whichever tab is opened
        tabs.addChangeListener(e -> ((Refreshable) tabs.getSelectedComponent()).refresh());

        add(tabs);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 520);
        setLocationRelativeTo(null);
    }
}