package com.example.datapulse_analytics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers", indexes = {
        @jakarta.persistence.Index(name = "idx_customers_state", columnList = "customer_state")
})
public class Customer {

    @Id
    @Column(name = "customer_id", nullable = false, length = 32)
    private String customerId;

    @Column(name = "customer_unique_id", nullable = false, length = 32)
    private String customerUniqueId;

    @Column(name = "customer_zip_code_prefix")
    private Integer customerZipCodePrefix;

    @Column(name = "customer_city")
    private String customerCity;

    @Column(name = "customer_state")
    private String customerState;

    public Customer() {}

    // Getters et Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerUniqueId() { return customerUniqueId; }
    public void setCustomerUniqueId(String customerUniqueId) { this.customerUniqueId = customerUniqueId; }

    public Integer getCustomerZipCodePrefix() { return customerZipCodePrefix; }
    public void setCustomerZipCodePrefix(Integer customerZipCodePrefix) { this.customerZipCodePrefix = customerZipCodePrefix; }

    public String getCustomerCity() { return customerCity; }
    public void setCustomerCity(String customerCity) { this.customerCity = customerCity; }

    public String getCustomerState() { return customerState; }
    public void setCustomerState(String customerState) { this.customerState = customerState; }
}
