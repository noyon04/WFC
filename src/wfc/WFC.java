/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package wfc;

import java.util.Scanner;

public class WFC {

    public static void main(String[] args) {
        FitnessClub2 fitnessClub = new FitnessClub2();
        Scanner scanner = new Scanner(System.in);

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
            System.out.println("6. Write a review");
            System.out.println("7. Generate reports");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

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
                    String fitnessType = selectFitnessType(scanner);
                    fitnessClub.displayTimetable(fitnessType);
                    break;
                case 4:
                    System.out.print("Enter customer name: ");
                    String customerName = scanner.nextLine();
                    System.out.print("Enter customer email: ");
                    String customerEmail = scanner.nextLine();
                    String dayBook = selectDay(scanner);
                    String time = selectTime(scanner);
                    String fitnessTypeBook = selectFitnessType(scanner);
                    fitnessClub.bookLesson(customerEmail, dayBook, time, fitnessTypeBook);
                    break;
                case 5:
                    System.out.print("Enter customer email: ");
                    customerEmail = scanner.nextLine();
                    String dayCancel = selectDay(scanner);
                    String timeCancel = selectTime(scanner);
                    String fitnessTypeCancel = selectFitnessType(scanner);
                    fitnessClub.cancelBooking(customerEmail, dayCancel, timeCancel, fitnessTypeCancel);
                    break;
                case 6:
                    System.out.print("Enter customer email: ");
                    customerEmail = scanner.nextLine();
                    String dayReveiw = selectDay(scanner);
                    String timeReview = selectTime(scanner);
                    String fitnessTypeReview = selectFitnessType(scanner);
                    System.out.print("Enter rating (1-5): ");
                    int rating = scanner.nextInt();
                    scanner.nextLine();  // Consume the newline character
                    fitnessClub.writeReview(customerEmail, dayReveiw, timeReview, fitnessTypeReview, rating);
                    break;
                case 7:
                    fitnessClub.generateReports();
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

        scanner.nextLine(); // Consume the newline character
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

        scanner.nextLine(); // Consume the newline character
        return times[timeIndex - 1];
    }

    public static String selectFitnessType(Scanner scanner) {
        String[] fitnessTypes = {"SPIN", "YOGA", "BODYSCULPT", "ZUMBA", "CYCLE RACE"};
        int fitnessTypeIndex;

        do {
            System.out.println("Select a fitness type:");
            System.out.println("1. SPIN");
            System.out.println("2. YOGA");
            System.out.println("3. BODYSCULPT");
            System.out.println("4. ZUMBA");
            System.out.println("5. CYCLE RACE");
            System.out.print("Enter the number: ");
            fitnessTypeIndex = scanner.nextInt();

            if (fitnessTypeIndex < 1 || fitnessTypeIndex > fitnessTypes.length) {
                System.out.println("Invalid choice. Please try again.");
            }
        } while (fitnessTypeIndex < 1 || fitnessTypeIndex > fitnessTypes.length);

        scanner.nextLine(); // Consume the newline character
        return fitnessTypes[fitnessTypeIndex - 1];
    }
}
