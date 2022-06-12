package com.assignment.warehouse.repository;

import com.assignment.warehouse.model.entity.Products;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ProductsRepository extends CrudRepository<Products, Integer> {

    @Transactional
    @Modifying
    @Query(
    value = "INSERT INTO products (name) values (:name)",
    nativeQuery = true)
    Integer insertProduct(@Param("name") String name);
}
