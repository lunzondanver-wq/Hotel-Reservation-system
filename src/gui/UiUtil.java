package gui;

import service.HotelException;

import javax.swing.*;
import java.awt.Component;

/** Shared helper: runs an action and shows error dialogs, so panels stay short. */
public final class UiUtil {
    private UiUtil() {}

    @FunctionalInterface
    public interface Action { void run() throws Exception; }

    public static void run(Component parent, Action action) {
        try { action.run(); }
        catch (HotelException ex) { error(parent, ex.getMessage()); }
        catch (Exception ex) { error(parent, "Unexpected error: " + ex.getMessage()); }
    }
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}