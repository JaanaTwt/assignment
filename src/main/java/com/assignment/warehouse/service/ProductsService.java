package com.assignment.warehouse.service;

import com.assignment.warehouse.model.ContainsArticles;
import com.assignment.warehouse.model.ProductMadeOf;
import com.assignment.warehouse.model.ProductsWrapper;
import com.assignment.warehouse.model.entity.Articles;
import com.assignment.warehouse.model.entity.Products;
import com.assignment.warehouse.model.entity.ProductsWithArticles;
import com.assignment.warehouse.repository.ArticlesRepository;
import com.assignment.warehouse.repository.ProductsRepository;
import com.assignment.warehouse.repository.ProductsWithArticlesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ProductsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsService.class);

    private final ArticlesRepository articlesRepository;
    private final ProductsRepository productsRepository;
    private final ProductsWithArticlesRepository productsWithArticlesRepository;

    @Autowired
    public ProductsService(ArticlesRepository articlesRepository, ProductsRepository productsRepository, ProductsWithArticlesRepository productsWithArticlesRepository) {
        this.articlesRepository = articlesRepository;
        this.productsRepository = productsRepository;
        this.productsWithArticlesRepository = productsWithArticlesRepository;
    }

    @Async
    public void loadProducts(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductsWrapper productsWrapper = null;
        try {
            productsWrapper = objectMapper.readValue(new File(filePath), ProductsWrapper.class);
        } catch (IOException e) {
            LOGGER.info("Exception occurred while processing the products file: {} stackTrace: {}", filePath, e.getStackTrace());
            throw (e);
        }
        productsWrapper.getProducts().forEach(productMadeOf -> {
            Products products = new Products();
            products.setName(productMadeOf.getName());
            Products product = productsRepository.save(products);
            productMadeOf.getContainsArticles().forEach(containsArticles ->
                    productsWithArticlesRepository.insertProductsWithArticles(product.getId(), containsArticles.getArtId(), containsArticles.getAmountOf())
            );
        });
        LOGGER.info("Products file: {} processed successfully.", filePath);
    }

    public ProductsWrapper getProducts() {
        ProductsWrapper productsWrapper = new ProductsWrapper();
        Map<Integer, ProductMadeOf> productMadeOfMap = new HashMap<>();
        productsWithArticlesRepository.findAll().forEach(productsWithArticle -> {
            Integer productId = productsWithArticle.getProductId();
            if (!productMadeOfMap.containsKey(productId)) {
                productMadeOfMap.put(productId, new ProductMadeOf());
            }
            ProductMadeOf productMadeOf = productMadeOfMap.get(productId);
            productMadeOf.setId(productsWithArticle.getProductId());
            Optional<Products> products = productsRepository.findById(productsWithArticle.getProductId());
            productMadeOf.setName(products.isPresent() ? products.get().getName() : "");
            mapToProductMadeOf(productsWithArticle, productMadeOf);
            productMadeOfMap.put(productId, productMadeOf);
        });
        List<ProductMadeOf> productMadeOfList = new ArrayList<>();
        productMadeOfList.addAll(productMadeOfMap.values());
        productsWrapper.setProducts(productMadeOfList);
        return productsWrapper;
    }

    public void createProduct(ProductMadeOf productMadeOf) {
        Products products = new Products();
        products.setName(productMadeOf.getName());
        try {
            Products product = productsRepository.save(products);
            productMadeOf.getContainsArticles().forEach(containsArticles ->
                    productsWithArticlesRepository.insertProductsWithArticles(product.getId(), containsArticles.getArtId(), containsArticles.getAmountOf())
            );
        } catch (Exception e) {
            LOGGER.error("Exception in creating a product: {} stackTrace: {}.", productMadeOf.toString(), e.getStackTrace());
            throw e;
        }
    }

    public ProductMadeOf getProductById(Integer productId) {
        Optional<Products> products = productsRepository.findById(productId);
        if (products.isPresent()) {
            ProductMadeOf productMadeOf = new ProductMadeOf();
            productMadeOf.setId(productId);
            productMadeOf.setName(products.get().getName());
            productsWithArticlesRepository.findByProductId(productId).forEach(productsWithArticle -> {
                mapToProductMadeOf(productsWithArticle, productMadeOf);
            });
            return productMadeOf;
        }
        throw new ResourceNotFoundException("Product: " + productId + " not found");
    }

    public void updateProduct(ProductMadeOf productMadeOf, Integer productId) {
        Optional<Products> products = productsRepository.findById(productId);
        if (products.isPresent()) {
            products.get().setName(productMadeOf.getName());
            try {
                productsRepository.save(products.get());
                productsWithArticlesRepository.deleteAllByProductId(productId);
                productMadeOf.getContainsArticles().forEach(containsArticles ->
                        productsWithArticlesRepository.insertProductsWithArticles(productId, containsArticles.getArtId(), containsArticles.getAmountOf())
                );
            } catch (Exception e) {
                LOGGER.error("Exception occurred while updating productId: {}, stackTrace: {}", productId, e.getStackTrace());
                throw e;
            }
        } else {
            throw new ResourceNotFoundException("Product: " + productId + " not found");
        }
    }

    public void deleteProduct(Integer productId) {
        Optional<Products> products = productsRepository.findById(productId);
        if (products.isPresent()) {
            productsRepository.deleteById(productId);
            productsWithArticlesRepository.deleteAllByProductId(productId);
        } else {
            throw new ResourceNotFoundException("Product: " + productId + " not found");
        }
    }

    public void sellProduct(Integer productId) {
        List<ProductsWithArticles> productsWithArticles = productsWithArticlesRepository.findByProductId(productId);
        List<Articles> updateArticles = new ArrayList<>();
        productsWithArticles.forEach(productsWithArticle -> {
            Optional<Articles> articles = articlesRepository.findById(productsWithArticle.getArticleId());
            if (articles.isPresent()) {
                Articles article = articles.get();
                if (article.getStock() < productsWithArticle.getAmountOf()) {
                    LOGGER.info("No Stock available for Product: {}", productId);
                    throw new RuntimeException();
                }
                article.setStock(article.getStock() - productsWithArticle.getAmountOf());
                updateArticles.add(article);
            } else{
                LOGGER.info("No Stock available for Product: {}", productId);
                throw new RuntimeException();
            }
        });
        articlesRepository.saveAll(updateArticles);
    }

    private void mapToProductMadeOf(ProductsWithArticles productsWithArticle, ProductMadeOf productMadeOf) {
        ContainsArticles containsArticles = new ContainsArticles();
        Integer articleId = productsWithArticle.getArticleId();
        containsArticles.setArtId(articleId);
        containsArticles.setAmountOf(productsWithArticle.getAmountOf());
        productMadeOf.addContainsArticles(containsArticles);
        Optional<Articles> articles = articlesRepository.findById(articleId);
        articles.ifPresent(value -> {
            productMadeOf.setAvailable(value.getStock() >= productsWithArticle.getAmountOf());
            if (productMadeOf.getStock() == null) {
                productMadeOf.setStock(value.getStock() > 0 ? Math.floorDiv(value.getStock(), productsWithArticle.getAmountOf()) : 0);
            } else {
                productMadeOf.setStock(Math.min(value.getStock() > 0 ? Math.floorDiv(value.getStock(), productsWithArticle.getAmountOf()) : 0, productMadeOf.getStock()));
            }
        });
    }
}
