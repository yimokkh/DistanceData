package org.example.distancedata.controllers;


import lombok.AllArgsConstructor;
import org.example.distancedata.aspect.AspectAnnotation;
import org.example.distancedata.dto.CountryDTO;
import org.example.distancedata.entity.Country;
import org.example.distancedata.exception.BadRequestException;
import org.example.distancedata.exception.ResourceNotFoundException;
import org.example.distancedata.services.DistanceService;
import org.example.distancedata.services.implementation.CountryServiceImpl;
import org.example.distancedata.services.implementation.ContinentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/api/countries")
@AllArgsConstructor
public class CountryController {
    private final CountryServiceImpl dataService;
    private final DistanceService distanceService;
    private final ContinentServiceImpl continentService;

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<Country>> getAllCountry() {
        return new ResponseEntity<>(dataService.read(), HttpStatus.OK);
    }

    @AspectAnnotation
    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity<Country> getCountryInfo(
            final @RequestParam(name = "country") String countryName)
            throws ResourceNotFoundException {
        var countryInfo = dataService.getByName(countryName);
        return new ResponseEntity<>(countryInfo, HttpStatus.OK);
    }

    @AspectAnnotation
    @GetMapping(value = "/find", produces = "application/json")
    public ResponseEntity<Country> getCountryInfoById(
            final @RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        var countryInfo = dataService.getByID(id);
        return new ResponseEntity<>(countryInfo, HttpStatus.OK);
    }

    @AspectAnnotation
    @GetMapping(value = "/distance/{firstCountry}+{secondCountry}",
            produces = "application/json")
    public ResponseEntity<HashMap<String, String>> getDistance(
            final @PathVariable(name = "firstCountry") String firstCountry,
            final @PathVariable(name = "secondCountry") String secondCountry)
            throws ResourceNotFoundException {
        var firstCountryInfo = dataService.getByName(firstCountry);
        var secondCountryInfo = dataService.getByName(secondCountry);
        double distance = distanceService.getDistanceInKilometres(firstCountryInfo, secondCountryInfo);
        if (distance != -1) {
            var objects = new HashMap<String, String>();
            objects.put("First country info", firstCountryInfo.toString());
            objects.put("Second country info", secondCountryInfo.toString());
            objects.put("Distance", Double.toString(distance));
            return new ResponseEntity<>(objects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @AspectAnnotation
    @PutMapping("/update")
    public HttpStatus update(final @RequestBody CountryDTO country)
            throws ResourceNotFoundException {
        dataService.update(country);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @PutMapping("/update/{continentName}")
    public HttpStatus update(final @RequestBody CountryDTO country, final @PathVariable(name = "continentName") String continentName)
            throws ResourceNotFoundException {
        var continent = continentService.getByName(continentName);
        dataService.updateWithContinent(country, continent);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @PostMapping("/create/{continentName}")
    public HttpStatus create(final @RequestBody CountryDTO country, final @PathVariable(name = "continentName") String continentName)
            throws ResourceNotFoundException, BadRequestException {
        var continent = continentService.getByName(continentName);
        dataService.createWithContinent(country, continent);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @DeleteMapping("/delete")
    public HttpStatus delete(final @RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        dataService.delete(id);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @GetMapping("/get_between_latitude")
    public ResponseEntity<List<Country>> getCountriesBetween(
            final @RequestParam(name = "first") Double first,
            final @RequestParam(name = "second") Double second) {
        if (first > second) {
            return new ResponseEntity<>(
                    dataService.getBetweenLatitudes(second, first),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                dataService.getBetweenLatitudes(first, second), HttpStatus.OK);
    }
}