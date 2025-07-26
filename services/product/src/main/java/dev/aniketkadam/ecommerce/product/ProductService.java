package dev.aniketkadam.ecommerce.product;

import dev.aniketkadam.ecommerce.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Integer createProduct(ProductRequest request) {
        Product product = mapper.toProduct(request);
        return repository.save(product)
                .getId();
    }

    @Transactional
    public List<ProductPurchaseResponse> purchaseProduct(List<ProductPurchaseRequest> request) {
        List<Integer> productIds = request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();
        List<Product> storedProducts = repository.findAllByIdInOrderById(productIds);
        if (productIds.size() != storedProducts.size()) {
            throw new ProductPurchaseException("One or more products does not exists");
        }
        List<ProductPurchaseRequest> storedRequest = request.stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();
        List<ProductPurchaseResponse> purchaseProducts = new ArrayList<>();

        for (int i = 0; i < storedProducts.size(); i++) {
            Product product = storedProducts.get(i);
            ProductPurchaseRequest productRequest = storedRequest.get(i);
            if (product.getAvailableQuantity() < productRequest.quantity()) {
                throw new ProductPurchaseException("Insufficient stock quantity for product with ID:: " + productRequest.productId());
            }
            double newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            repository.save(product);
            purchaseProducts.add(mapper.toProductPurchaseResponse(product, productRequest.quantity()));
        }
        return purchaseProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with the Id:: " + productId));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
