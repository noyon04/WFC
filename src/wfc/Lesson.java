/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wfc;

/**
 *
 * @author Hamiduzzaman Noyon
 */
import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private String fitnessType;
    private String startTime;
    private String endTime;
    private List<Customer> bookings;

    public Lesson(String fitnessType, String startTime, String endTime) {
        this.fitnessType = fitnessType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookings = new ArrayList<>();
    }

    public String getFitnessType() {
        return fitnessType;
    }

    public void setFitnessType(String fitnessType) {
        this.fitnessType = fitnessType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<Customer> getBookings() {
        return bookings;
    }

    public void setBookings(List<Customer> bookings) {
        this.bookings = bookings;
    }
}