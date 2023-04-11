/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package wfc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.sql.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
/**
 *
 * @author Hamiduzzaman Noyon
 */
public class FitnessClubTest {

    ConnectionDB db = new ConnectionDB();

    public FitnessClubTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of randomMonthNameFromLastThreeMonths method, of class FitnessClub.
     */
    public void testRandomMonthNameFromLastThreeMonths() {
        System.out.println("randomMonthNameFromLastThreeMonths");

        LocalDate currentDate = LocalDate.now();
        LocalDate threeMonthsAgo = currentDate.minus(3, ChronoUnit.MONTHS);

        Month currentMonth = currentDate.getMonth();
        Month threeMonthsAgoMonth = threeMonthsAgo.getMonth();

        Set<String> expectedMonths = new HashSet<>();
        for (int i = 0; i <= 3; i++) {
            expectedMonths.add(threeMonthsAgoMonth.plus(i).name());
        }

        String result = FitnessClub.randomMonthNameFromLastThreeMonths();
        assertTrue(expectedMonths.contains(result), "The random month should be within the last three months");
    }

    /**
     * Test of insertSampleData method, of class FitnessClub.
     */
    @Test
    public void testInsertSampleData() {
        System.out.println("insertSampleData");
        FitnessClub instance = new FitnessClub();

        // Clear the tables to ensure consistent testing.
        clearTables(instance);

        // Insert sample data.
        instance.insertSampleData();

        // Check if the data was inserted correctly.
        try {
            // Check if lessons have been inserted.
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM lessons");
            rs.next();
            int lessonCount = rs.getInt(1);
            assertTrue(lessonCount > 0, "Lessons were not inserted");

            // Check if customers have been inserted.
            rs = stmt.executeQuery("SELECT COUNT(*) FROM customers");
            rs.next();
            int customerCount = rs.getInt(1);
            assertTrue(customerCount > 0, "Customers were not inserted");

            // Check if bookings have been inserted.
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings");
            rs.next();
            int bookingCount = rs.getInt(1);
            assertTrue(bookingCount > 0, "Bookings were not inserted");

            // Check if reviews have been inserted.
            rs = stmt.executeQuery("SELECT COUNT(*) FROM reviews");
            rs.next();
            int reviewCount = rs.getInt(1);
            assertTrue(reviewCount > 0, "Reviews were not inserted");
        } catch (SQLException e) {
            fail("Failed to execute SQL queries: " + e.getMessage());
        }
    }

    private void clearTables(FitnessClub instance) {
        try {
            Statement stmt = db.getConnection().createStatement();
            stmt.executeUpdate("DELETE FROM reviews");
            stmt.executeUpdate("DELETE FROM bookings");
            stmt.executeUpdate("DELETE FROM customers");
            stmt.executeUpdate("DELETE FROM lessons");
        } catch (SQLException e) {
            fail("Failed to clear tables: " + e.getMessage());
        }
    }

    /**
     * Test of listBookingsForCustomer method, of class FitnessClub.
     */
    @Test
    public void testListBookingsForCustomer() {
        System.out.println("listBookingsForCustomer");

        FitnessClub instance = new FitnessClub();

        // Insert sample data.
        instance.insertSampleData();

        // Test for an existing customer with bookings.
        String customerEmail = "john.smith@example.com";
        String status = "attended";
        int expectedResult = 0; // Replace this with the expected booking count for the given customerEmail and status.

        // Capture the output stream.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        int result = instance.listBookingsForCustomer(customerEmail, status);

        // Restore the original output stream.
        System.setOut(originalOut);

        // Check the result.
        assertEquals(expectedResult, result, "The returned booking count is incorrect.");

        // Test for a non-existing customer.
        customerEmail = "non.existent@example.com";
        int nonExistentCustomerResult = instance.listBookingsForCustomer(customerEmail, status);
        assertEquals(-1, nonExistentCustomerResult, "The method should return -1 for a non-existent customer.");
    }

