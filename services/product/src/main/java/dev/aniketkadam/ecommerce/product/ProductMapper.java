package dev.aniketkadam.ecommerce.product;

import dev.aniketkadam.ecommerce.category.Category;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {
    public Product toProduct(ProductRequest request) {
        return Product.builder()
                .id(request.id())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .availableQuantity(request.availableQuantity())
                .category(
                        Category.builder()
                                .id(request.categoryId())
                                .build()
                )
                .build();
    }

    public ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .availableQuantity(product.getAvailableQuantity())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .categoryDescription(product.getCategory().getDescription())
                .build();
    }

    public ProductPurchaseResponse toProductPurchaseResponse(Product product, double quantity) {
        return ProductPurchaseResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(quantity)
                .build();
    }
}
