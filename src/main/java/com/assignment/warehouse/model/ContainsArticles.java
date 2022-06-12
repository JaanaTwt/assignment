package com.assignment.warehouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContainsArticles {
    private Integer artId;
    private Integer amountOf;

    @JsonProperty("art_id")
    public Integer getArtId() {
        return artId;
    }

    @JsonProperty("amount_of")
    public Integer getAmountOf() {
        return amountOf;
    }
}
