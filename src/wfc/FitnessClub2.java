/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wfc;

import java.sql.*;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Hamiduzzaman Noyon
 */
public class FitnessClub2 {

    private Connection connection;

    public FitnessClub2() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:./database/wfcdb;IFEXISTS=TRUE", "admin", "");
            createTables();
           // insertSampleData();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Datgabase Connection error" + e);
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS customers (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255))");
            statement.execute("CREATE TABLE IF NOT EXISTS lessons (id INT AUTO_INCREMENT PRIMARY KEY, lesson_day VARCHAR(255), start_time TIME, end_time TIME, fitness_type VARCHAR(255), available_slots INT)");
            statement.execute("CREATE TABLE IF NOT EXISTS bookings (id INT AUTO_INCREMENT PRIMARY KEY, customer_id INT, lesson_id INT, FOREIGN KEY (customer_id) REFERENCES customers(id), FOREIGN KEY (lesson_id) REFERENCES lessons(id))");
            statement.execute("CREATE TABLE IF NOT EXISTS reviews (id INT AUTO_INCREMENT PRIMARY KEY, customer_id INT, lesson_id INT, rating INT, FOREIGN KEY (customer_id) REFERENCES customers(id), FOREIGN KEY (lesson_id) REFERENCES lessons(id))");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertSampleData() {
        try {
            // Insert lessons
            String[] days = {"Saturday", "Sunday"};
            String[] fitnessTypes = {"SPIN", "YOGA", "BODYSCULPT", "ZUMBA", "CYCLE RACE"};
            LocalTime[] lessonTimes = {LocalTime.of(10, 0), LocalTime.of(12, 0)};

            for (String day : days) {
            for (String fitnessType : fitnessTypes) {
                for (LocalTime lessonTime : lessonTimes) {
                    PreparedStatement insertLesson = connection.prepareStatement("INSERT INTO lessons (lesson_day, start_time, end_time, fitness_type, available_slots) VALUES (?, ?, ?, ?, ?)");
                    insertLesson.setString(1, day);
                    insertLesson.setTime(2, Time.valueOf(lessonTime));
                    insertLesson.setTime(3, Time.valueOf(lessonTime.plusHours(1)));
                    insertLesson.setString(4, fitnessType);
                    insertLesson.setInt(5, 5); // Set initial available slots to 5
                    insertLesson.executeUpdate();
                }
            }
        }

            // Insert customers
            String[][] customersData = {{"John Doe", "john.doe@example.com"}, {"Jane Doe", "jane.doe@example.com"}};
            for (String[] customerData : customersData) {
                PreparedStatement insertCustomer = connection.prepareStatement("INSERT INTO customers (name, email) VALUES (?, ?)");
                insertCustomer.setString(1, customerData[0]);
                insertCustomer.setString(2, customerData[1]);
                insertCustomer.executeUpdate();
            }

            // Insert bookings and reviews for the past 4 weeks
            LocalDate currentDate = LocalDate.now();
            LocalDate fourWeeksAgo = currentDate.minus(4, ChronoUnit.WEEKS);

            while (fourWeeksAgo.isBefore(currentDate)) {
                if (fourWeeksAgo.getDayOfWeek().toString().equalsIgnoreCase("Saturday") || fourWeeksAgo.getDayOfWeek().toString().equalsIgnoreCase("Sunday")) {
                    PreparedStatement insertBooking = connection.prepareStatement("INSERT INTO bookings (customer_id, lesson_id) VALUES (?, ?)");
                    insertBooking.setInt(1, 1); // Customer ID
                    insertBooking.setInt(2, 1); // Lesson ID
                    insertBooking.executeUpdate();

                    PreparedStatement insertReview = connection.prepareStatement("INSERT INTO reviews (customer_id, lesson_id, rating) VALUES (?, ?, ?)");
                    insertReview.setInt(1, 1); // Customer ID
                    insertReview.setInt(2, 1); // Lesson ID
                    insertReview.setInt(3, 5); // Rating
                    insertReview.executeUpdate();
                }
                fourWeeksAgo = fourWeeksAgo.plusDays(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCustomer(Customer customer) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customers (name, email) VALUES (?, ?)");
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayTimetable(String filter) {
    try {
        PreparedStatement timetableStatement;
        if (filter.equalsIgnoreCase("Saturday") || filter.equalsIgnoreCase("Sunday")) {
            timetableStatement = connection.prepareStatement("SELECT * FROM lessons WHERE lesson_day = ?");
            timetableStatement.setString(1, filter);
        } else {
            timetableStatement = connection.prepareStatement("SELECT * FROM lessons WHERE fitness_type = ?");
            timetableStatement.setString(1, filter);
        }

        ResultSet timetableResults = timetableStatement.executeQuery();
        while (timetableResults.next()) {
            System.out.println("Lesson " + timetableResults.getInt("id") + ": " + timetableResults.getString("fitness_type") + " (" + timetableResults.getTime("start_time").toLocalTime() + ") - " + timetableResults.getInt("available_slots") + " spaces left");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public void bookLesson(String customerEmail, String day, String time, String fitnessType) {
        try {
            // Find customer by email
            PreparedStatement findCustomer = connection.prepareStatement("SELECT id FROM customers WHERE email = ?");
            findCustomer.setString(1, customerEmail);
            ResultSet customerResult = findCustomer.executeQuery();

            if (!customerResult.next()) {
                System.out.println("Customer not found.");
                return;
            }

            int customerId = customerResult.getInt("id");

            // Find lesson by day, time, and fitnessType
            PreparedStatement findLesson = connection.prepareStatement("SELECT id FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");
            findLesson.setString(1, day);
            findLesson.setString(2, time);
            findLesson.setString(3, fitnessType);
            ResultSet lessonResult = findLesson.executeQuery();

            if (!lessonResult.next()) {
                System.out.println("Lesson not found.");
                return;
            }

            int lessonId = lessonResult.getInt("id");

            // Book the lesson
            PreparedStatement bookLesson = connection.prepareStatement("INSERT INTO bookings (customer_id, lesson_id) VALUES (?, ?)");
            bookLesson.setInt(1, customerId);
            bookLesson.setInt(2, lessonId);
            bookLesson.executeUpdate();

            System.out.println("Booking successful!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelBooking(String customerEmail, String day, String time, String fitnessType) {
        try {
            // Find customer by email
            PreparedStatement findCustomer = connection.prepareStatement("SELECT id FROM customers WHERE email = ?");
            findCustomer.setString(1, customerEmail);
            ResultSet customerResult = findCustomer.executeQuery();

            if (!customerResult.next()) {
                System.out.println("Customer not found.");
                return;
            }

            int customerId = customerResult.getInt("id");

            // Find lesson by day, time, and fitnessType
            PreparedStatement findLesson = connection.prepareStatement("SELECT id FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");
            findLesson.setString(1, day);
            findLesson.setString(2, time);
            findLesson.setString(3, fitnessType);
            ResultSet lessonResult = findLesson.executeQuery();

            if (!lessonResult.next()) {
                System.out.println("Lesson not found.");
                return;
            }

            int lessonId = lessonResult.getInt("id");

            // Cancel the booking
            PreparedStatement cancelBooking = connection.prepareStatement("DELETE FROM bookings WHERE customer_id = ? AND lesson_id = ?");
            cancelBooking.setInt(1, customerId);
            cancelBooking.setInt(2, lessonId);
            cancelBooking.executeUpdate();

            System.out.println("Booking canceled successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeReview(String customerEmail, String day, String time, String fitnessType, int rating) {
        try {
            // Find customer by email
            PreparedStatement findCustomer = connection.prepareStatement("SELECT id FROM customers WHERE email = ?");
            findCustomer.setString(1, customerEmail);
            ResultSet customerResult = findCustomer.executeQuery();

            if (!customerResult.next()) {
                System.out.println("Customer not found.");
                return;
            }

            int customerId = customerResult.getInt("id");

            // Find lesson by day, time, and fitnessType
            PreparedStatement findLesson = connection.prepareStatement("SELECT id FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");
            findLesson.setString(1, day);
            findLesson.setString(2, time);
            findLesson.setString(3, fitnessType);
            ResultSet lessonResult = findLesson.executeQuery();

            if (!lessonResult.next()) {
                System.out.println("Lesson not found.");
                return;
            }

            int lessonId = lessonResult.getInt("id");

            // Write the review
            PreparedStatement writeReview = connection.prepareStatement("INSERT INTO reviews (customer_id, lesson_id, rating) VALUES (?, ?, ?)");
            writeReview.setInt(1, customerId);
            writeReview.setInt(2, lessonId);
            writeReview.setInt(3, rating);
            writeReview.executeUpdate();

            System.out.println("Review and rating recorded successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateReports() {
        try {
            PreparedStatement reportStatement = connection.prepareStatement("SELECT l.lesson_day, l.fitness_type, COUNT(b.customer_id) as customer_count, AVG(r.rating) as avg_rating FROM lessons l LEFT JOIN bookings b ON l.id = b.lesson_id LEFT JOIN reviews r ON l.id = r.lesson_id GROUP BY l.lesson_day, l.fitness_type");
            ResultSet resultSet = reportStatement.executeQuery();

            System.out.println("Reports:");
            System.out.println("1. Number of customers per lesson on each day:");

            while (resultSet.next()) {
                String day = resultSet.getString("lesson_day");
                String fitnessType = resultSet.getString("fitness_type");
                int customerCount = resultSet.getInt("customer_count");
                double avgRating = resultSet.getDouble("avg_rating");

                System.out.printf("%s - %s - %d customer(s) - Avg rating: %s%n", day, fitnessType, customerCount, avgRating == 0 ? "N/A" : String.format("%.1f", avgRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
