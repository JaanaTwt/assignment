package com.assignment.warehouse.resource;

import com.assignment.warehouse.WarehouseApplication;
import com.assignment.warehouse.model.ContainsArticles;
import com.assignment.warehouse.model.ProductMadeOf;
import com.assignment.warehouse.model.entity.Articles;
import com.assignment.warehouse.model.entity.Products;
import com.assignment.warehouse.model.entity.ProductsWithArticles;
import com.assignment.warehouse.repository.ArticlesRepository;
import com.assignment.warehouse.repository.ProductsRepository;
import com.assignment.warehouse.repository.ProductsWithArticlesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = WarehouseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductResourceIT {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ProductsRepository productsRepository;

    @MockBean
    private ProductsWithArticlesRepository productsWithArticlesRepository;

    @MockBean
    private ArticlesRepository articlesRepository;

    @Test
    public void testLoadingProductsFile() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/products/load?filePath=src/test/resources/products.json", String.class);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        ResponseEntity<String> responseWithStatus400 = restTemplate.getForEntity("/products/load?filePath=src/test/resources/wrongProducts.json", String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseWithStatus400.getStatusCode());
    }

    @Test
    public void testGetProducts() throws JSONException {
        ProductsWithArticles productsWithArticles = new ProductsWithArticles();
        productsWithArticles.setArticleId(1);
        productsWithArticles.setProductId(1);
        productsWithArticles.setAmountOf(4);

        Products products = new Products();
        products.setId(1);
        products.setName("Dining Table");

        Articles article = new Articles();
        article.setId(1);
        article.setName("leg");
        article.setStock(12);

        when(productsWithArticlesRepository.findAll()).thenReturn(Collections.singleton(productsWithArticles));
        when(productsRepository.findById(1)).thenReturn(Optional.of(products));
        when(articlesRepository.findById(1)).thenReturn(Optional.of(article));

        String expected = "{products:[{id:1,name:\"Dining Table\",available:true,stock:3,contain_articles:[{art_id:1,amount_of:4}]}]}";
        ResponseEntity<String> response = restTemplate.getForEntity("/products", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), true);

        verify(productsWithArticlesRepository, times(1)).findAll();
        verify(productsRepository, times(1)).findById(1);
        verify(articlesRepository, times(1)).findById(1);
    }

    @Test
    public void testGetProductsById() throws JSONException, JsonProcessingException {
        ProductsWithArticles productsWithArticles = new ProductsWithArticles();
        productsWithArticles.setArticleId(1);
        productsWithArticles.setProductId(1);
        productsWithArticles.setAmountOf(4);

        Products products = new Products();
        products.setId(1);
        products.setName("Dining Table");

        Articles article = new Articles();
        article.setId(1);
        article.setName("leg");
        article.setStock(12);

        when(productsRepository.findById(1)).thenReturn(Optional.of(products));
        when(productsWithArticlesRepository.findByProductId(1)).thenReturn(Collections.singletonList(productsWithArticles));
        when(articlesRepository.findById(1)).thenReturn(Optional.of(article));

        String expected = "{id:1,name:\"Dining Table\",available:true,stock:3,contain_articles:[{art_id:1,amount_of:4}]}";
        ResponseEntity<String> response = restTemplate.getForEntity("/products/1", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), true);

        verify(productsRepository, times(1)).findById(1);
        verify(productsWithArticlesRepository, times(1)).findByProductId(1);
        verify(articlesRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateProducts() {
        ProductMadeOf productMadeOf = new ProductMadeOf();
        productMadeOf.setName("Dining Table");
        ContainsArticles containsArticle = new ContainsArticles();
        containsArticle.setArtId(1);
        containsArticle.setAmountOf(4);
        List<ContainsArticles> containsArticles = List.of(containsArticle);
        productMadeOf.setContainsArticles(containsArticles);

        Products products = new Products();
        products.setId(1);
        products.setName("Dining Table");

        when(productsRepository.save(any())).thenReturn(products);
        ResponseEntity<String> response = restTemplate.postForEntity("/products", productMadeOf, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        when(productsRepository.save(any())).thenThrow(RuntimeException.class);

        ResponseEntity<String> exceptionResponse = restTemplate.postForEntity("/products", productMadeOf, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatusCode());
        verify(productsRepository, times(2)).save(any());
        verify(productsWithArticlesRepository, times(1)).insertProductsWithArticles(1,1,4);
    }

    @Test
    public void testUpdateProduct() {
        ProductMadeOf productMadeOf = new ProductMadeOf();
        productMadeOf.setName("Dining Table");
        ContainsArticles containsArticle = new ContainsArticles();
        containsArticle.setArtId(1);
        containsArticle.setAmountOf(4);
        List<ContainsArticles> containsArticles = List.of(containsArticle);
        productMadeOf.setContainsArticles(containsArticles);

        Products products = new Products();
        products.setId(1);
        products.setName("Dining Table");

        when(productsRepository.findById(1)).thenReturn(Optional.of(products));
        ResponseEntity<String> response = restTemplate.exchange("/products/1", HttpMethod.PUT, new HttpEntity<>(productMadeOf), String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        when(productsRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<String> responseWithStatus400 = restTemplate.exchange("/products/1", HttpMethod.PUT, new HttpEntity<>(productMadeOf), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseWithStatus400.getStatusCode());

        verify(productsRepository, times(2)).findById(1);
        verify(productsRepository, times(1)).save(products);
        verify(productsWithArticlesRepository, times(1)).deleteAllByProductId(1);
        verify(productsWithArticlesRepository, times(1)).insertProductsWithArticles(1,1,4);
    }

    @Test
    public void testDeleteProduct() {
        ProductMadeOf productMadeOf = new ProductMadeOf();
        productMadeOf.setName("Dining Table");
        ContainsArticles containsArticle = new ContainsArticles();
        containsArticle.setArtId(1);
        containsArticle.setAmountOf(4);
        List<ContainsArticles> containsArticles = List.of(containsArticle);
        productMadeOf.setContainsArticles(containsArticles);

        Products products = new Products();
        products.setId(1);
        products.setName("Dining Table");

        when(productsRepository.findById(1)).thenReturn(Optional.of(products));
        ResponseEntity<String> response = restTemplate.exchange("/products/1", HttpMethod.DELETE, new HttpEntity<>(null), String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        when(productsRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<String> responseWithStatus404 = restTemplate.exchange("/products/1", HttpMethod.DELETE, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.NOT_FOUND, responseWithStatus404.getStatusCode());

        verify(productsRepository, times(2)).findById(1);
        verify(productsRepository, times(1)).deleteById(1);
        verify(productsWithArticlesRepository, times(1)).deleteAllByProductId(1);
    }


    @Test
    public void testSellProduct() {
        ProductsWithArticles productsWithArticles = new ProductsWithArticles();
        productsWithArticles.setArticleId(1);
        productsWithArticles.setProductId(1);
        productsWithArticles.setAmountOf(4);

        Products products = new Products();
        products.setId(1);
        products.setName("Dining Table");

        Articles article = new Articles();
        article.setId(1);
        article.setName("leg");
        article.setStock(12);

        when(productsWithArticlesRepository.findByProductId(1)).thenReturn(Collections.singletonList(productsWithArticles));
        when(articlesRepository.findById(1)).thenReturn(Optional.of(article));
        ResponseEntity<String> response = restTemplate.exchange("/products/1", HttpMethod.POST, new HttpEntity<>(null), String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        article.setStock(3);
        ResponseEntity<String> responseWithStatus400 = restTemplate.exchange("/products/1", HttpMethod.POST, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseWithStatus400.getStatusCode());

        when(articlesRepository.findById(1)).thenReturn(Optional.empty());
        responseWithStatus400 = restTemplate.exchange("/products/1", HttpMethod.POST, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseWithStatus400.getStatusCode());

        verify(productsWithArticlesRepository, times(3)).findByProductId(1);
        verify(articlesRepository, times(3)).findById(1);
        verify(articlesRepository, times(1)).saveAll(any());
    }

}