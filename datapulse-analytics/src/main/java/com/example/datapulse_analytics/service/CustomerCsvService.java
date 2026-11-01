package com.example.datapulse_analytics.service;

import com.example.datapulse_analytics.model.Customer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerCsvService {

    private final Map<String, Customer> customersById;

    public CustomerCsvService(@Value("${app.data-directory}") String dataDirectory) throws IOException {
        Path csvPath = Path.of(dataDirectory, "olist_customers_dataset.csv");
        if (!Files.exists(csvPath)) {
            throw new IOException("Customer CSV file not found: " + csvPath.toAbsolutePath());
        }

        try (Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreEmptyLines(true)
                     .build()
                     .parse(reader)) {
            customersById = parser.stream()
                    .map(this::toCustomer)
                    .collect(Collectors.toUnmodifiableMap(Customer::getCustomerId, Function.identity()));
        }
    }

    public List<Customer> findAll() {
        return List.copyOf(customersById.values());
    }

    public long count() {
        return customersById.size();
    }

    public Customer findById(String customerId) {
        return customersById.get(customerId);
    }

    private Customer toCustomer(CSVRecord record) {
        Customer customer = new Customer();
        customer.setCustomerId(record.get("customer_id"));
        customer.setCustomerUniqueId(record.get("customer_unique_id"));
        customer.setCustomerZipCodePrefix(Integer.valueOf(record.get("customer_zip_code_prefix")));
        customer.setCustomerCity(record.get("customer_city"));
        customer.setCustomerState(record.get("customer_state"));
        return customer;
    }
}
