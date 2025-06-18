package com.shopsProject.management.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 도매업체 상품 Entity
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WholesaleProd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wholesaler_uuid", referencedColumnName = "uuid")
    private User wholesaler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int price;

    // 샘플 요청 가능 여부 : default true(가능)
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isSmplAva;

    // 상품 활성화(판매중) 여부 : default true(판매중)
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive;

    // 상품을 등록한 도매업체만 볼 수 있는 memo
    @Column(columnDefinition = "TEXT")
    private String memo;

    // 일반적인 쇼핑몰, 블로그 형태로 상품을 자세하게 소개
    // 작성 여부는 선택적
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProdImg> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProdDetailBlock> detailBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<WholesaleProdVariant> variants = new ArrayList<>();

//    @PrePersist
//    public void prePersist() {
//        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    public void preUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }

}
