package com.assignment.warehouse.service;

import com.assignment.warehouse.model.Inventory;
import com.assignment.warehouse.model.entity.Articles;
import com.assignment.warehouse.repository.ArticlesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    private final ArticlesRepository articlesRepository;

    @Autowired
    public InventoryService(ArticlesRepository articlesRepository) {
        this.articlesRepository = articlesRepository;
    }

    @Async
    public void loadArticles(String filePath) throws InterruptedException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Inventory inventory = objectMapper.readValue(new File(filePath), Inventory.class);
            articlesRepository.saveAll(inventory.getInventory());
        } catch (IOException e) {
            LOGGER.info("Exception occurred while processing the inventory file: {} stackTrace: {}", filePath, e.getStackTrace());
            throw (e);
        }
        LOGGER.info("Inventory File: {} successfully processed.", filePath);
    }

    public List<Articles> getArticles() {
        return Streamable.of(articlesRepository.findAll()).toList();
    }

    public Articles getArticleById(Integer id) {
        return articlesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Article: " + id + " not found")
        );
    }

    public void createArticle(Articles articles) {
        try {
            articlesRepository.save(articles);
        } catch (Exception e) {
            LOGGER.error("Exception in creating a article: {} stackTrace: {}.", articles.toString(), e.getStackTrace());
            throw e;
        }
    }

    public void updateArticle(Articles updateArticles, Integer id) {
        Optional<Articles> articles = articlesRepository.findById(id);
        if (articles.isPresent()) {
            Articles article = articles.get();
            article.setName(updateArticles.getName());
            article.setStock(updateArticles.getStock());
            try {
                articlesRepository.save(article);
            } catch (Exception e) {
                LOGGER.error("Exception occurred while updating article Id: {}, stackTrace: {}", id, e.getStackTrace());
                throw e;
            }
        } else {
            throw new ResourceNotFoundException("Article: " + id + " not found");
        }

    }
}
