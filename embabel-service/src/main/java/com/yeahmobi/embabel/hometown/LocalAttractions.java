package com.yeahmobi.embabel.hometown;

import com.fasterxml.jackson.annotation.JsonClassDescription;

import java.util.List;

@JsonClassDescription("当地热门景点")
public record LocalAttractions(List<Item> items) {
    public record Item(String name, String description) {}
}
