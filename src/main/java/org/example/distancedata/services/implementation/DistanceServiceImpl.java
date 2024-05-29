package org.example.distancedata.services.implementation;

import org.example.distancedata.entity.Country;
import org.example.distancedata.services.DistanceService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DistanceServiceImpl implements DistanceService {
    @Override
    public double getDistanceInKilometres(final Country first, final Country second) {
        if (first == null || second == null) {
            return -1.0;
        }
        if (first.getName().equalsIgnoreCase(second.getName())) {
            return 0.0;
        }
        double delta = first.getLongitude() - second.getLongitude();
        double radianValue = Math.sin(Math.toRadians(first.getLatitude()))
                * Math.sin(Math.toRadians(second.getLatitude()))
                + Math.cos(Math.toRadians(first.getLatitude()))
                * Math.cos(Math.toRadians(second.getLatitude()))
                * Math.cos(Math.toRadians(delta));
        double degreeValue = Math.toDegrees(Math.acos(radianValue));
        double distance = degreeValue * 60 * 1.1515 * 1.6093;
        return BigDecimal.valueOf(distance).setScale(4,
                RoundingMode.CEILING).doubleValue();
    }
}