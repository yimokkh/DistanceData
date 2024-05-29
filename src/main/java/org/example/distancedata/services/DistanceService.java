package org.example.distancedata.services;

import org.example.distancedata.entity.Country;

import org.springframework.stereotype.Service;

@Service
public interface DistanceService {
    double getDistanceInKilometres(Country to, Country from);
}