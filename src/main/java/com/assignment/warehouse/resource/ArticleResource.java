package com.assignment.warehouse.resource;

import com.assignment.warehouse.model.entity.Articles;
import com.assignment.warehouse.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleResource {

    private final InventoryService inventoryService;

    @Autowired
    public  ArticleResource(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @GetMapping("/load")
    public ResponseEntity loadArticles(@RequestParam String filePath) throws InterruptedException, IOException {
        Path path = Paths.get(filePath);
        if(Files.exists(path)){
            this.inventoryService.loadArticles(filePath);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping
    public List<Articles> getArticles(){
        return inventoryService.getArticles();
    }

    @GetMapping("/{id}")
    public ResponseEntity getArticlesById(@PathVariable("id") Integer id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(inventoryService.getArticleById(id));
        } catch (ResourceNotFoundException rnf){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity createArticle(@RequestBody Articles articles) {
        try {
            inventoryService.createArticle(articles);
        } catch (Exception cve) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity updateArticle(@RequestBody Articles articles, @PathVariable Integer id) {
        try {
            inventoryService.updateArticle(articles, id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
