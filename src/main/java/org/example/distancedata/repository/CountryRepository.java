package org.example.distancedata.repository;

import org.example.distancedata.entity.Country;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> getCountryByName(@Param("name") String name);

    Optional<Country> getCountryById(@Param("id") Long id);

    @Query("SELECT country FROM Country country WHERE country.latitude > :first" + " AND country.latitude < :second ORDER BY country.latitude")
    List<Country> findAllCountryWithLatitudeBetween(@Param("first") Double first, @Param("second") Double second);
}