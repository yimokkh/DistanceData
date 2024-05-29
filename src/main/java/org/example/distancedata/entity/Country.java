package org.example.distancedata.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "country")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    @Id
    @Column(name = "id")
    Long id;
    @Column(name = "name")
    String name;
    @Column(name = "latitude")
    Double latitude;
    @Column(name = "longitude")
    Double longitude;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "continent")
    @JsonBackReference
    private org.example.distancedata.entity.Continent continent;

    @Override
    public String toString() {
        return name + "\nlatitude: " + latitude + "\nlongitude: " + longitude + "\nINDEX: "
                + id + "\ncontinent " + continent.getName() + " id:" + continent.getId();

    }
}