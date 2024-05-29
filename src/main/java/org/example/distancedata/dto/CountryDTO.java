package org.example.distancedata.dto;

import lombok.Data;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Data
public class CountryDTO {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer idCountry;
}