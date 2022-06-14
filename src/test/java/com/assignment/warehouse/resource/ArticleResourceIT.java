package com.assignment.warehouse.resource;

import com.assignment.warehouse.WarehouseApplication;
import com.assignment.warehouse.model.entity.Articles;
import com.assignment.warehouse.repository.ArticlesRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = WarehouseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArticleResourceIT {

    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ArticlesRepository articlesRepository;

    @Test
    public void testLoadingInventoryFile() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/articles/load?filePath=src/test/resources/inventory.json", String.class);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        ResponseEntity<String> responseWithStatus400 = restTemplate.getForEntity("/articles/load?filePath=src/test/resources/wrongInventory.json", String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseWithStatus400.getStatusCode());
    }

    @Test
    public void testGetArticles() throws JSONException, JsonProcessingException {
        Articles article1 = new Articles();
        article1.setId(1);
        article1.setName("leg");
        article1.setStock(12);
        Articles article2 = new Articles();
        article2.setId(1);
        article2.setName("leg");
        article2.setStock(12);
        List<Articles> articles = List.of(article1,article2);

        when(articlesRepository.findAll()).thenReturn(articles);
        String expected = mapper.writeValueAsString(articles);
        ResponseEntity<String> response = restTemplate.getForEntity("/articles", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        JSONAssert.assertEquals(expected, response.getBody(), true);

        verify(articlesRepository, times(1)).findAll();
    }

    @Test
    public void testGetArticlesById() throws JSONException, JsonProcessingException {
        Articles article = new Articles();
        article.setId(1);
        article.setName("leg");
        article.setStock(12);

        when(articlesRepository.findById(1)).thenReturn(Optional.of(article));
        String expected = mapper.writeValueAsString(article);
        ResponseEntity<String> response = restTemplate.getForEntity("/articles/1", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        JSONAssert.assertEquals(expected, response.getBody(), true);

        verify(articlesRepository, times(1)).findById(1);

        ResponseEntity<String> responseWithStatusNotFound = restTemplate.getForEntity("/articles/2", String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseWithStatusNotFound.getStatusCode());
        verify(articlesRepository, times(1)).findById(2);
    }

    @Test
    public void testCreateArticle() {
        Articles article = new Articles();
        article.setId(1);
        article.setName("leg");
        article.setStock(12);

        ResponseEntity<String> response = restTemplate.postForEntity("/articles", article, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        when(articlesRepository.save(article)).thenThrow(RuntimeException.class);

        ResponseEntity<String> exceptionResponse = restTemplate.postForEntity("/articles", article, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatusCode());
        verify(articlesRepository, times(2)).save(article);
    }

    @Test
    public void testUpdateArticle() {
        Articles article = new Articles();
        article.setId(1);
        article.setName("leg");
        article.setStock(12);

        ResponseEntity<String> wrongResponse = restTemplate.exchange("/articles/2", HttpMethod.PUT, new HttpEntity<>(article), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, wrongResponse.getStatusCode());

        when(articlesRepository.findById(1)).thenReturn(Optional.of(article));
        ResponseEntity<String> response = restTemplate.exchange("/articles/1", HttpMethod.PUT, new HttpEntity<>(article), String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(articlesRepository, times(1)).save(article);
    }
}
