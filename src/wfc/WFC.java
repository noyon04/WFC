/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package wfc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WFC {

    public static void main(String[] args) {
        FitnessClub fitnessClub = new FitnessClub();
        Scanner scanner = new Scanner(System.in);
        ConnectionDB db = new ConnectionDB();
        Connection connection = db.getConnection();
        fitnessClub.loadDummyDataIfFirstRun();

        // Add some pre-registered customers
        //fitnessClub.addCustomer(new Customer("John Doe", "john.doe@example.com"));
        //fitnessClub.addCustomer(new Customer("Jane Doe", "jane.doe@example.com"));
        // Print the available options
        System.out.println("Welcome to the Weekend Fitness Club booking system!");
        System.out.println("Please choose an option:");

        // Process the user's choice
        int choice;
        do {
            System.out.println("\n");
            System.out.println("1. Add a Customer");
            System.out.println("2. View timetable by day");
            System.out.println("3. View timetable by fitness type");
            System.out.println("4. Book a lesson");
            System.out.println("5. Cancel a booking");
            System.out.println("6. Attend a lesson");
            System.out.println("7. Change a lesson");
            System.out.println("8. Write a review");
            System.out.println("9. Generate reports");
            System.out.println("10. All Customers");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();  

            switch (choice) {
                case 1:
                    System.out.print("Enter customer name: ");
                    String cName = scanner.nextLine();
                    System.out.print("Enter customer email: ");
                    String cEmail = scanner.nextLine();
                    fitnessClub.addCustomer(new Customer(cName, cEmail));
                    break;
                case 2:
                    String day = selectDay(scanner);
                    fitnessClub.displayTimetable(day);
                    break;
                case 3:
                    String fitnessType = selectFitnessType(scanner, connection);
                    fitnessClub.displayTimetable(fitnessType);
                    break;
                case 4:
                    System.out.print("Enter customer name: ");
                    String customerName = scanner.nextLine();
                    System.out.print("Enter customer email: ");
                    String customerEmail = scanner.nextLine();
                    String dayBook = selectDay(scanner);
                    String time = selectTime(scanner);
                    String fitnessTypeBook = selectFitnessType(scanner, connection);
                    fitnessClub.bookLesson(customerEmail, dayBook, time, fitnessTypeBook);
                    break;

                case 5:
                    System.out.print("Enter customer email: ");
                    customerEmail = scanner.nextLine();
                    int bookingToCancel = fitnessClub.listBookingsForCustomer(customerEmail, "booked");
                    if (bookingToCancel != -1) {
                        fitnessClub.cancelBooking(customerEmail, bookingToCancel);
                    }
                    break;
                case 6:
                    System.out.print("Enter customer email: ");
                    customerEmail = scanner.nextLine();
                    int bookingToAttend = fitnessClub.listBookingsForCustomer(customerEmail, "booked");
                    if (bookingToAttend != -1) {
                        fitnessClub.attendLesson(customerEmail, bookingToAttend);
                    }
                    break;
                case 7:
                    System.out.print("Enter customer email: ");
                    customerEmail = scanner.nextLine();
                    int oldBookingToChange = fitnessClub.listBookingsForCustomer(customerEmail, "booked");
                    if (oldBookingToChange != -1) {
                        System.out.println("Enter the new lesson details:");
                        String newDay = selectDay(scanner);
                        String newTime = selectTime(scanner);
                        String newFitnessType = selectFitnessType(scanner, connection);
                        fitnessClub.changeLesson(customerEmail, oldBookingToChange, newDay, newTime, newFitnessType);
                    }
                    break;
                case 8:
                    System.out.print("Enter customer email: ");
                    customerEmail = scanner.nextLine();
                    int writeReview = fitnessClub.listBookingsForWriteReview(customerEmail, "attended");
                    if (writeReview != -1) {
                        System.out.print("Enter rating (1-5): ");
                        int rating = scanner.nextInt();
                        scanner.nextLine();  
                        System.out.print("Enter your review: ");
                        String reviewText = scanner.nextLine();
                        fitnessClub.writeReview(customerEmail, writeReview, rating, reviewText);
                    }

                    break;
                case 9:
                    System.out.println("Select a report type:");
                    System.out.println("1. Monthly lesson report");
                    System.out.println("2. Monthly champion fitness type report");
                    System.out.print("Enter the number: ");
                    int reportType = scanner.nextInt();
                    scanner.nextLine(); 
                    System.out.print("Enter the month number (e.g., 03 for March): ");
                    int month = scanner.nextInt();
                    scanner.nextLine(); 
                    fitnessClub.generateReports(reportType, month);
                    break;
                case 10:
                    fitnessClub.printAllCustomers();
                    break;
                case 0:
                    System.out.println("Thank you for using the Weekend Fitness Club booking system!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

        scanner.close();
    }

    public static String selectDay(Scanner scanner) {
        String[] days = {"Saturday", "Sunday"};
        int dayIndex;

        do {
            System.out.println("Select a day: ");
            System.out.println("1. Saturday");
            System.out.println("2. Sunday");
            System.out.print("Enter the number: ");
            dayIndex = scanner.nextInt();

            if (dayIndex < 1 || dayIndex > days.length) {
                System.out.println("Invalid choice. Please try again.");
            }
        } while (dayIndex < 1 || dayIndex > days.length);

        scanner.nextLine(); 
        return days[dayIndex - 1];
    }

    public static String selectTime(Scanner scanner) {
        String[] times = {"10.00.00", "12.00.00"};
        int timeIndex;

        do {
            System.out.println("Select a time: ");
            System.out.println("1. 10.00");
            System.out.println("2. 12.00");
            System.out.print("Enter the number: ");
            timeIndex = scanner.nextInt();

            if (timeIndex < 1 || timeIndex > times.length) {
                System.out.println("Invalid choice. Please try again.");
            }
        } while (timeIndex < 1 || timeIndex > times.length);

        scanner.nextLine(); 
        return times[timeIndex - 1];
    }

    public static String selectFitnessType(Scanner scanner, Connection connection) {
        Map<String, BigDecimal> fitnessTypePrices = getFitnessTypePrices(connection);
        List<String> fitnessTypes = new ArrayList<>(fitnessTypePrices.keySet());
        int fitnessTypeIndex;

        do {
            System.out.println("Select a fitness type:");
            for (int i = 0; i < fitnessTypes.size(); i++) {
                String fitnessType = fitnessTypes.get(i);
                BigDecimal price = fitnessTypePrices.get(fitnessType);
                System.out.printf("%d. %s ($%.2f)%n", i + 1, fitnessType, price);
            }
            System.out.print("Enter the number: ");
            fitnessTypeIndex = scanner.nextInt();

            if (fitnessTypeIndex < 1 || fitnessTypeIndex > fitnessTypes.size()) {
                System.out.println("Invalid choice. Please try again.");
            }
        } while (fitnessTypeIndex < 1 || fitnessTypeIndex > fitnessTypes.size());

        scanner.nextLine(); 
        return fitnessTypes.get(fitnessTypeIndex - 1);
    }

    public static Map<String, BigDecimal> getFitnessTypePrices(Connection connection) {
        Map<String, BigDecimal> prices = new HashMap<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT fitness_type, price FROM lessons");

            while (resultSet.next()) {
                String fitnessType = resultSet.getString("fitness_type");
                BigDecimal price = resultSet.getBigDecimal("price");
                prices.putIfAbsent(fitnessType, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prices;
    }
}
