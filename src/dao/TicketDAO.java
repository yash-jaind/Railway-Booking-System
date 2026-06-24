package dao;

import db.DBConnection;
import models.Passenger;
import models.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    // Add new passenger and return generated ID
    public int addPassenger(Passenger passenger) {
        String sql = "INSERT INTO passengers (name, age, email) VALUES (?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, passenger.getName());
            ps.setInt(2, passenger.getAge());
            ps.setString(3, passenger.getEmail());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) generatedId = rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error adding passenger: " + e.getMessage());
        }
        return generatedId;
    }

    // Book ticket and return PNR
    public int bookTicket(int passengerId, int trainId) {
        String sql = "INSERT INTO bookings (passenger_id, train_id, booking_date, status) VALUES (?, ?, CURDATE(), 'CONFIRMED')";
        int pnr = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, passengerId);
            ps.setInt(2, trainId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) pnr = rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error booking ticket: " + e.getMessage());
        }
        return pnr;
    }
public boolean isAlreadyBooked(String email, int trainId) {

    String sql = """
        SELECT COUNT(*)
        FROM bookings b
        JOIN passengers p
        ON b.passenger_id = p.passenger_id
        WHERE p.email = ?
        AND b.train_id = ?
        AND b.status = 'CONFIRMED'
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, email);
        ps.setInt(2, trainId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }

    return false;
}
    // Cancel ticket by PNR
    public boolean cancelTicket(int pnr) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE pnr = ? AND status = 'CONFIRMED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pnr);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error cancelling ticket: " + e.getMessage());
        }
        return false;
    }

    // Get ticket by PNR
    public Ticket getTicketByPNR(int pnr) {
        String sql = "SELECT b.pnr, p.name, t.train_name, t.source, t.destination, b.booking_date, b.status, b.train_id " +
                     "FROM bookings b " +
                     "JOIN passengers p ON b.passenger_id = p.passenger_id " +
                     "JOIN trains t ON b.train_id = t.train_id " +
                     "WHERE b.pnr = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pnr);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Ticket(
                    rs.getInt("pnr"),
                    rs.getString("name"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("booking_date"),
                    rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching ticket: " + e.getMessage());
        }
        return null;
    }

    // Get train_id from PNR (for seat restoration on cancel)
    public int getTrainIdByPNR(int pnr) {
        String sql = "SELECT train_id FROM bookings WHERE pnr = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pnr);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("train_id");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return -1;
    }

    // View all bookings for a passenger by email
    public List<Ticket> getBookingHistory(String email) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT b.pnr, p.name, t.train_name, t.source, t.destination, b.booking_date, b.status " +
                     "FROM bookings b " +
                     "JOIN passengers p ON b.passenger_id = p.passenger_id " +
                     "JOIN trains t ON b.train_id = t.train_id " +
                     "WHERE LOWER(p.email) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tickets.add(new Ticket(
                    rs.getInt("pnr"),
                    rs.getString("name"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("booking_date"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching history: " + e.getMessage());
        }
        return tickets;
    }
}