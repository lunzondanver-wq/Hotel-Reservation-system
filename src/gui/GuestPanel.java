package gui;

import model.Guest;
import service.HotelException;
import service.HotelManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuestPanel extends JPanel implements Refreshable {
    private final HotelManager manager;
    private final JTextField idField = new JTextField(8);
    private final JTextField nameField = new JTextField(16);
    private final JTextField phoneField = new JTextField(12);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Phone"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public GuestPanel(HotelManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add"), updateBtn = new JButton("Update"),
                deleteBtn = new JButton("Delete"), clearBtn = new JButton("Clear");
        form.add(new JLabel("ID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Phone:")); form.add(phoneField);
        form.add(addBtn); form.add(updateBtn); form.add(deleteBtn); form.add(clearBtn);
        add(form, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (e.getValueIsAdjusting() || r < 0) return;
            idField.setText((String) model.getValueAt(r, 0));
            nameField.setText((String) model.getValueAt(r, 1));
            phoneField.setText((String) model.getValueAt(r, 2));
            idField.setEditable(false);
        });
        addBtn.addActionListener(e -> UiUtil.run(this, () -> {
            manager.addGuest(idField.getText(), nameField.getText(), phoneField.getText());
            clear(); refresh();
        }));
        updateBtn.addActionListener(e -> UiUtil.run(this, () -> {
            manager.updateGuest(idField.getText(), nameField.getText(), phoneField.getText());
            refresh();
        }));
        deleteBtn.addActionListener(e -> UiUtil.run(this, () -> {
            if (idField.getText().isBlank()) throw new HotelException("Select a guest to delete.");
            int ok = JOptionPane.showConfirmDialog(this, "Delete guest " + idField.getText() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) { manager.deleteGuest(idField.getText()); clear(); refresh(); }
        }));
        clearBtn.addActionListener(e -> clear());
        refresh();
    }

    private void clear() {
        idField.setText(""); nameField.setText(""); phoneField.setText("");
        idField.setEditable(true); table.clearSelection();
    }

    @Override public void refresh() {
        model.setRowCount(0);
        for (Guest g : manager.getGuests()) model.addRow(new Object[]{g.getId(), g.getName(), g.getPhone()});
    }
}