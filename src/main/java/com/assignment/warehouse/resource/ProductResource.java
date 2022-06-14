package com.assignment.warehouse.resource;

import com.assignment.warehouse.model.ProductMadeOf;
import com.assignment.warehouse.model.ProductsWrapper;
import com.assignment.warehouse.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/products")
public class ProductResource {

    private final ProductsService productsService;

    @Autowired
    public ProductResource(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping("/load")
    public ResponseEntity loadProducts(@RequestParam String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (Files.exists(path)) {
            productsService.loadProducts(filePath);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping
    public ProductsWrapper getProducts() {
        return productsService.getProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity getProductById(@PathVariable("id") Integer productId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productsService.getProductById(productId));
        } catch (ResourceNotFoundException rnf) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity createProduct(@RequestBody ProductMadeOf productMadeOf) {
        try {
            productsService.createProduct(productMadeOf);
        } catch (Exception cve) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity updateProduct(@RequestBody ProductMadeOf productMadeOf, @PathVariable Integer id) {
        try {
            productsService.updateProduct(productMadeOf, id);
        } catch (Exception cve) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable Integer id) {
        try {
            productsService.deleteProduct(id);
        } catch (ResourceNotFoundException rnf) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}")
    public ResponseEntity sellProduct(@PathVariable Integer id) {
        try {
            productsService.sellProduct(id);
        } catch (RuntimeException rnf) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
