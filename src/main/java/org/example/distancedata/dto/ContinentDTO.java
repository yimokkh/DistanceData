package org.example.distancedata.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Data
public class ContinentDTO {
    private Long id;
    private String name;
    private List<String> languages;

    public ContinentDTO() {
        this.languages = new ArrayList<>();
    }
}