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

import com.embabel.agent.domain.library.Person;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonClassDescription("有籍贯（家乡/出生地）的人")
@JsonDeserialize(as = LocalPerson.class)
public record LocalPerson(
        String name,
        @JsonPropertyDescription("籍贯") String hometown
) implements Person {

    @JsonCreator
    public LocalPerson(
            @JsonProperty("name") String name,
            @JsonProperty("hometown") String hometown
    ) {
        this.name = name;
        this.hometown = hometown;
    }

    @Override
    public String getName() {
        return name;
    }
}