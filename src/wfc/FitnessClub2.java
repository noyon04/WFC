/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wfc;

import java.sql.*;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
             //insertSampleData();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Datgabase Connection error" + e);
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS customers (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255))");
            statement.execute("CREATE TABLE IF NOT EXISTS lessons (id INT AUTO_INCREMENT PRIMARY KEY, lesson_day VARCHAR(10), start_time TIME, end_time TIME, fitness_type VARCHAR(20), available_slots INT DEFAULT 5)");
            statement.execute("CREATE TABLE IF NOT EXISTS bookings (id INT AUTO_INCREMENT PRIMARY KEY, customer_id INT, lesson_id INT, booking_month VARCHAR(10), FOREIGN KEY (customer_id) REFERENCES customers(id), FOREIGN KEY (lesson_id) REFERENCES lessons(id))");
            statement.execute("CREATE TABLE IF NOT EXISTS reviews (id INT AUTO_INCREMENT PRIMARY KEY, customer_id INT, lesson_id INT, rating INT, booking_month VARCHAR(10), FOREIGN KEY (customer_id) REFERENCES customers(id), FOREIGN KEY (lesson_id) REFERENCES lessons(id))");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String randomMonthNameFromLastThreeMonths() {
        LocalDate currentDate = LocalDate.now();
        LocalDate threeMonthsAgo = currentDate.minus(3, ChronoUnit.MONTHS);

        Month currentMonth = currentDate.getMonth();
        Month threeMonthsAgoMonth = threeMonthsAgo.getMonth();

        int monthCount = currentMonth.getValue() - threeMonthsAgoMonth.getValue();
        if (monthCount < 0) {
            monthCount += 12;
        }

        int randomMonthIndex = new Random().nextInt(monthCount + 1);
        Month randomMonth = threeMonthsAgoMonth.plus(randomMonthIndex);

        return randomMonth.name();
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
            String[][] customersData = {{"John Smith", "john.smith@example.com"}, {"Jane Doe", "jane.doe@example.com"}, {"Mike Brown", "mike.brown@example.com"}, {"Emma Johnson", "emma.johnson@example.com"}, {"Lisa Wilson", "lisa.wilson@example.com"}, {"Peter Davis", "peter.davis@example.com"}, {"Susan Taylor", "susan.taylor@example.com"}, {"Mark Jones", "mark.jones@example.com"}, {"Karen White", "karen.white@example.com"}, {"Brian Harris", "brian.harris@example.com"}};
            for (String[] customerData : customersData) {

                PreparedStatement checkCustomer = connection.prepareStatement("SELECT COUNT(*) FROM customers WHERE name = ? AND email = ?");
                checkCustomer.setString(1, customerData[0]);
                checkCustomer.setString(2, customerData[1]);
                ResultSet resultSet = checkCustomer.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count == 0) {
                    PreparedStatement insertCustomer = connection.prepareStatement("INSERT INTO customers (name, email) VALUES (?, ?)");
                    insertCustomer.setString(1, customerData[0]);
                    insertCustomer.setString(2, customerData[1]);
                    insertCustomer.executeUpdate();
                }
            }

            Random random = new Random();

            for (int i = 0; i < 100; i++) {
                int customerId = random.nextInt(10) + 1; // Random customer ID between 1 and 10
                int lessonId = random.nextInt(20) + 1; // Random lesson ID between 1 and 20
                int rating = random.nextInt(5) + 1; // Random rating between 1 and 5
                String randomMonthName = randomMonthNameFromLastThreeMonths();

                // Check if booking already exists for the customer and lesson
                PreparedStatement checkBookingExists = connection.prepareStatement("SELECT * FROM bookings WHERE customer_id = ? AND lesson_id = ?");
                checkBookingExists.setInt(1, customerId);
                checkBookingExists.setInt(2, lessonId);
                ResultSet bookingExistsResult = checkBookingExists.executeQuery();

                if (!bookingExistsResult.next()) {
                    // Check if there is space available in the lesson
                    PreparedStatement checkAvailableSlots = connection.prepareStatement("SELECT available_slots FROM lessons WHERE id = ?");
                    checkAvailableSlots.setInt(1, lessonId);
                    ResultSet availableSlotsResult = checkAvailableSlots.executeQuery();
                    availableSlotsResult.next();
                    int availableSlots = availableSlotsResult.getInt("available_slots");

                    if (availableSlots > 0) {
                        PreparedStatement insertBooking = connection.prepareStatement("INSERT INTO bookings (customer_id, lesson_id, booking_month) VALUES (?, ?, ?)");
                        insertBooking.setInt(1, customerId);
                        insertBooking.setInt(2, lessonId);
                        insertBooking.setString(3, randomMonthName);
                        insertBooking.executeUpdate();

                        // Update the available slots for the lesson
                        PreparedStatement updateAvailableSlots = connection.prepareStatement("UPDATE lessons SET available_slots = available_slots - 1 WHERE id = ?");
                        updateAvailableSlots.setInt(1, lessonId);
                        updateAvailableSlots.executeUpdate();

                        // Check if review already exists for the customer and lesson
                        PreparedStatement checkReviewExists = connection.prepareStatement("SELECT * FROM reviews WHERE customer_id = ? AND lesson_id = ?");
                        checkReviewExists.setInt(1, customerId);
                        checkReviewExists.setInt(2, lessonId);
                        ResultSet reviewExistsResult = checkReviewExists.executeQuery();

                        if (!reviewExistsResult.next()) {
                            PreparedStatement insertReview = connection.prepareStatement("INSERT INTO reviews (customer_id, lesson_id, rating, booking_month) VALUES (?, ?, ?, ?)");
                            insertReview.setInt(1, customerId);
                            insertReview.setInt(2, lessonId);
                            insertReview.setInt(3, rating);
                            insertReview.setString(4, randomMonthName);
                            insertReview.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCustomer(Customer customer) {
        try {

            PreparedStatement checkCustomer = connection.prepareStatement("SELECT COUNT(*) FROM customers WHERE name = ? AND email = ?");
            checkCustomer.setString(1, customer.getName());
            checkCustomer.setString(2, customer.getEmail());
            ResultSet resultSet = checkCustomer.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customers (name, email) VALUES (?, ?)");
                preparedStatement.setString(1, customer.getName());
                preparedStatement.setString(2, customer.getEmail());
                preparedStatement.executeUpdate();
                System.out.println("Successfull added");
            } else {
                System.out.println("Name already exists: " + customer.getName());
            }
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
            // PreparedStatement findLesson = connection.prepareStatement("SELECT id FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");
            PreparedStatement findLesson = connection.prepareStatement("SELECT id, available_slots FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");

            findLesson.setString(1, day);
            findLesson.setString(2, time);
            findLesson.setString(3, fitnessType);
            ResultSet lessonResult = findLesson.executeQuery();

            if (!lessonResult.next()) {
                System.out.println("Lesson not found.");
                return;
            }

            int lessonId = lessonResult.getInt("id");
            int availableSlots = lessonResult.getInt("available_slots");

            // Check if booking already exists
            PreparedStatement checkExistingBooking = connection.prepareStatement("SELECT * FROM bookings WHERE customer_id = ? AND lesson_id = ?");
            checkExistingBooking.setInt(1, customerId);
            checkExistingBooking.setInt(2, lessonId);
            ResultSet existingBookingResult = checkExistingBooking.executeQuery();

            if (existingBookingResult.next()) {
                System.out.println("You have already booked this lesson.");
                return;
            }

            // Check available slots for the lesson
            if (availableSlots <= 0) {
                System.out.println("Sorry, there are no available slots for this lesson.");
                return;
            }

            // Get the current month
            String currentMonth = LocalDate.now().getMonth().name();

            // Book the lesson
            PreparedStatement bookLesson = connection.prepareStatement("INSERT INTO bookings (customer_id, lesson_id, booking_month) VALUES (?, ?, ?)");
            bookLesson.setInt(1, customerId);
            bookLesson.setInt(2, lessonId);
            bookLesson.setString(3, currentMonth);
            bookLesson.executeUpdate();

            // Update the available slots
            PreparedStatement updateAvailableSlots = connection.prepareStatement("UPDATE lessons SET available_slots = available_slots - 1 WHERE id = ?");
            updateAvailableSlots.setInt(1, lessonId);
            updateAvailableSlots.executeUpdate();

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
            PreparedStatement findLesson = connection.prepareStatement("SELECT id, available_slots FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");
            findLesson.setString(1, day);
            findLesson.setString(2, time);
            findLesson.setString(3, fitnessType);
            ResultSet lessonResult = findLesson.executeQuery();

            if (!lessonResult.next()) {
                System.out.println("Lesson not found.");
                return;
            }

            int lessonId = lessonResult.getInt("id");
            int availableSlots = lessonResult.getInt("available_slots");
            // Cancel the booking
            PreparedStatement cancelBooking = connection.prepareStatement("DELETE FROM bookings WHERE customer_id = ? AND lesson_id = ?");
            cancelBooking.setInt(1, customerId);
            cancelBooking.setInt(2, lessonId);
            cancelBooking.executeUpdate();

            // Update the available slots
            PreparedStatement updateAvailableSlots = connection.prepareStatement("UPDATE lessons SET available_slots = available_slots + 1 WHERE id = ?");
            updateAvailableSlots.setInt(1, lessonId);
            updateAvailableSlots.executeUpdate();

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
            
             // Check if booking already exists
            PreparedStatement checkExistingBooking = connection.prepareStatement("SELECT * FROM bookings WHERE customer_id = ? AND lesson_id = ?");
            checkExistingBooking.setInt(1, customerId);
            checkExistingBooking.setInt(2, lessonId);
            ResultSet existingBookingResult = checkExistingBooking.executeQuery();

            if (!existingBookingResult.next()) {
                System.out.println("Please note that you can only review a lesson if you have booked it.");
                return;
            }

            // Check if review already exists
            PreparedStatement checkExistingReview = connection.prepareStatement("SELECT * FROM reviews WHERE customer_id = ? AND lesson_id = ?");
            checkExistingReview.setInt(1, customerId);
            checkExistingReview.setInt(2, lessonId);
            ResultSet existingReviewResult = checkExistingReview.executeQuery();

            if (existingReviewResult.next()) {
                System.out.println("You have already reviewed this lesson.");
                return;
            }

            // Get the current month
            String currentMonth = LocalDate.now().getMonth().name();

            // Write the review
            PreparedStatement writeReview = connection.prepareStatement("INSERT INTO reviews (customer_id, lesson_id, rating, booking_month) VALUES (?, ?, ?, ?)");
            writeReview.setInt(1, customerId);
            writeReview.setInt(2, lessonId);
            writeReview.setInt(3, rating);
            writeReview.setString(4, currentMonth);
            writeReview.executeUpdate();

            System.out.println("Review and rating recorded successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateReports() {
        try {
            PreparedStatement reportStatement = connection.prepareStatement(
                    "SELECT l.lesson_day, l.start_time, l.fitness_type, COUNT(b.id) as booking_count, AVG(r.rating) as avg_rating "
                    + "FROM lessons l LEFT JOIN bookings b ON l.id = b.lesson_id LEFT JOIN reviews r ON (b.lesson_id = r.lesson_id AND b.customer_id = r.customer_id) "
                    + "GROUP BY l.lesson_day, l.start_time, l.fitness_type "
                    + "ORDER BY l.lesson_day, l.start_time, l.fitness_type"
            );

            ResultSet resultSet = reportStatement.executeQuery();

            System.out.println("Reports:");
            System.out.println("1. Number of bookings and average rating per lesson:");

            while (resultSet.next()) {
                String day = resultSet.getString("lesson_day");
                String startTime = resultSet.getString("start_time");
                String fitnessType = resultSet.getString("fitness_type");
                int bookingCount = resultSet.getInt("booking_count");
                double avgRating = resultSet.getDouble("avg_rating");

                System.out.printf("%s - %s - %s - %d booking(s) - Avg rating: %s%n", day, startTime, fitnessType, bookingCount, avgRating == 0 ? "N/A" : String.format("%.2f", avgRating));
            }

            PreparedStatement monthlyReportStatement = connection.prepareStatement(
                    "SELECT b.booking_month, COUNT(b.id) as booking_count "
                    + "FROM bookings b "
                    + "GROUP BY b.booking_month "
                    + "ORDER BY CASE "
                    + "WHEN b.booking_month = 'JANUARY' THEN 1 "
                    + "WHEN b.booking_month = 'FEBRUARY' THEN 2 "
                    + "WHEN b.booking_month = 'MARCH' THEN 3 "
                    + "WHEN b.booking_month = 'APRIL' THEN 4 "
                    + "WHEN b.booking_month = 'MAY' THEN 5 "
                    + "WHEN b.booking_month = 'JUNE' THEN 6 "
                    + "WHEN b.booking_month = 'JULY' THEN 7 "
                    + "WHEN b.booking_month = 'AUGUST' THEN 8 "
                    + "WHEN b.booking_month = 'SEPTEMBER' THEN 9 "
                    + "WHEN b.booking_month = 'OCTOBER' THEN 10 "
                    + "WHEN b.booking_month = 'NOVEMBER' THEN 11 "
                    + "WHEN b.booking_month = 'DECEMBER' THEN 12 "
                    + "END"
            );

            resultSet = monthlyReportStatement.executeQuery();

            System.out.println("\n2. Bookings count per booking month:");

            while (resultSet.next()) {
                String month = resultSet.getString("booking_month");
                int bookingCount = resultSet.getInt("booking_count");

                System.out.printf("%s - %d booking(s)%n", month, bookingCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public void generateReports() {
//    try {
//        PreparedStatement reportStatement = connection.prepareStatement(
//                "SELECT l.lesson_day, l.fitness_type, COUNT(DISTINCT b.customer_id) as customer_count, AVG(r.rating) as avg_rating "
//                + "FROM lessons l LEFT JOIN bookings b ON l.id = b.lesson_id LEFT JOIN reviews r ON l.id = r.lesson_id "
//                + "GROUP BY l.lesson_day, l.fitness_type "
//                + "ORDER BY l.lesson_day, l.fitness_type"
//        );
////         PreparedStatement reportStatement = connection.prepareStatement(
////                "SELECT l.lesson_day, l.fitness_type, COUNT(DISTINCT b.customer_id) as customer_count, AVG(r.rating) as avg_rating "
////                + "FROM lessons l LEFT JOIN bookings b ON l.id = b.lesson_id LEFT JOIN reviews r ON l.id = r.lesson_id "
////                + "GROUP BY l.lesson_day, l.fitness_type "
////                + "HAVING COUNT(DISTINCT b.customer_id) <= 5 "
////                + "ORDER BY l.lesson_day, l.fitness_type"
////        );
////        PreparedStatement reportStatement = connection.prepareStatement(
////                "SELECT l.lesson_day, l.fitness_type, COUNT(b.customer_id) as customer_count, AVG(r.rating) as avg_rating "
////                + "FROM lessons l LEFT JOIN bookings b ON l.id = b.lesson_id LEFT JOIN reviews r ON l.id = r.lesson_id "
////                + "GROUP BY l.lesson_day, l.fitness_type "
////                + "ORDER BY l.lesson_day, l.fitness_type"
////        );
//
//        ResultSet resultSet = reportStatement.executeQuery();
//
//        System.out.println("Reports:");
//        System.out.println("1. Number of customers per lesson on each day:");
//
//        while (resultSet.next()) {
//            String day = resultSet.getString("lesson_day");
//            String fitnessType = resultSet.getString("fitness_type");
//            int customerCount = resultSet.getInt("customer_count");
//            double avgRating = resultSet.getDouble("avg_rating");
//
//            System.out.printf("%s - %s - %d customer(s) - Avg rating: %s%n", day, fitnessType, customerCount, avgRating == 0 ? "N/A" : String.format("%.1f", avgRating));
//        }
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//}
//    public void generateReports() {
//        try {
//            PreparedStatement reportStatement = connection.prepareStatement(
//                    "SELECT COALESCE(b.booking_month, 'N/A') as booking_month, l.lesson_day, l.fitness_type, COUNT(b.customer_id) as customer_count, AVG(r.rating) as avg_rating "
//                    + "FROM lessons l LEFT JOIN bookings b ON l.id = b.lesson_id LEFT JOIN reviews r ON l.id = r.lesson_id "
//                    + "WHERE b.booking_month IS NOT NULL "
//                    + "GROUP BY b.booking_month, l.lesson_day, l.fitness_type "
//                    + "ORDER BY CASE b.booking_month "
//                    + "WHEN 'JANUARY' THEN 1 "
//                    + "WHEN 'FEBRUARY' THEN 2 "
//                    + "WHEN 'MARCH' THEN 3 "
//                    + "WHEN 'APRIL' THEN 4 "
//                    + "WHEN 'MAY' THEN 5 "
//                    + "WHEN 'JUNE' THEN 6 "
//                    + "WHEN 'JULY' THEN 7 "
//                    + "WHEN 'AUGUST' THEN 8 "
//                    + "WHEN 'SEPTEMBER' THEN 9 "
//                    + "WHEN 'OCTOBER' THEN 10 "
//                    + "WHEN 'NOVEMBER' THEN 11 "
//                    + "WHEN 'DECEMBER' THEN 12 "
//                    + "END, l.lesson_day, l.fitness_type"
//            );
//
//            ResultSet resultSet = reportStatement.executeQuery();
//
//            System.out.println("Reports:");
//
//            String currentMonth = "";
//            int counter = 1;
//            while (resultSet.next()) {
//                String month = resultSet.getString("booking_month");
//                if (!month.equals(currentMonth)) {
//                    currentMonth = month;
//                    System.out.println("\nMonth: " + currentMonth);
//                    System.out.println(counter + ". Number of customers per lesson on each day:");
//                    counter++;
//                }
//
//                String day = resultSet.getString("lesson_day");
//                String fitnessType = resultSet.getString("fitness_type");
//                int customerCount = resultSet.getInt("customer_count");
//                double avgRating = resultSet.getDouble("avg_rating");
//
//                System.out.printf("%s - %s - %d customer(s) - Avg rating: %s%n", day, fitnessType, customerCount, avgRating == 0 ? "N/A" : String.format("%.1f", avgRating));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
