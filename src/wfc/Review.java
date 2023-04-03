/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wfc;

/**
 *
 * @author Hamiduzzaman Noyon
 */
public class Review {
    private String customerEmail;
    private String day;
    private String time;
    private String fitnessType;
    private int rating;

    public Review(String customerEmail, String day, String time, String fitnessType, int rating) {
        this.customerEmail = customerEmail;
        this.day = day;
        this.time = time;
        this.fitnessType = fitnessType;
        this.rating = rating;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFitnessType() {
        return fitnessType;
    }

    public void setFitnessType(String fitnessType) {
        this.fitnessType = fitnessType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}