package com.assignment.warehouse.model;

import com.assignment.warehouse.model.entity.Articles;
import lombok.Data;

import java.util.List;

@Data
public class Inventory {
    private List<Articles> inventory;
}