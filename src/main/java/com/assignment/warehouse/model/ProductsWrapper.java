package com.assignment.warehouse.model;

import lombok.Data;

import java.util.List;

@Data
public class ProductsWrapper {
    private List<ProductMadeOf> products;

}

