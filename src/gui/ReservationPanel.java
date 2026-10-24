package gui;

import model.Guest;
import model.Reservation;
import model.Room;
import service.HotelException;
import service.HotelManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Book rooms, check out, cancel, and view the bill. */
public class ReservationPanel extends JPanel implements Refreshable {
    private final HotelManager manager;
    private final JComboBox<Guest> guestBox = new JComboBox<>();
    private final JComboBox<Room> roomBox = new JComboBox<>();
    private final JSpinner nightsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
    private final JSpinner guestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JLabel estimateLabel = new JLabel("Estimated total: PHP 0.00");
    private final JTextArea billArea = new JTextArea(8, 40);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Res. ID", "Guest", "Room", "Type", "Nights", "Guests", "Total (PHP)", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public ReservationPanel(HotelManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel bookP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookP.setBorder(BorderFactory.createTitledBorder("New Reservation"));
        JButton bookBtn = new JButton("Book");
        bookP.add(new JLabel("Guest:")); bookP.add(guestBox);
        bookP.add(new JLabel("Room:")); bookP.add(roomBox);
        bookP.add(new JLabel("Nights:")); bookP.add(nightsSpinner);
        bookP.add(new JLabel("Guests:")); bookP.add(guestsSpinner);
        bookP.add(bookBtn); bookP.add(estimateLabel);
        add(bookP, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(4, 4));
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton billBtn = new JButton("Show Bill"), outBtn = new JButton("Check-out"), cancelBtn = new JButton("Cancel Reservation");
        btns.add(billBtn); btns.add(outBtn); btns.add(cancelBtn);
        billArea.setEditable(false);
        billArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        south.add(btns, BorderLayout.NORTH);
        south.add(new JScrollPane(billArea), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        roomBox.addActionListener(e -> updateEstimate());
        nightsSpinner.addChangeListener(e -> updateEstimate());

        bookBtn.addActionListener(e -> UiUtil.run(this, () -> {
            Guest g = (Guest) guestBox.getSelectedItem();
            Room r = (Room) roomBox.getSelectedItem();
            if (g == null) throw new HotelException("Add a guest first.");
            if (r == null) throw new HotelException("No available room to book.");
            Reservation res = manager.book(g.getId(), r.getRoomNumber(), (Integer) nightsSpinner.getValue(), (Integer) guestsSpinner.getValue());
            billArea.setText(res.getBill());
            refresh();
        }));
        billBtn.addActionListener(e -> UiUtil.run(this, () -> billArea.setText(manager.getBill(selectedId()))));
        outBtn.addActionListener(e -> UiUtil.run(this, () -> {
            manager.checkOut(selectedId());
            billArea.setText(manager.getBill(selectedId()));
            refresh();
        }));
        cancelBtn.addActionListener(e -> UiUtil.run(this, () -> {
            String id = selectedId();
            int ok = JOptionPane.showConfirmDialog(this, "Cancel reservation " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) { manager.cancel(id); refresh(); }
        }));
        refresh();
    }

    private String selectedId() throws HotelException {
        int r = table.getSelectedRow();
        if (r < 0) throw new HotelException("Select a reservation from the table first.");
        return (String) model.getValueAt(r, 0);
    }

    private void updateEstimate() {
        Room r = (Room) roomBox.getSelectedItem();
        if (r == null) { estimateLabel.setText("Estimated total: PHP 0.00"); return; }
        double total = r.calculateTotal((Integer) nightsSpinner.getValue());   // POLYMORPHISM
        estimateLabel.setText(String.format("Estimated total: PHP %.2f", total));
    }

    @Override public void refresh() {
        int sel = table.getSelectedRow();
        guestBox.removeAllItems(); roomBox.removeAllItems();
        for (Guest g : manager.getGuests()) guestBox.addItem(g);
        for (Room r : manager.getRooms()) if (r.isAvailable()) roomBox.addItem(r);
        model.setRowCount(0);
        for (Reservation r : manager.getReservations())
            model.addRow(new Object[]{r.getId(), r.getGuest().getName(), r.getRoom().getRoomNumber(),
                    r.getRoom().getRoomType(), r.getNights(), r.getGuestCount(),
                    String.format("%.2f", r.getTotal()), r.getStatus()});
        if (sel >= 0 && sel < model.getRowCount()) table.setRowSelectionInterval(sel, sel);
        updateEstimate();
    }
}