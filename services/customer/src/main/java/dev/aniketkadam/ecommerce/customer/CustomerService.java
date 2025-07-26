package dev.aniketkadam.ecommerce.customer;

import dev.aniketkadam.ecommerce.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Transactional
    public String createCustomer(CustomerRequest request) {
        Customer customer = repository.save(mapper.toCustomer(request));
        return customer.getId();
    }

    @Transactional
    public void updateCustomer(CustomerRequest request) {
        Customer customer = repository.findById(request.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                        format("Cannot update customer:: No customer found with the provided Id:: %S", request.id())
                ));
        mergerCustomer(customer, request);
        repository.save(customer);
    }

    private void mergerCustomer(Customer customer, CustomerRequest request) {
        if (StringUtils.isNotBlank(request.firstname())) {
            customer.setFirstname(request.firstname());
        }
        if (StringUtils.isNotBlank(request.lastname())) {
            customer.setLastname(request.lastname());
        }
        if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }
        if (request.address() != null) {
            customer.setAddress(request.address());
        }
    }

    public List<CustomerResponse> findALlCustomers() {
        return repository.findAll()
                .stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public Boolean existsById(String customerId) {
        return repository.findById(customerId)
                .isPresent();
    }

    public CustomerResponse findCustomerById(String customerId) {
        return repository.findById(customerId)
                .map(mapper::fromCustomer)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                String.format("Cannot update customer:: No customer found with the provided Id:: %S", customerId)
                ));
    }

    @Transactional
    public void deleteCustomerById(String customerId) {
        repository.deleteById(customerId);
        log.info(String.format("Delete the customer with Id:: %s", customerId));
    }
}
