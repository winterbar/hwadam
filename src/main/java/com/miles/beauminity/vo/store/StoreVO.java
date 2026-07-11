package com.miles.beauminity.vo.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreVO {
    private Long productId;
    private String title;
    private int lprice;
    private String image;
    private String brand;
    private String category1;
    private String category2;
   }
