package com.assignment.warehouse.repository;

import com.assignment.warehouse.model.entity.ProductsWithArticles;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProductsWithArticlesRepository extends CrudRepository<ProductsWithArticles, Integer> {

    @Transactional
    @Modifying
    @Query(
            value = "INSERT INTO products_with_articles (product_id, article_id, amount_of) values (:productId, :articleId, :amountOf)",
            nativeQuery = true
    )
    void insertProductsWithArticles(@Param("productId") Integer prodcutId, @Param("articleId") Integer articleId, @Param("amountOf") Integer amountOf);

    @Query("select pwa from ProductsWithArticles pwa where pwa.productId = ?1")
    List<ProductsWithArticles> findByProductId(Integer productId);

    @Transactional
    @Modifying
    @Query("delete from ProductsWithArticles pwa where pwa.productId=:productId")
    void deleteAllByProductId(@Param("productId") Integer prodcutId);
}
