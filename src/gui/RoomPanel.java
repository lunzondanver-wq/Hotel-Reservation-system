package gui;

import model.Room;
import service.HotelException;
import service.HotelManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomPanel extends JPanel implements Refreshable {
    private final HotelManager manager;
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"});
    private final JTextField numberField = new JTextField(6);
    private final JTextField descField = new JTextField(18);
    private final JTextField searchField = new JTextField(15);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Room No.", "Type", "Rate/Night", "Max Guests", "Amenities", "Description", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public RoomPanel(HotelManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Search"), showAllBtn = new JButton("Show All");
        top.add(new JLabel("Search (number, type, status):")); top.add(searchField);
        top.add(searchBtn); top.add(showAllBtn);
        add(top, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add"), updateBtn = new JButton("Update Description"),
                deleteBtn = new JButton("Delete"), clearBtn = new JButton("Clear");
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Room No.:")); form.add(numberField);
        form.add(new JLabel("Description:")); form.add(descField);
        form.add(addBtn); form.add(updateBtn); form.add(deleteBtn); form.add(clearBtn);
        add(form, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (e.getValueIsAdjusting() || r < 0) return;
            numberField.setText((String) model.getValueAt(r, 0));
            typeBox.setSelectedItem(model.getValueAt(r, 1));
            descField.setText((String) model.getValueAt(r, 5));
            numberField.setEditable(false);
            typeBox.setEnabled(false);
        });
        addBtn.addActionListener(e -> UiUtil.run(this, () -> {
            manager.addRoom((String) typeBox.getSelectedItem(), numberField.getText(), descField.getText());
            clear(); refresh();
        }));
        updateBtn.addActionListener(e -> UiUtil.run(this, () -> {
            manager.updateRoom(numberField.getText(), descField.getText());
            refresh();
        }));
        deleteBtn.addActionListener(e -> UiUtil.run(this, () -> {
            if (numberField.getText().isBlank()) throw new HotelException("Select a room to delete.");
            int ok = JOptionPane.showConfirmDialog(this, "Delete room " + numberField.getText() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) { manager.deleteRoom(numberField.getText()); clear(); refresh(); }
        }));
        clearBtn.addActionListener(e -> clear());
        searchBtn.addActionListener(e -> load(manager.searchRooms(searchField.getText())));
        showAllBtn.addActionListener(e -> { searchField.setText(""); refresh(); });
        refresh();
    }

    private void clear() {
        numberField.setText(""); descField.setText("");
        numberField.setEditable(true); typeBox.setEnabled(true);
        table.clearSelection();
    }

    private void load(List<Room> list) {
        model.setRowCount(0);
        for (Room r : list)   // POLYMORPHISM: each call resolves to the room's own subclass
            model.addRow(new Object[]{r.getRoomNumber(), r.getRoomType(), String.format("%.2f", r.getRatePerNight()),
                    r.getMaxGuests(), r.getAmenities(), r.getDescription(), r.isAvailable() ? "Available" : "Booked"});
    }

    @Override public void refresh() { load(manager.getRooms()); }
}