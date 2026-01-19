import java.sql.*;
import java.util.Scanner;

public class ParkingManagementSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking_system";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "root"; // Replace with your MySQL password

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to the database.");

            while (true) {
                System.out.println("\nParking Management System");
                System.out.println("1. Park a vehicle");
                System.out.println("2. Exit a vehicle");
                System.out.println("3. View parking records");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> parkVehicle(connection, scanner);
                    case 2 -> exitVehicle(connection, scanner);
                    case 3 -> viewRecords(connection);
                    case 4 -> {
                        System.out.println("Exiting system.");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void parkVehicle(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter vehicle number: ");
        String vehicleNumber = scanner.nextLine();

        System.out.print("Enter vehicle category (car/bike/bus): ");
        String category = scanner.nextLine();

        String sql = "INSERT INTO parking_records (vehicle_number, entry_time, exit_time, parking_fee) VALUES (?, NOW(), NULL, NULL)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vehicleNumber);
            statement.executeUpdate();
            System.out.println("Vehicle parked successfully.");
        }
    }

    private static void exitVehicle(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter vehicle number: ");
        String vehicleNumber = scanner.nextLine();

        System.out.print("Enter vehicle category (car/bike/bus): ");
        String category = scanner.nextLine();

        String fetchSql = "SELECT id, entry_time FROM parking_records WHERE vehicle_number = ? AND exit_time IS NULL";
        try (PreparedStatement fetchStatement = connection.prepareStatement(fetchSql)) {
            fetchStatement.setString(1, vehicleNumber);
            ResultSet resultSet = fetchStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                Timestamp entryTime = resultSet.getTimestamp("entry_time");
                Timestamp exitTime = new Timestamp(System.currentTimeMillis());

                long duration = (exitTime.getTime() - entryTime.getTime()) / (1000 * 60); // Duration in minutes
                double fee = calculateFee(duration, category);

                String updateSql = "UPDATE parking_records SET exit_time = ?, parking_fee = ? WHERE id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setTimestamp(1, exitTime);
                    updateStatement.setDouble(2, fee);
                    updateStatement.setInt(3, id);
                    updateStatement.executeUpdate();

                    System.out.println("Vehicle exited successfully.");
                    System.out.printf("Parking Fee: Rs%.2f\n", fee);
                }
            } else {
                System.out.println("No record found for the given vehicle number.");
            }
        }
    }

    private static void viewRecords(Connection connection) throws SQLException {
        String sql = "SELECT * FROM parking_records";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.printf("%-5s %-15s %-20s %-20s %-10s\n", "ID", "Vehicle Number", "Entry Time", "Exit Time", "Fee");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String vehicleNumber = resultSet.getString("vehicle_number");
                Timestamp entryTime = resultSet.getTimestamp("entry_time");
                Timestamp exitTime = resultSet.getTimestamp("exit_time");
                double fee = resultSet.getDouble("parking_fee");

                System.out.printf("%-5d %-15s %-20s %-20s %-10.2f\n", id, vehicleNumber, entryTime, exitTime, fee);
            }
        }
    }

    private static double calculateFee(long duration, String category) {
        double ratePerHour;
        ratePerHour = switch (category.toLowerCase()) {
            case "car" -> 50.0;
            case "bike" -> 25.0;
            case "bus" -> 100.0;
            default -> 25.0;
        }; // Increased by 50
        // Increased by 25
        // Increased by 100
        // Default rate
        return (duration / 60.0) * ratePerHour;
    }
}
