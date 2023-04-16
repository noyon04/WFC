/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wfc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Hamiduzzaman Noyon
 */
public class FitnessClub {

    ConnectionDB db = new ConnectionDB();
    private Connection connection;
    private Scanner scanner = new Scanner(System.in);

    public FitnessClub() {

        connection = db.getConnection();
        createTables();
        //insertSampleData();

    }

    public FitnessClub(Connection mockConnection) {
        connection = mockConnection;
    }

    private void createTables() {
        try {
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS customers (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255))");

            statement.execute("CREATE TABLE IF NOT EXISTS lessons (id INT AUTO_INCREMENT PRIMARY KEY, lesson_day VARCHAR(10), start_time TIME, end_time TIME, fitness_type VARCHAR(20), available_slots INT DEFAULT 5, price DECIMAL(10, 2))");

            statement.execute("CREATE TABLE IF NOT EXISTS bookings (id INT AUTO_INCREMENT PRIMARY KEY, customer_id INT, lesson_id INT, booking_month VARCHAR(10), status ENUM('booked', 'attended', 'changed', 'cancelled') DEFAULT 'booked', FOREIGN KEY (customer_id) REFERENCES customers(id), FOREIGN KEY (lesson_id) REFERENCES lessons(id))");

            statement.execute("CREATE TABLE IF NOT EXISTS reviews (id INT AUTO_INCREMENT PRIMARY KEY, customer_id INT, lesson_id INT, rating INT, review_text TEXT, booking_month VARCHAR(10), FOREIGN KEY (customer_id) REFERENCES customers(id), FOREIGN KEY (lesson_id) REFERENCES lessons(id))");

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

    public void loadDummyDataIfFirstRun() {
        File flagFile = new File("first_run_flag.txt");

        if (!flagFile.exists()) {
            try {
                boolean areTablesEmpty = areBookingAndCustomerTablesEmpty();

               
                if (areTablesEmpty) {
                    insertSampleData();
                }

                
                flagFile.createNewFile();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean areBookingAndCustomerTablesEmpty() {
        boolean isEmpty = true;
        try {
            PreparedStatement bookingCountQuery = connection.prepareStatement("SELECT COUNT(*) as row_count FROM bookings");
            ResultSet bookingResultSet = bookingCountQuery.executeQuery();
            
            PreparedStatement customerCountQuery = connection.prepareStatement("SELECT COUNT(*) as row_count FROM customers");
            ResultSet customerResultSet = customerCountQuery.executeQuery();
            
            if (bookingResultSet.next() && customerResultSet.next()) {
                int bookingRowCount = bookingResultSet.getInt("row_count");
                int customerRowCount = customerResultSet.getInt("row_count");
                if (bookingRowCount > 0 || customerRowCount > 0) {
                    isEmpty = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isEmpty;
    }

    public void insertSampleData() {
        try {
            // Inserting lessons
            String[] days = {"Saturday", "Sunday"};
            String[] fitnessTypes = {"SPIN", "YOGA", "BODYSCULPT", "ZUMBA", "CYCLE RACE"};
            LocalTime[] lessonTimes = {LocalTime.of(10, 0), LocalTime.of(12, 0)};
            BigDecimal[] fitnessTypePrices = {BigDecimal.valueOf(25.00), BigDecimal.valueOf(20.00), BigDecimal.valueOf(30.00), BigDecimal.valueOf(15.00), BigDecimal.valueOf(35.00)};
            String[] sampleReviews = {"Great class!", "Loved it!", "Instructor was awesome.", "Enjoyed the session.", "Amazing workout!"};
            String[] bookingStatuses = {"booked", "attended", "changed", "cancelled"};

            for (int i = 0; i < days.length; i++) {
                for (int j = 0; j < fitnessTypes.length; j++) {
                    for (LocalTime lessonTime : lessonTimes) {
                        PreparedStatement insertLesson = connection.prepareStatement("INSERT INTO lessons (lesson_day, start_time, end_time, fitness_type, available_slots, price) VALUES (?, ?, ?, ?, ?, ?)");
                        insertLesson.setString(1, days[i]);
                        insertLesson.setTime(2, Time.valueOf(lessonTime));
                        insertLesson.setTime(3, Time.valueOf(lessonTime.plusHours(1)));
                        insertLesson.setString(4, fitnessTypes[j]);
                        insertLesson.setInt(5, 5); // Set initial available slots to 5
                        insertLesson.setBigDecimal(6, fitnessTypePrices[j]);
                        insertLesson.executeUpdate();
                    }
                }
            }

            // Inserting customers
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

                int customerId = random.nextInt(10) + 1;
                int lessonId = random.nextInt(20) + 1;
                int rating = random.nextInt(5) + 1;
                String randomMonthName = randomMonthNameFromLastThreeMonths();
                String randomStatus = bookingStatuses[random.nextInt(bookingStatuses.length)];

                //booking already exists Checking
                PreparedStatement checkBookingExists = connection.prepareStatement("SELECT * FROM bookings WHERE customer_id = ? AND lesson_id = ?");
                checkBookingExists.setInt(1, customerId);
                checkBookingExists.setInt(2, lessonId);
                ResultSet bookingExistsResult = checkBookingExists.executeQuery();

                if (!bookingExistsResult.next()) {

                    PreparedStatement checkAvailableSlots = connection.prepareStatement("SELECT available_slots FROM lessons WHERE id = ?");
                    checkAvailableSlots.setInt(1, lessonId);
                    ResultSet availableSlotsResult = checkAvailableSlots.executeQuery();
                    availableSlotsResult.next();
                    int availableSlots = availableSlotsResult.getInt("available_slots");

                    if (availableSlots > 0) {
                        PreparedStatement insertBooking = connection.prepareStatement("INSERT INTO bookings (customer_id, lesson_id, booking_month, status) VALUES (?, ?, ?, ?)");
                        insertBooking.setInt(1, customerId);
                        insertBooking.setInt(2, lessonId);
                        insertBooking.setString(3, randomMonthName);
                        insertBooking.setString(4, randomStatus);
                        insertBooking.executeUpdate();

                        PreparedStatement updateAvailableSlots = connection.prepareStatement("UPDATE lessons SET available_slots = available_slots - 1 WHERE id = ?");
                        updateAvailableSlots.setInt(1, lessonId);
                        updateAvailableSlots.executeUpdate();

                        PreparedStatement checkReviewExists = connection.prepareStatement("SELECT * FROM reviews WHERE customer_id = ? AND lesson_id = ?");
                        checkReviewExists.setInt(1, customerId);
                        checkReviewExists.setInt(2, lessonId);
                        ResultSet reviewExistsResult = checkReviewExists.executeQuery();
                        if (!reviewExistsResult.next()) {
                            String randomReview = sampleReviews[random.nextInt(sampleReviews.length)];

                            PreparedStatement insertReview = connection.prepareStatement("INSERT INTO reviews (customer_id, lesson_id, rating, review_text, booking_month) VALUES (?, ?, ?, ?, ?)");
                            insertReview.setInt(1, customerId);
                            insertReview.setInt(2, lessonId);
                            insertReview.setInt(3, rating);
                            insertReview.setString(4, randomReview);
                            insertReview.setString(5, randomMonthName);
                            insertReview.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int listBookingsForCustomer(String customerEmail, String status) {
        try {
            int customerId = findCustomerByEmail(customerEmail);

            if (customerId == -1) {
                System.out.println("Customer not found.");
                return -1;
            }

            PreparedStatement listBookings = connection.prepareStatement(
                    "SELECT b.id, l.lesson_day, l.start_time, l.fitness_type "
                    + "FROM bookings b JOIN lessons l ON b.lesson_id = l.id "
                    + "WHERE b.customer_id = ? AND b.status = ?"
            );
            listBookings.setInt(1, customerId);
            listBookings.setString(2, status);
            ResultSet bookingResults = listBookings.executeQuery();

            List<Integer> bookingIds = new ArrayList<>();
            int counter = 1;
            while (bookingResults.next()) {
                int bookingId = bookingResults.getInt("id");
                String day = bookingResults.getString("lesson_day");
                String time = bookingResults.getString("start_time");
                String fitnessType = bookingResults.getString("fitness_type");
                bookingIds.add(bookingId);

                System.out.println(counter + ": " + day + ", " + time + ", " + fitnessType);
                counter++;
            }

            if (bookingIds.isEmpty()) {
                System.out.println("No bookings found with the specified status: " + status);
                return -1;
            }

            System.out.print("Enter the number: ");
            int selectedIndex = scanner.nextInt() - 1;
            scanner.nextLine();  // Consume the newline character
            if (selectedIndex < 0 || selectedIndex > (bookingIds.size() - 1)) {
                System.out.println("Invalid choice.");
                return -1;
            }
            return bookingIds.get(selectedIndex);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int listBookingsForWriteReview(String customerEmail, String status) {
        try {
            int customerId = findCustomerByEmail(customerEmail);

            if (customerId == -1) {
                System.out.println("Customer not found.");
                return -1;
            }

            PreparedStatement listBookings = connection.prepareStatement(
                    "SELECT b.id, l.lesson_day, l.start_time, l.fitness_type "
                    + "FROM bookings b JOIN lessons l ON b.lesson_id = l.id "
                    + "WHERE b.customer_id = ? AND b.status = ? AND NOT EXISTS (SELECT * FROM reviews r WHERE r.customer_id = b.customer_id AND r.lesson_id = b.lesson_id)"
            );
            listBookings.setInt(1, customerId);
            listBookings.setString(2, status);
            ResultSet bookingResults = listBookings.executeQuery();

            List<Integer> bookingIds = new ArrayList<>();
            int counter = 1;
            while (bookingResults.next()) {
                int bookingId = bookingResults.getInt("id");
                String day = bookingResults.getString("lesson_day");
                String time = bookingResults.getString("start_time");
                String fitnessType = bookingResults.getString("fitness_type");
                bookingIds.add(bookingId);

                System.out.println(counter + ": " + day + ", " + time + ", " + fitnessType);
                counter++;
            }

            if (bookingIds.isEmpty()) {
                System.out.println("No bookings found or you have already written reviews for all attended lessons.");
                return -1;
            }

            System.out.print("Enter the number: ");
            int selectedIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            if (selectedIndex < 0 || selectedIndex > (bookingIds.size() - 1)) {
                System.out.println("Invalid choice.");
                return -1;
            }
            return bookingIds.get(selectedIndex);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private int findCustomerByEmail(String customerEmail) throws SQLException {
        PreparedStatement findCustomer = connection.prepareStatement("SELECT id FROM customers WHERE email = ?");
        findCustomer.setString(1, customerEmail);
        ResultSet customerResult = findCustomer.executeQuery();
        return customerResult.next() ? customerResult.getInt("id") : -1;
    }

    private int findLesson(String day, String time, String fitnessType) throws SQLException {
        PreparedStatement findLesson = connection.prepareStatement("SELECT id FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");
        findLesson.setString(1, day);
        findLesson.setString(2, time);
        findLesson.setString(3, fitnessType);
        ResultSet lessonResult = findLesson.executeQuery();
        return lessonResult.next() ? lessonResult.getInt("id") : -1;
    }

    private ResultSet checkBooking(int customerId, int lessonId) throws SQLException {
        PreparedStatement checkExistingBooking = connection.prepareStatement("SELECT * FROM bookings WHERE customer_id = ? AND lesson_id = ?");
        checkExistingBooking.setInt(1, customerId);
        checkExistingBooking.setInt(2, lessonId);
        return checkExistingBooking.executeQuery();
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

    public int getCustomerIdByEmail(String customerEmail) throws SQLException {
        return findCustomerByEmail(customerEmail);
    }

    public void bookLesson(String customerEmail, String day, String time, String fitnessType) {
        try {

            int customerId = findCustomerByEmail(customerEmail);

            if (customerId == -1) {
                System.out.println("Customer not found.");
                return;
            }

            // PreparedStatement findLesson = connection.prepareStatement("SELECT id FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");
            PreparedStatement findLessonQ = connection.prepareStatement("SELECT id, available_slots FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");

            findLessonQ.setString(1, day);
            findLessonQ.setString(2, time);
            findLessonQ.setString(3, fitnessType);
            ResultSet lessonResult = findLessonQ.executeQuery();

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
                String status = existingBookingResult.getString("status");
                if (!"cancelled".equalsIgnoreCase(status) || !"changed".equalsIgnoreCase(status)) {
                    System.out.println("You have already booked this lesson.");
                    return;
                }
            }

            if (availableSlots <= 0) {
                System.out.println("Sorry, there are no available slots for this lesson.");
                return;
            }

            String currentMonth = LocalDate.now().getMonth().name();

            PreparedStatement bookLesson = connection.prepareStatement("INSERT INTO bookings (customer_id, lesson_id, booking_month) VALUES (?, ?, ?)");
            bookLesson.setInt(1, customerId);
            bookLesson.setInt(2, lessonId);
            bookLesson.setString(3, currentMonth);
            bookLesson.executeUpdate();

            PreparedStatement updateAvailableSlots = connection.prepareStatement("UPDATE lessons SET available_slots = available_slots - 1 WHERE id = ?");
            updateAvailableSlots.setInt(1, lessonId);
            updateAvailableSlots.executeUpdate();

            System.out.println("Booking successful!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBookingStatus(int bookingId, String status) {
        try {
            PreparedStatement updateBookingStatus = connection.prepareStatement("UPDATE bookings SET status = ? WHERE id = ?");
            updateBookingStatus.setString(1, status);
            updateBookingStatus.setInt(2, bookingId);
            updateBookingStatus.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void attendLesson(String customerEmail, int bookingToAttend) {

        updateBookingStatus(bookingToAttend, "attended");
        System.out.println("Lesson attended successfully!");

    }

    public void cancelBooking(String customerEmail, int bookingToCancel) {

        updateBookingStatus(bookingToCancel, "cancelled");
        try {

            PreparedStatement updateAvailableSlots = connection.prepareStatement("UPDATE lessons SET available_slots = available_slots + 1 WHERE id = ?");
            updateAvailableSlots.setInt(1, bookingToCancel);
            updateAvailableSlots.executeUpdate();

            System.out.println("Booking cancelled successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeLesson(String customerEmail, int oldBookingToChange, String newDay, String newTime, String newFitnessType) {
        try {
            int customerId = findCustomerByEmail(customerEmail);

            if (customerId == -1) {
                System.out.println("Customer not found.");
                return;
            }

            int newLessonId = findLesson(newDay, newTime, newFitnessType);

            if (newLessonId == -1) {
                System.out.println("New lesson not found.");
                return;
            }

            ResultSet existingBookingResult = checkBooking(customerId, newLessonId);

            if (existingBookingResult.next()) {
                String newStatus = existingBookingResult.getString("status");
                if (!"cancelled".equalsIgnoreCase(newStatus)) {
                    System.out.println("You have already booked the new lesson.");
                    return;
                }
            }

            updateBookingStatus(oldBookingToChange, "changed");

            PreparedStatement updateAvailableSlots = connection.prepareStatement("UPDATE lessons SET available_slots = available_slots + 1 WHERE id = ?");
            updateAvailableSlots.setInt(1, oldBookingToChange);
            updateAvailableSlots.executeUpdate();

            PreparedStatement updateNewBookingStatus = connection.prepareStatement("INSERT INTO bookings (customer_id, lesson_id, booking_month, status) VALUES (?, ?, ?, 'booked')");
            updateNewBookingStatus.setInt(1, customerId);
            updateNewBookingStatus.setInt(2, newLessonId);
            updateNewBookingStatus.setString(3, LocalDate.now().getMonth().name());
            updateNewBookingStatus.executeUpdate();

            System.out.println("Lesson change successful!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeReview(String customerEmail, int lessonId, int rating, String review) {
        try {
            int customerId = findCustomerByEmail(customerEmail);

            if (customerId == -1) {
                System.out.println("Customer not found.");
                return;
            }

            PreparedStatement checkExistingReview = connection.prepareStatement("SELECT * FROM reviews WHERE customer_id = ? AND lesson_id = ?");
            checkExistingReview.setInt(1, customerId);
            checkExistingReview.setInt(2, lessonId);
            ResultSet existingReviewResult = checkExistingReview.executeQuery();

            if (existingReviewResult.next()) {
                System.out.println("You have already reviewed this lesson.");
                return;
            }

            String currentMonth = LocalDate.now().getMonth().name();

            PreparedStatement writeReview = connection.prepareStatement("INSERT INTO reviews (customer_id, lesson_id, rating, review, booking_month) VALUES (?, ?, ?, ?, ?)");
            writeReview.setInt(1, customerId);
            writeReview.setInt(2, lessonId);
            writeReview.setInt(3, rating);
            writeReview.setString(4, review);
            writeReview.setString(5, currentMonth);
            writeReview.executeUpdate();

            System.out.println("Review and rating recorded successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateReports(int reportType, int month) {
        String monthName = Month.of(month).name();

        switch (reportType) {
            case 1:
                generateMonthlyLessonReport(monthName);
                break;
            case 2:
                generateMonthlyChampionFitnessTypeReport(monthName);
                break;
            default:
                System.out.println("Invalid report type.");
                break;
        }
    }

    public void generateMonthlyLessonReport(String monthName) {
        try {
            PreparedStatement reportStatement = connection.prepareStatement(
                    "SELECT l.lesson_day, l.start_time, l.fitness_type, COUNT(b.id) as booking_count, AVG(r.rating) as avg_rating "
                    + "FROM lessons l "
                    + "LEFT JOIN bookings b ON l.id = b.lesson_id AND b.status = 'attended' AND b.booking_month = ? "
                    + "LEFT JOIN reviews r ON b.lesson_id = r.lesson_id AND b.customer_id = r.customer_id "
                    + "GROUP BY l.lesson_day, l.start_time, l.fitness_type "
                    + "ORDER BY l.lesson_day, l.start_time, l.fitness_type"
            );
            reportStatement.setString(1, monthName);
            ResultSet resultSet = reportStatement.executeQuery();

            System.out.println("Monthly lesson report:");
            System.out.println("Number of customers per lesson and average rating on " + monthName.toUpperCase() + ": ");

            while (resultSet.next()) {
                String day = resultSet.getString("lesson_day");
                String startTime = resultSet.getString("start_time");
                String fitnessType = resultSet.getString("fitness_type");
                int bookingCount = resultSet.getInt("booking_count");
                double avgRating = resultSet.getDouble("avg_rating");

                System.out.printf("%s - %s - %s - %d customer(s) - Avg rating: %s%n", day, startTime, fitnessType, bookingCount, avgRating == 0 ? "N/A" : String.format("%.2f", avgRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateMonthlyChampionFitnessTypeReport(String monthName) {
        try {
            PreparedStatement reportStatement = connection.prepareStatement(
                    "SELECT l.fitness_type, COUNT(b.id) as booking_count, SUM(l.price) as total_income "
                    + "FROM lessons l "
                    + "JOIN bookings b ON l.id = b.lesson_id AND b.status = 'attended' AND b.booking_month = ? "
                    + "GROUP BY l.fitness_type "
                    + "ORDER BY total_income DESC"
            );
            reportStatement.setString(1, monthName);
            ResultSet resultSet = reportStatement.executeQuery();

            System.out.println("Monthly champion fitness type report:");
            System.out.println("Number of customers per lesson and average rating on " + monthName.toUpperCase() + ": ");

            while (resultSet.next()) {
                String fitnessType = resultSet.getString("fitness_type");
                int bookingCount = resultSet.getInt("booking_count");
                double totalIncome = resultSet.getDouble("total_income");
                System.out.printf("%s - %d booking(s) - Total income: $%.2f%n", fitnessType, bookingCount, totalIncome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