    /**
     * Test of listBookingsForWriteReview method, of class FitnessClub.
     */
    @Test
    public void testListBookingsForWriteReview() {
        System.out.println("listBookingsForWriteReview");

        FitnessClub instance = new FitnessClub();

        // Insert sample data.
        instance.insertSampleData();

        // Test for an existing customer with bookings.
        String customerEmail = "john.smith@example.com";
        String status = "attended";
        int expectedResult = 0; // Replace this with the expected booking count for the given customerEmail and status.

        // Capture the output stream.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        int result = instance.listBookingsForWriteReview(customerEmail, status);

        // Restore the original output stream.
        System.setOut(originalOut);

        // Check the result.
        assertEquals(expectedResult, result, "The returned booking count is incorrect.");

        // Test for a non-existing customer.
        customerEmail = "non.existent@example.com";
        int nonExistentCustomerResult = instance.listBookingsForWriteReview(customerEmail, status);
        assertEquals(-1, nonExistentCustomerResult, "The method should return -1 for a non-existent customer.");
    }

    /**
     * Test of addCustomer method, of class FitnessClub.
     */
    @Test
    public void testAddCustomer() {
        System.out.println("addCustomer");

        FitnessClub instance = new FitnessClub();

        // Test for adding a new customer.
        Customer newCustomer = new Customer("New Customer", "new.customer@example.com");

        // Capture the output stream.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        instance.addCustomer(newCustomer);

        // Restore the original output stream.
        System.setOut(originalOut);

        // Check the result.
        String expectedResult = "Successfull added" + System.lineSeparator();
        assertEquals(expectedResult, outputStream.toString(), "Adding a new customer should print the expected output.");

        // Check if the customer is in the database.
        try {
            PreparedStatement checkCustomer = db.getConnection().prepareStatement("SELECT COUNT(*) FROM customers WHERE name = ? AND email = ?");
            checkCustomer.setString(1, newCustomer.getName());
            checkCustomer.setString(2, newCustomer.getEmail());
            ResultSet resultSet = checkCustomer.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            assertEquals(1, count, "The customer should be added to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException occurred while checking the customer in the database.");
        }

        // Test for adding an existing customer.
        Customer existingCustomer = new Customer("John Smith", "john.smith@example.com");

        // Capture the output stream again.
        outputStream.reset();
        System.setOut(new PrintStream(outputStream));

        instance.addCustomer(existingCustomer);

        // Restore the original output stream.
        System.setOut(originalOut);

        // Check the result.
        expectedResult = "Name already exists: " + existingCustomer.getName() + System.lineSeparator();
        assertEquals(expectedResult, outputStream.toString(), "Adding an existing customer should print the expected output.");
    }

