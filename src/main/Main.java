package main;

import dao.TicketDAO;
import dao.TrainDAO;
import models.Passenger;
import models.Ticket;
import models.Train;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static TrainDAO trainDAO = new TrainDAO();
    static TicketDAO ticketDAO = new TicketDAO();

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   WELCOME TO RAILWAY BOOKING SYSTEM     ");
        System.out.println("==========================================");

        int choice;
        do {
            printMenu();
            choice = getInt("Enter your choice: ");

            switch (choice) {
                case 1 -> searchTrain();
                case 2 -> bookTicket();
                case 3 -> cancelTicket();
                case 4 -> checkPNR();
                case 5 -> viewHistory();
                case 0 -> System.out.println("\nThank you for using Railway Booking System. Goodbye!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    static void printMenu() {
        System.out.println("\n==========================================");
        System.out.println("  1. Search Train");
        System.out.println("  2. Book Ticket");
        System.out.println("  3. Cancel Ticket");
        System.out.println("  4. Check PNR Status");
        System.out.println("  5. View Booking History");
        System.out.println("  0. Exit");
        System.out.println("==========================================");
    }

    // ── 1. Search Train ─────────────────────────────────────
    static void searchTrain() {
        System.out.println("\n--- Search Train ---");
        System.out.print("Enter Source City: ");
        String source = sc.nextLine().trim();
        System.out.print("Enter Destination City: ");
        String destination = sc.nextLine().trim();

        List<Train> trains = trainDAO.searchTrains(source, destination);

        if (trains.isEmpty()) {
            System.out.println("No trains found for this route.");
        } else {
            System.out.println("\nAvailable Trains:");
            System.out.println("----------------------------------------------------------");
            for (Train t : trains) {
                System.out.println(t);
            }
            System.out.println("----------------------------------------------------------");
        }
    }

    // ── 2. Book Ticket ──────────────────────────────────────
    static void bookTicket() {
        System.out.println("\n--- Book Ticket ---");

        System.out.print("Enter Source City: ");
        String source = sc.nextLine().trim();
        System.out.print("Enter Destination City: ");
        String destination = sc.nextLine().trim();

        List<Train> trains = trainDAO.searchTrains(source, destination);

        if (trains.isEmpty()) {
            System.out.println("No trains available for this route.");
            return;
        }

        System.out.println("\nAvailable Trains:");
        for (Train t : trains) System.out.println(t);

        int trainId = getInt("Enter Train ID to book: ");
        boolean validTrain = false;

for (Train t : trains) {
    if (t.getTrainId() == trainId) {
        validTrain = true;
        break;
    }
}

if (!validTrain) {
    System.out.println("Please select a Train ID from the available trains shown above.");
    return;
}
        
        Train selected = trainDAO.getTrainById(trainId);

        if (selected == null) {
            System.out.println("Invalid Train ID.");
            return;
        }

        System.out.println("\nEnter Passenger Details:");
       String name;

while (true) {
    System.out.print("Name (Enter 0 to go back): ");
    name = sc.nextLine().trim();

    if (name.equals("0")) {
        return;
    }

    if (name.matches("[A-Za-z ]{2,50}")) {
        break;
    }

    System.out.println("Name should contain only letters and spaces.");
}
        int age = getInt("Age: ");
   String email;
   if(age < 1 || age > 120){
    System.out.println("Invalid Age");
    return;
}

while (true) {
    System.out.print("Email (Enter 0 to go back): ");
    email = sc.nextLine().trim();

    if (email.equals("0")) {
        return; // back to main menu
    }

    if (email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
        break;
    } else {
        System.out.println("Enter a valid Gmail address!");
    }
}
          if (ticketDAO.isAlreadyBooked(email, trainId)) {
    System.out.println("❌ You already have a confirmed ticket for this train.");
    return;
}
        Passenger passenger = new Passenger(name, age, email);
        if (ticketDAO.isAlreadyBooked(email, trainId)) {
    System.out.println("❌ You already have a confirmed ticket for this train.");
    return;
}
        int passengerId = ticketDAO.addPassenger(passenger);

        if (passengerId == -1) {
            System.out.println("Failed to add passenger.");
            return;
        }

        boolean seatReserved = trainDAO.decreaseSeat(trainId);
        if (!seatReserved) {
            System.out.println("Sorry, no seats available.");
            return;
        }

        int pnr = ticketDAO.bookTicket(passengerId, trainId);

        if (pnr != -1) {
            System.out.println("\n✅ Ticket Booked Successfully!");
            System.out.println("Your PNR Number: " + pnr);
            System.out.println("Train: " + selected.getTrainName());
            System.out.println("From: " + selected.getSource() + " → To: " + selected.getDestination());
            System.out.println("Departure: " + selected.getDepartureTime());
            System.out.println("Passenger: " + name);
            System.out.println("Please save your PNR number for future reference.");
        } else {
            System.out.println("Booking failed. Please try again.");
        }
    }

    // ── 3. Cancel Ticket ────────────────────────────────────
    static void cancelTicket() {
        System.out.println("\n--- Cancel Ticket ---");
        int pnr = getInt("Enter PNR Number: ");

        Ticket ticket = ticketDAO.getTicketByPNR(pnr);

        if (ticket == null) {
            System.out.println("No ticket found with PNR: " + pnr);
            return;
        }

        if (ticket.getStatus().equals("CANCELLED")) {
            System.out.println("This ticket is already cancelled.");
            return;
        }

        System.out.println(ticket);
        System.out.print("\nAre you sure you want to cancel? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            int trainId = ticketDAO.getTrainIdByPNR(pnr);
            boolean cancelled = ticketDAO.cancelTicket(pnr);

            if (cancelled) {
                trainDAO.increaseSeat(trainId);
                System.out.println("✅ Ticket cancelled successfully. Seat has been released.");
            } else {
                System.out.println("Cancellation failed. Please try again.");
            }
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    // ── 4. Check PNR Status ─────────────────────────────────
    static void checkPNR() {
        System.out.println("\n--- Check PNR Status ---");
        int pnr = getInt("Enter PNR Number: ");

        Ticket ticket = ticketDAO.getTicketByPNR(pnr);

        if (ticket != null) {
            System.out.println(ticket);
        } else {
            System.out.println("No ticket found with PNR: " + pnr);
        }
    }

    // ── 5. View Booking History ─────────────────────────────
    static void viewHistory() {
        System.out.println("\n--- View Booking History ---");
        System.out.print("Enter your Email: ");
        String email = sc.nextLine().trim();

        List<Ticket> tickets = ticketDAO.getBookingHistory(email);

        if (tickets.isEmpty()) {
            System.out.println("No bookings found for: " + email);
        } else {
            System.out.println("\nYour Booking History:");
            System.out.println("----------------------------------------------------------");
            for (Ticket t : tickets) System.out.println(t);
            System.out.println("----------------------------------------------------------");
        }
    }

    // ── Helper: safe int input ──────────────────────────────
    static int getInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int val = Integer.parseInt(sc.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}   