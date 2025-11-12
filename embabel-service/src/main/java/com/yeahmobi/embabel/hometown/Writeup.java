/*
 * Copyright 2024-2025 Embabel Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yeahmobi.embabel.hometown;

import com.embabel.agent.domain.library.HasContent;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("幽默旅游推荐的结构化输出，包括标题、小吃、景点、贴士和幽默小知识。")
public record Writeup(

        @JsonPropertyDescription("整体标题，例如“舌尖上的长沙：一场火辣与浪漫的旅程”")
        String title,

        @JsonPropertyDescription("当地特色小吃列表")
        List<Item> foods,

        @JsonPropertyDescription("当地热门景点列表")
        List<Item> attractions,

        @JsonPropertyDescription("旅行实用贴士")
        Tips tips,

        @JsonPropertyDescription("一句幽默的冷知识或本地人小贴士")
        String funFact

) implements HasContent {

    @JsonCreator
    public Writeup(
            @JsonProperty("title") String title,
            @JsonProperty("foods") List<Item> foods,
            @JsonProperty("attractions") List<Item> attractions,
            @JsonProperty("tips") Tips tips,
            @JsonProperty("funFact") String funFact
    ) {
        this.title = title;
        this.foods = foods;
        this.attractions = attractions;
        this.tips = tips;
        this.funFact = funFact;
    }

    @Override
    public String getContent() {
        return title;
    }

    @JsonClassDescription("出行贴士，包括最佳出游时间、交通与拍照建议")
    public record Tips(
            @JsonPropertyDescription("最佳出游时间建议")
            String bestTime,
            @JsonPropertyDescription("交通建议")
            String transport,
            @JsonPropertyDescription("拍照建议")
            String photo
    ) {}

    @JsonClassDescription("条目，包括名称与幽默描述")
    public record Item(
            @JsonPropertyDescription("名称，例如小吃名或景点名")
            String name,
            @JsonPropertyDescription("幽默、有趣的描述")
            String description
    ) {}
}
