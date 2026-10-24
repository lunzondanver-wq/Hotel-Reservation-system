package model;

/** ABSTRACTION: contract for anything that can compute a bill. */
public interface Billable {
    double calculateTotal(int nights);
}