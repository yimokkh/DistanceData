package org.example.distancedata.repository;

import org.example.distancedata.entity.Continent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContinentRepository extends JpaRepository<Continent, Long> {
    Optional<Continent> getByName(@Param("name") String name);

    Optional<Continent> getContinentById(@Param("id") Long id);

    @Query(value = "SELECT continent.id, continent.name FROM continent "
            + "LEFT JOIN language_continent ON continent.id = language_continent.id_continent "
            + "WHERE language_continent.id_language = ?1", nativeQuery = true)
    List<Continent> findAllContinentWithLanguage(@Param("1") Long id);
}