    /**
     * Test of displayTimetable method, of class FitnessClub.
     */
    @Test
    public void testDisplayTimetable() {
        System.out.println("displayTimetable");

        FitnessClub instance = new FitnessClub();

        // Define different filters for testing.
        String[] filters = {"Saturday", "Sunday", "Yoga"};

        for (String filter : filters) {
            // Capture the output stream.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));

            instance.displayTimetable(filter);

            // Restore the original output stream.
            System.setOut(originalOut);

            // Check if the timetable displayed matches the filter.
            List<String> expectedOutput = new ArrayList<>();

            try {
                PreparedStatement timetableStatement;
                if (filter.equalsIgnoreCase("Saturday") || filter.equalsIgnoreCase("Sunday")) {
                    timetableStatement = db.getConnection().prepareStatement("SELECT * FROM lessons WHERE lesson_day = ?");
                    timetableStatement.setString(1, filter);
                } else {
                    timetableStatement = db.getConnection().prepareStatement("SELECT * FROM lessons WHERE fitness_type = ?");
                    timetableStatement.setString(1, filter);
                }

                ResultSet timetableResults = timetableStatement.executeQuery();
                while (timetableResults.next()) {
                    expectedOutput.add("Lesson " + timetableResults.getInt("id") + ": " + timetableResults.getString("fitness_type") + " (" + timetableResults.getTime("start_time").toLocalTime() + ") - " + timetableResults.getInt("available_slots") + " spaces left");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                fail("SQLException occurred while checking the timetable with filter: " + filter);
            }

            String[] resultLines = outputStream.toString().split(System.lineSeparator());
            assertEquals(expectedOutput.size(), resultLines.length, "The number of displayed lessons should match the expected output for filter: " + filter);

            for (int i = 0; i < expectedOutput.size(); i++) {
                assertEquals(expectedOutput.get(i), resultLines[i], "Displayed lesson should match the expected output for filter: " + filter);
            }
        }
    }

    /**
     * Test of bookLesson method, of class FitnessClub.
     */
   @Test
public void testBookLesson() {
    System.out.println("bookLesson");

    FitnessClub instance = new FitnessClub();

    String customerEmail = "test@example.com";
    String day = "Saturday";
    String time = "10:00:00";
    String fitnessType = "Yoga";

    // Capture the output stream.
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    instance.bookLesson(customerEmail, day, time, fitnessType);

    // Restore the original output stream.
    System.setOut(originalOut);

    // Check if the booking was successful.
    assertTrue(outputStream.toString().contains("Booking successful!"), "Booking should be successful");

    // Check if the lesson is booked in the database.
    try {
        int customerId = instance.getCustomerIdByEmail(customerEmail);

        PreparedStatement findLesson = db.getConnection().prepareStatement("SELECT id, available_slots FROM lessons WHERE lesson_day = ? AND start_time = ? AND fitness_type = ?");
        findLesson.setString(1, day);
        findLesson.setString(2, time);
        findLesson.setString(3, fitnessType);
        ResultSet lessonResult = findLesson.executeQuery();

        if (!lessonResult.next()) {
            fail("Lesson not found.");
        }

        int lessonId = lessonResult.getInt("id");
        int availableSlots = lessonResult.getInt("available_slots");

        // Check the booking in the database.
        PreparedStatement checkBooking = db.getConnection().prepareStatement("SELECT * FROM bookings WHERE customer_id = ? AND lesson_id = ?");
        checkBooking.setInt(1, customerId);
        checkBooking.setInt(2, lessonId);
        ResultSet bookingResult = checkBooking.executeQuery();

        assertTrue(bookingResult.next(), "Booking should be present in the database");

        // Check if the available slots are updated in the database.
        assertEquals(availableSlots - 1, bookingResult.getInt("available_slots"), "Available slots should be updated");

    } catch (SQLException e) {
        e.printStackTrace();
        fail("SQLException occurred while checking the booking");
    }
}

    /**
     * Test of attendLesson method, of class FitnessClub.
     */
   @Test
public void testAttendLesson() throws SQLException {
    System.out.println("attendLesson");

    // Mock the Connection object and PreparedStatement
    Connection mockConnection = mock(Connection.class);
    PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

    // Set up the mock connection to return the mock PreparedStatement
    when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);

    // Set up the mock PreparedStatement to return a mock ResultSet
    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Initialize the FitnessClub instance with the mocked Connection object
    FitnessClub instance = new FitnessClub(mockConnection);

    // Set up test data
    String customerEmail = "test@example.com";
    int bookingToAttend = 1;

    // Call the attendLesson method
    instance.attendLesson(customerEmail, bookingToAttend);

    // Verify that the PreparedStatement was called with the correct SQL query and parameters
    verify(mockConnection).prepareStatement("UPDATE bookings SET status = ? WHERE id = ?");
    verify(mockPreparedStatement).setString(1, "attended");
    verify(mockPreparedStatement).setInt(2, bookingToAttend);

    // Verify that executeUpdate was called on the PreparedStatement
    verify(mockPreparedStatement).executeUpdate();
}

    /**
     * Test of cancelBooking method, of class FitnessClub.
     */
      @Test
    public void testCancelBooking() throws SQLException {
        System.out.println("cancelBooking");
        String customerEmail = "test@example.com";
        int bookingToCancel = 1;

        // Create mock objects
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        // Define behavior of mock objects
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);

        // Create a FitnessClub instance with the mocked connection
        FitnessClub instance = new FitnessClub(mockConnection);

        // Call the cancelBooking method
        instance.cancelBooking(customerEmail, bookingToCancel);

        // Verify if the PreparedStatement was executed once
        verify(mockPreparedStatement, times(1)).executeUpdate();

