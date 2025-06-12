package com.shopsProject.management.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "size", "color"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProdVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private WholesaleProd product;

    @Column(length = 20, nullable = false, columnDefinition = "Varchar(20) default '기본'")
    private String size;

    @Column(length = 20, nullable = false, columnDefinition = "Varchar(20) default '기본'")
    private String color;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int stock;

}
