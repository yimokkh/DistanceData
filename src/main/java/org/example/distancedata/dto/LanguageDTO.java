package org.example.distancedata.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Data
public class LanguageDTO {
    private String name;
    private Long id;
    private List<String> countries;

    public LanguageDTO() {
        this.countries = new ArrayList<>();
    }
}