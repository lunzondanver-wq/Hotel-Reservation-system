package gui;

/** Panels implement this so MainFrame can reload their data when a tab is opened. */
public interface Refreshable { void refresh(); }