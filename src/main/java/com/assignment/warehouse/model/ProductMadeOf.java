package com.assignment.warehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductMadeOf {
    private Integer id;
    private String name;
    private boolean available;
    private Integer stock;
    private List<ContainsArticles> containsArticles;

    @JsonProperty("contain_articles")
    public List<ContainsArticles> getContainsArticles() {
        return containsArticles;
    }

    @JsonProperty("contain_articles")
    public void setContainsArticles(List<ContainsArticles> containsArticles) {
        this.containsArticles = containsArticles;
    }

    public void  addContainsArticles(ContainsArticles containsArticles){
        if(this.containsArticles == null){
            this.containsArticles = new ArrayList<>();
            this.containsArticles.add(containsArticles);
        } else {
            this.containsArticles.add(containsArticles);
        }
    }
}
