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
            System.out.println("1. View timetable by day");
            System.out.println("2. View timetable by fitness type");
            System.out.println("3. Book a lesson");
            System.out.println("4. Cancel a booking");
            System.out.println("5. Write a review");
            System.out.println("6. Generate reports");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Select a day: ");
                    System.out.println("1. Saturday");
                    System.out.println("2. Sunday");
                    System.out.print("Enter the number: ");
                    int dayIndex = scanner.nextInt();
                    scanner.nextLine();  // Consume the newline character
                    String[] days = {"Saturday", "Sunday",};
                    String day = days[dayIndex - 1];
                    fitnessClub.displayTimetable(day);
                    break;
                case 2:
                    System.out.println("Select a fitness type:");
                    System.out.println("1. SPIN");
                    System.out.println("2. YOGA");
                    System.out.println("3. BODYSCULPT");
                    System.out.println("4. ZUMBA");
                    System.out.println("5. CYCLE RACE");
                    System.out.print("Enter the number: ");
                    int fitnessTypeIndex = scanner.nextInt();
                    scanner.nextLine();  // Consume the newline character
                    String[] fitnessTypes = {"SPIN", "YOGA", "BODYSCULPT", "ZUMBA", "CYCLE RACE"};
                    String fitnessType = fitnessTypes[fitnessTypeIndex - 1];
                    fitnessClub.displayTimetable(fitnessType);
                    break;
                case 3:
                    System.out.print("Enter customer name: ");
                    String customerName = scanner.nextLine();
                    System.out.print("Enter customer email: ");
                    String customerEmail = scanner.nextLine();
                    System.out.print("Enter day (Saturday or Sunday): ");
                    day = scanner.nextLine();
                    System.out.print("Enter time (morning or afternoon): ");
                    String time = scanner.nextLine();
                    System.out.print("Enter fitness type: ");
                    fitnessType = scanner.nextLine();
                    fitnessClub.bookLesson(customerEmail, day, time, fitnessType);
                    break;
                case 4:
                    System.out.print("Enter customer email: ");
                    customerEmail = scanner.nextLine();
                    System.out.print("Enter day (Saturday or Sunday): ");
                    day = scanner.nextLine();
                    System.out.print("Enter time (morning or afternoon): ");
                    time = scanner.nextLine();
                    System.out.print("Enter fitness type: ");
                    fitnessType = scanner.nextLine();
                    fitnessClub.cancelBooking(customerEmail, day, time, fitnessType);
                    break;
                case 5:
                    System.out.print("Enter customer email: ");
                    customerEmail = scanner.nextLine();
                    System.out.print("Enter day (Saturday or Sunday): ");
                    day = scanner.nextLine();
                    System.out.print("Enter time (morning or afternoon): ");
                    time = scanner.nextLine();
                    System.out.print("Enter fitness type: ");
                    fitnessType = scanner.nextLine();
                    System.out.print("Enter rating (1-5): ");
                    int rating = scanner.nextInt();
                    scanner.nextLine();  // Consume the newline character
                    fitnessClub.writeReview(customerEmail, day, time, fitnessType, rating);
                    break;
                case 6:
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
}
