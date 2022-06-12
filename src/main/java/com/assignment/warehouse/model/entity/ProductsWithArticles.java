package com.assignment.warehouse.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "products_with_articles")
@Entity
public class ProductsWithArticles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer productId;
    private Integer articleId;
    private Integer amountOf;
}
