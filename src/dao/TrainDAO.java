package dao;

import db.DBConnection;
import models.Train;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

    // Search trains by source and destination
    public List<Train> searchTrains(String source, String destination) {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT * FROM trains WHERE LOWER(source) = LOWER(?) AND LOWER(destination) = LOWER(?) AND available_seats > 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, source);
            ps.setString(2, destination);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                trains.add(new Train(
                    rs.getInt("train_id"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("departure_time"),
                    rs.getInt("total_seats"),
                    rs.getInt("available_seats")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error searching trains: " + e.getMessage());
        }
        return trains;
    }

    // Get train by ID
    public Train getTrainById(int trainId) {
        String sql = "SELECT * FROM trains WHERE train_id = ?";
        Train train = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trainId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                train = new Train(
                    rs.getInt("train_id"),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("departure_time"),
                    rs.getInt("total_seats"),
                    rs.getInt("available_seats")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching train: " + e.getMessage());
        }
        return train;
    }

    // Decrease available seats by 1
    public boolean decreaseSeat(int trainId) {
        String sql = "UPDATE trains SET available_seats = available_seats - 1 WHERE train_id = ? AND available_seats > 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trainId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating seat: " + e.getMessage());
        }
        return false;
    }

    // Increase available seats by 1 (on cancellation)
    public boolean increaseSeat(int trainId) {
        String sql = "UPDATE trains SET available_seats = available_seats + 1 WHERE train_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trainId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating seat: " + e.getMessage());
        }
        return false;
    }
}