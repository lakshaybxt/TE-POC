package com.testing.ex.domain.entity;

import com.testing.ex.domain.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Product entity is generic: can represent phones, tablets, etc.
 * Multi-tenancy is enforced by tenantId column and repository filters.
 * Faster lookups + no duplicate data for the same tenant.
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "ix_products_tenant_sku", columnList = "tenantId,sku", unique = true),
    @Index(name = "ix_products_tenant_category", columnList = "tenantId,category"),
    @Index(name = "ix_products_tenant_name", columnList = "tenantId,name")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @NotBlank
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotBlank
    @Column(name = "sku", nullable = false, length = 80)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 60)
    private Category category;

    @NotNull
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "description", length = 2000)
    private String description;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "features_id", referencedColumnName = "id")
    private Features features;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}


