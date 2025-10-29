package com.testing.ex.service.impl;

import com.testing.ex.domain.dto.request.CreateFeature;
import com.testing.ex.domain.dto.request.CreateProduct;
import com.testing.ex.domain.dto.request.UpdateFeature;
import com.testing.ex.domain.dto.request.UpdateProduct;
import com.testing.ex.domain.dto.response.FeatureResponse;
import com.testing.ex.domain.dto.response.ProductResponse;
import com.testing.ex.domain.entity.Features;
import com.testing.ex.domain.entity.Product;
import com.testing.ex.repos.ProductRepository;
import com.testing.ex.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.slf4j.MDC;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Retrieves all products associated with a specific user.
     *
     * @param userId The ID of the user whose products are to be retrieved.
     * @return A page of ProductDtos.Response representing the user's products.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllByUserId(String userId, Pageable pageable) {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            Page<Product> page = productRepository.findAllByTenantId(userId, pageable);
            Page<ProductResponse> result = page.map((Product p) -> toResponse(p));

            sw.stop();
            log.info("action=getAllProducts userId={} productCount={} durationMs={}",
                    userId, result.getTotalElements(), sw.getTotalTimeMillis());
            return result;
        } catch (Exception e) {
            sw.stop();
            log.error("action=getAllProducts userId={} error={}",
                    userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Creates a new product for a specific user.
     *
     * @param userId  The ID of the user creating the product.
     * @param request The product creation request containing product details.
     * @return A ProductDtos.Response representing the created product.
     */
    @Override
    @Transactional
    public ProductResponse createProduct(String userId, CreateProduct request) {
        StopWatch sw = new StopWatch();
        sw.start();

        log.debug("action=createProduct userId={} name={} sku={} price={} category={}",
              userId, request.name(), request.sku(), request.price(), request.category());

        CreateFeature featureDto = request.features();
        Features features = Features.builder()
            .ram(featureDto.ram())
            .storage(featureDto.storage())
            .battery(featureDto.battery())
            .build();

        Product product = Product.builder()
                .tenantId(userId)
                .name(request.name())
                .sku(request.sku())
                .category(request.category())
                .price(request.price())
                .description(request.description())
                .features(features)
                .build();

        try {
            Product saved = productRepository.save(product);
            sw.stop();
            log.info("action=createProduct userId={} productId={} durationMs={}",
                    userId, saved.getId(), sw.getTotalTimeMillis());
            return toResponse(saved);
        } catch (Exception e) {
            sw.stop();
            log.error("action=createProduct userId={} error={}",
                    userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Updates an existing product for a specific user.
     *
     * @param userId    The ID of the user updating the product.
     * @param productId The ID of the product to be updated.
     * @param request   The product update request containing updated details.
     * @return A ProductDtos.Response representing the updated product.
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(String userId, Long productId, UpdateProduct request) {
        StopWatch sw = new StopWatch();
        sw.start();

        log.debug("action=updateProduct userId={} productId={} payloadName={} payloadSku={}",
                userId, productId, request.name(), request.sku());

        try {
            Product existing = productRepository.findByIdAndTenantId(productId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found or access denied"));

            if (request.name() != null) existing.setName(request.name());
            if (request.sku() != null) existing.setSku(request.sku());
            if (request.price() != null) existing.setPrice(request.price());
            if (request.description() != null) existing.setDescription(request.description());
            if (request.category() != null) existing.setCategory(request.category());

            UpdateFeature featureDto = request.features();
            if (featureDto != null) {
                Features features = existing.getFeatures();
                if (features == null) {
                    features = new Features();
                    existing.setFeatures(features);
                }
                if (featureDto.ram() != null)
                  features.setRam(featureDto.ram());
                if (featureDto.storage() != null)
                    features.setStorage(featureDto.storage());
                if (featureDto.battery() != null)
                    features.setBattery(featureDto.battery());
            }

            Product saved = productRepository.save(existing);
            sw.stop();
            log.info("action=updateProduct userId={} productId={} durationMs={}",
                    userId, productId, sw.getTotalTimeMillis());
            return toResponse(saved);
        } catch (Exception e) {
            sw.stop();
            log.error("action=updateProduct userId={} productId={} error={}",
                userId, productId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Deletes a product for a specific user.
     *
     * @param userId    The ID of the user deleting the product.
     * @param productId The ID of the product to be deleted.
     */
    @Override
    @Transactional
    public void deleteProduct(String userId, Long productId) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        MDC.put("productId", String.valueOf(productId));
        StopWatch sw = new StopWatch();
        sw.start();

        log.info("action=deleteProduct requestId={} userId={} productId={}", requestId, userId, productId);
        try {
            Product existing = productRepository.findByIdAndTenantId(productId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found or access denied"));
            productRepository.delete(existing);
            sw.stop();
            log.info("action=deleteProduct requestId={} userId={} productId={} durationMs={}",
                    requestId, userId, productId, sw.getTotalTimeMillis());
        } catch (Exception e) {
            sw.stop();
            log.error("action=deleteProduct requestId={} userId={} productId={} error={}",
                    requestId, userId, productId, e.getMessage(), e);
            throw e;
        } finally {
            MDC.remove("requestId");
            MDC.remove("userId");
            MDC.remove("productId");
        }
    }

    @Override
    public Product getProductEntityByIdAndUserId(Long productId, String userId) {
        return productRepository.findByIdAndTenantId(productId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or access denied"));
    }

    /**
     * Converts a Product entity to a ProductDtos.Response DTO.
     *
     * @param p The Product entity to convert.
     * @return The corresponding ProductDtos.Response DTO.
     */
    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getSku(),
                p.getCategory().name(),
                p.getPrice(),
                p.getFeatures() != null ? new FeatureResponse(
                        p.getFeatures().getRam(),
                        p.getFeatures().getStorage(),
                        p.getFeatures().getBattery()
                ) : null,
                p.getDescription()
        );
    }
}

