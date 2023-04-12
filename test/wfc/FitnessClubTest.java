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

        
        clearTables(instance);

       
        instance.insertSampleData();

       
        try {
          
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM lessons");
            rs.next();
            int lessonCount = rs.getInt(1);
            assertTrue(lessonCount > 0, "Lessons were not inserted");

            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM customers");
            rs.next();
            int customerCount = rs.getInt(1);
            assertTrue(customerCount > 0, "Customers were not inserted");

           
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings");
            rs.next();
            int bookingCount = rs.getInt(1);
            assertTrue(bookingCount > 0, "Bookings were not inserted");

            
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

      
        instance.insertSampleData();

        
        String customerEmail = "john.smith@example.com";
        String status = "attended";
        int expectedResult = 0; 

        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        int result = instance.listBookingsForCustomer(customerEmail, status);

       
        System.setOut(originalOut);

        
        assertEquals(expectedResult, result, "The returned booking count is incorrect.");

       
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

        
        instance.insertSampleData();

       
        String customerEmail = "john.smith@example.com";
        String status = "attended";
        int expectedResult = 0; 

       
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        int result = instance.listBookingsForWriteReview(customerEmail, status);

        
        System.setOut(originalOut);

        
        assertEquals(expectedResult, result, "The returned booking count is incorrect.");

        
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

        
        Customer newCustomer = new Customer("New Customer", "new.customer@example.com");

        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        instance.addCustomer(newCustomer);

        
        System.setOut(originalOut);

        
        String expectedResult = "Successfull added" + System.lineSeparator();
        assertEquals(expectedResult, outputStream.toString(), "Adding a new customer should print the expected output.");

      
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

      
        Customer existingCustomer = new Customer("John Smith", "john.smith@example.com");

        
        outputStream.reset();
        System.setOut(new PrintStream(outputStream));

        instance.addCustomer(existingCustomer);

        
        System.setOut(originalOut);

        
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

       
        String[] filters = {"Saturday", "Sunday", "Yoga"};

        for (String filter : filters) {
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));

            instance.displayTimetable(filter);

            
            System.setOut(originalOut);

            
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

    
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    instance.bookLesson(customerEmail, day, time, fitnessType);

    
    System.setOut(originalOut);

    
    assertTrue(outputStream.toString().contains("Booking successful!"), "Booking should be successful");

    
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

        
        PreparedStatement checkBooking = db.getConnection().prepareStatement("SELECT * FROM bookings WHERE customer_id = ? AND lesson_id = ?");
        checkBooking.setInt(1, customerId);
        checkBooking.setInt(2, lessonId);
        ResultSet bookingResult = checkBooking.executeQuery();

        assertTrue(bookingResult.next(), "Booking should be present in the database");

        
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

    
    Connection mockConnection = mock(Connection.class);
    PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

   
    when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);

    
    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    
    FitnessClub instance = new FitnessClub(mockConnection);

    
    String customerEmail = "test@example.com";
    int bookingToAttend = 1;

    
    instance.attendLesson(customerEmail, bookingToAttend);

   
    verify(mockConnection).prepareStatement("UPDATE bookings SET status = ? WHERE id = ?");
    verify(mockPreparedStatement).setString(1, "attended");
    verify(mockPreparedStatement).setInt(2, bookingToAttend);

    
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

        
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);

        
        FitnessClub instance = new FitnessClub(mockConnection);

        
        instance.cancelBooking(customerEmail, bookingToCancel);

        
        verify(mockPreparedStatement, times(1)).executeUpdate();

        
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

        
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement =mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        
        FitnessClub instance = new FitnessClub(mockConnection);

        
        instance.changeLesson(customerEmail, oldBookingToChange, newDay, newTime, newFitnessType);

       
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

        
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        
        FitnessClub instance = new FitnessClub(mockConnection);

        
        instance.writeReview(customerEmail, lessonId, rating, review);

        
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

        
        Connection mockConnection = mock(Connection.class);

        
        FitnessClub instance = new FitnessClub(mockConnection);

        
        FitnessClub spyInstance = spy(instance);

        
        spyInstance.generateReports(reportType, month);
        String monthName = Month.of(month).name();
        
        verify(spyInstance, times(1)).generateMonthlyLessonReport(monthName);
    }

    /**
     * Test of generateMonthlyLessonReport method, of class FitnessClub.
     */
   @Test
    public void testGenerateMonthlyLessonReport() throws SQLException {
        System.out.println("generateMonthlyLessonReport");
        String monthName = "JANUARY";

        
        Connection mockConnection = mock(Connection.class);

       
        FitnessClub instance = new FitnessClub(mockConnection);

        
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        
        ResultSet mockResultSet = mock(ResultSet.class);

        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("lesson_day")).thenReturn("Monday");
        when(mockResultSet.getString("start_time")).thenReturn("10:00");
        when(mockResultSet.getString("fitness_type")).thenReturn("Yoga");
        when(mockResultSet.getInt("booking_count")).thenReturn(5);
        when(mockResultSet.getDouble("avg_rating")).thenReturn(4.2);

        
        instance.generateMonthlyLessonReport(monthName);

        
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

        
        Connection mockConnection = mock(Connection.class);

       
        FitnessClub instance = new FitnessClub(mockConnection);

       
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        
        ResultSet mockResultSet = mock(ResultSet.class);

        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("fitness_type")).thenReturn("Yoga");
        when(mockResultSet.getInt("booking_count")).thenReturn(10);
        when(mockResultSet.getDouble("total_income")).thenReturn(500.0);

        
        instance.generateMonthlyChampionFitnessTypeReport(monthName);

        // Verify the interactions
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(1)).setString(1, monthName);
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(2)).next();
    }

}
