package models;

public class Ticket {

    private int pnr;
    private int passengerId;
    private int trainId;
    private String bookingDate;
    private String status;
    private String passengerName;
    private String trainName;
    private String source;
    private String destination;

    public Ticket(int pnr, int passengerId, int trainId, String bookingDate, String status) {
        this.pnr = pnr;
        this.passengerId = passengerId;
        this.trainId = trainId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public Ticket(int pnr, String passengerName, String trainName,
                  String source, String destination, String bookingDate, String status) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public int getPnr() { return pnr; }
    public int getPassengerId() { return passengerId; }
    public int getTrainId() { return trainId; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
    public String getPassengerName() { return passengerName; }
    public String getTrainName() { return trainName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }

    @Override
    public String toString() {
        return "\n=== Ticket Details ===" +
               "\nPNR: " + pnr +
               "\nPassenger: " + passengerName +
               "\nTrain: " + trainName +
               "\nFrom: " + source +
               "\nTo: " + destination +
               "\nDate: " + bookingDate +
               "\nStatus: " + status;
    }
}   