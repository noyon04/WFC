/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wfc;

import java.util.*;

public class FitnessClub {

    private List<Customer> customers;
    private Map<String, List<Lesson>> timetable;
    private Map<String, List<Review>> reviews;

    public FitnessClub() {
        customers = new ArrayList<>();
        timetable = new HashMap<>();
        reviews = new HashMap<>();

        // Initialize the timetable with sample lessons
        initializeTimetable();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    private void initializeTimetable() {
        timetable.put("Saturday", Arrays.asList(
                new Lesson("SPIN", "10:00am", "11:00am"),
                new Lesson("YOGA", "12:00pm", "1:00pm")
        ));

        timetable.put("Sunday", Arrays.asList(
                new Lesson("SPIN", "10:00am", "11:00am"),
                new Lesson("YOGA", "12:00pm", "1:00pm")
        ));
    }

    public void displayTimetable(String filter) {
        System.out.println(filter + " Timetable:");

        List<Lesson> lessons = timetable.get(filter);
        if (lessons == null) {
            System.out.println("Invalid input. Please try again.");
            return;
        }

        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            System.out.println((i + 1) + ". " + lesson.getFitnessType() + " - " + lesson.getStartTime() + " to " + lesson.getEndTime());
        }
    }

    public void bookLesson(String customerName, String customerEmail, String day, String time, String fitnessType) {
        Lesson lesson = findLesson(day, time, fitnessType);
        if (lesson == null) {
            System.out.println("Invalid input. Please try again.");
            return;
        }

        Customer customer = findCustomerByEmail(customerEmail);
        if (customer == null) {
            customer = new Customer(customerName, customerEmail);
            addCustomer(customer);
        }

        lesson.getBookings().add(customer);
        System.out.println("Booking successful! You are booked for " + fitnessType + " on " + day + " at " + lesson.getStartTime() + ".");
    }

    public void cancelBooking(String customerEmail, String day, String time, String fitnessType) {
        Lesson lesson = findLesson(day, time, fitnessType);
        if (lesson == null) {
            System.out.println("Invalid input. Please try again.");
            return;
        }

        Customer customer = findCustomerByEmail(customerEmail);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        if (lesson.getBookings().remove(customer)) {
            System.out.println("Booking cancelled successfully.");
        } else {
            System.out.println("Booking not found.");
        }
    }

    public void writeReview(String customerEmail, String day, String time, String fitnessType, int rating) {
        String key = customerEmail + day + time + fitnessType;
        List<Review> customerReviews = reviews.getOrDefault(key, new ArrayList<>());
        customerReviews.add(new Review(customerEmail, day, time, fitnessType, rating));
        reviews.put(key, customerReviews);
        System.out.println("Review and rating recorded successfully.");
    }

    public void generateReports() {
        System.out.println("Reports:");
        System.out.println("1. Number of customers per lesson on each day:");

        for (String day : timetable.keySet()) {
            List<Lesson> lessons = timetable.get(day);

            for (Lesson lesson : lessons) {
                String fitnessType = lesson.getFitnessType();
                int numberOfCustomers = lesson.getBookings().size();
                double avgRating = calculateAverageRating(day, lesson.getStartTime(), fitnessType);

                System.out.print(day + " - " + fitnessType + " - " + numberOfCustomers + " customer");
                if (numberOfCustomers != 1) {
                    System.out.print("s");
                }
                System.out.print(" - Avg rating: ");
                if (avgRating >= 0) {
                    System.out.println(avgRating);
                } else {
                    System.out.println("N/A");
                }
            }
        }
    }

    private double calculateAverageRating(String day, String time, String fitnessType) {
        int totalRating = 0;
        int count = 0;

        for (List<Review> reviewList : reviews.values()) {
            for (Review review : reviewList) {
                if (review.getDay().equalsIgnoreCase(day) && review.getTime().equalsIgnoreCase(time) && review.getFitnessType().equalsIgnoreCase(fitnessType)) {
                    totalRating += review.getRating();
                    count++;
                }
            }
        }

        return count > 0 ? (double) totalRating / count : -1;
    }

    private Lesson findLesson(String day, String time, String fitnessType) {
        List<Lesson> lessons = timetable.get(day);

        if (lessons != null) {
            for (Lesson lesson : lessons) {
                if (lesson.getStartTime().equalsIgnoreCase(time) && lesson.getFitnessType().equalsIgnoreCase(fitnessType)) {
                    return lesson;
                }
            }
        }

        return null;
    }

    private Customer findCustomerByEmail(String email) {
        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                return customer;
            }
        }

        return null;
    }

}