        // You can also add assertions for expected behavior, if necessary
    }

    /**
     * Test of changeLesson method, of class FitnessClub.
     */
    
    @Test
    public void testChangeLesson() throws SQLException {
        System.out.println("changeLesson");
        String customerEmail = "test@example.com";
        int oldBookingToChange = 1;
        String newDay = "Monday";
        String newTime = "10:00";
        String newFitnessType = "Yoga";

        // Create mock objects
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement =mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior of mock objects
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Create a FitnessClub instance with the mocked connection
        FitnessClub instance = new FitnessClub(mockConnection);

        // Call the changeLesson method
        instance.changeLesson(customerEmail, oldBookingToChange, newDay, newTime, newFitnessType);

        // Verify if the PreparedStatement was executed the expected number of times
        verify(mockPreparedStatement, times(4)).executeUpdate();

       
    }

    /**
     * Test of writeReview method, of class FitnessClub.
     */
     @Test
    public void testWriteReview() throws SQLException {
        System.out.println("writeReview");
        String customerEmail = "test@example.com";
        int lessonId = 1;
        int rating = 5;
        String review = "Great lesson!";

        // Create mock objects
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior of mock objects
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Create a FitnessClub instance with the mocked connection
        FitnessClub instance = new FitnessClub(mockConnection);

        // Call the writeReview method
        instance.writeReview(customerEmail, lessonId, rating, review);

        // Verify if the PreparedStatement was executed once
        verify(mockPreparedStatement, times(1)).executeUpdate();

    }

    /**
     * Test of generateReports method, of class FitnessClub.
     */
    @Test
    public void testGenerateReports() {
        System.out.println("generateReports");
        int reportType = 1;
        int month = 1;

        // Create a mock connection
        Connection mockConnection = mock(Connection.class);

        // Create a FitnessClub instance with the mocked connection
        FitnessClub instance = new FitnessClub(mockConnection);

        // Spy on the FitnessClub instance to track method calls
        FitnessClub spyInstance = spy(instance);

        // Call the generateReports method
        spyInstance.generateReports(reportType, month);
        String monthName = Month.of(month).name();
        // Verify if the generateMonthlyLessonReport method was called once
        verify(spyInstance, times(1)).generateMonthlyLessonReport(monthName);
    }

    /**
     * Test of generateMonthlyLessonReport method, of class FitnessClub.
     */
   @Test
    public void testGenerateMonthlyLessonReport() throws SQLException {
        System.out.println("generateMonthlyLessonReport");
        String monthName = "JANUARY";

        // Create a mock connection
        Connection mockConnection = mock(Connection.class);

        // Create a FitnessClub instance with the mocked connection
        FitnessClub instance = new FitnessClub(mockConnection);

        // Mock PreparedStatement
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        // Mock ResultSet
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for mock connection
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Define behavior for mock PreparedStatement
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Define behavior for mock ResultSet
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("lesson_day")).thenReturn("Monday");
        when(mockResultSet.getString("start_time")).thenReturn("10:00");
        when(mockResultSet.getString("fitness_type")).thenReturn("Yoga");
        when(mockResultSet.getInt("booking_count")).thenReturn(5);
        when(mockResultSet.getDouble("avg_rating")).thenReturn(4.2);

        // Call the generateMonthlyLessonReport method
        instance.generateMonthlyLessonReport(monthName);

        // Verify the interactions
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(1)).setString(1, monthName);
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(2)).next();
    }
    /**
     * Test of generateMonthlyChampionFitnessTypeReport method, of class
     * FitnessClub.
     */
     @Test
    public void testGenerateMonthlyChampionFitnessTypeReport() throws SQLException {
        System.out.println("generateMonthlyChampionFitnessTypeReport");
        String monthName = "JANUARY";

        // Create a mock connection
        Connection mockConnection = mock(Connection.class);

        // Create a FitnessClub instance with the mocked connection
        FitnessClub instance = new FitnessClub(mockConnection);

        // Mock PreparedStatement
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        // Mock ResultSet
        ResultSet mockResultSet = mock(ResultSet.class);

        // Define behavior for mock connection
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Define behavior for mock PreparedStatement
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Define behavior for mock ResultSet
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("fitness_type")).thenReturn("Yoga");
        when(mockResultSet.getInt("booking_count")).thenReturn(10);
        when(mockResultSet.getDouble("total_income")).thenReturn(500.0);

        // Call the generateMonthlyChampionFitnessTypeReport method
        instance.generateMonthlyChampionFitnessTypeReport(monthName);

        // Verify the interactions
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(1)).setString(1, monthName);
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(2)).next();
    }

}
