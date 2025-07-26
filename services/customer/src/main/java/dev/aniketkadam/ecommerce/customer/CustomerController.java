package dev.aniketkadam.ecommerce.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<String> createCustomer(
            @RequestBody @Valid CustomerRequest request
    ) {
        return ResponseEntity
                .status(CREATED)
                .body(service.createCustomer(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateCustomer(
            @RequestBody @Valid CustomerRequest request
    ) {
        service.updateCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAll() {
        return ResponseEntity
                .ok(service.findALlCustomers());
    }

    @GetMapping("/exists/{customer-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("customer-id") String customerId
    ) {
        return ResponseEntity.ok(service.existsById(customerId));
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<CustomerResponse> findCustomerById(
            @PathVariable("customer-id") String customerId
    ) {
        return ResponseEntity.ok(service.findCustomerById(customerId));
    }

    @DeleteMapping("/customer-id")
    public ResponseEntity<Void> deleteCustomerById(
            @PathVariable("customer-id") String customerId
    ) {
        service.deleteCustomerById(customerId);
        return ResponseEntity.accepted().build();
    }


}
