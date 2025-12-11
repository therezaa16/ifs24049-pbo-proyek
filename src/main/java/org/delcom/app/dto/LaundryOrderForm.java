package org.delcom.app.dto;

import java.util.UUID;

public class LaundryOrderForm {

    private UUID id;

    private String customerName;

    private String phoneNumber;

    private String serviceType;

    private Double weight;

    private Double price;

    private String status;

    private String notes;

    private String confirmCustomerName; // untuk konfirmasi delete

    // Constructor
    public LaundryOrderForm() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getConfirmCustomerName() {
        return confirmCustomerName;
    }

    public void setConfirmCustomerName(String confirmCustomerName) {
        this.confirmCustomerName = confirmCustomerName;
    }
}