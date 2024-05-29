package org.example.distancedata.services.implementation;

import lombok.AllArgsConstructor;
import org.example.distancedata.cache.LRUCache;
import org.example.distancedata.dto.CountryDTO;
import org.example.distancedata.entity.Country;
import org.example.distancedata.entity.Continent;
import org.example.distancedata.exception.BadRequestException;
import org.example.distancedata.exception.ResourceNotFoundException;
import org.example.distancedata.repository.CountryRepository;
import org.example.distancedata.services.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements DataService<Country> {
    private final CountryRepository repository;
    private final LRUCache<Long, Country> cache;
    private static final String DONT_EXIST = " doesn't exist";

    private long findFreeId() {
        var list = read();
        long i = 1;
        for (Country countryInfo : list) {
            if (countryInfo.getId() != i) {
                return i;
            }
            i++;
        }
        return i + 1;
    }

    public void createWithContinent(final CountryDTO country, final Continent continent)
            throws BadRequestException {
        try {
            getByName(country.getName());
            throw new BadRequestException("Can't create because already exist");
        } catch (ResourceNotFoundException e) {
            var newCountry = Country.builder().name(country.getName())
                    .latitude(country.getLatitude()).longitude(country.getLongitude())
                    .continent(continent).id(findFreeId()).build();
            repository.save(newCountry);
            cache.put(newCountry.getId(), newCountry);
        }
    }

    @Override
    public void create(final Country country) throws BadRequestException {
        try {
            getByID(country.getId());
            throw new BadRequestException("Can't create country with this id"
                    + country.getId() + " already exist");
        } catch (ResourceNotFoundException e) {
            repository.save(country);
            cache.put(country.getId(), country);

        }
    }

    public void updateWithContinent(final CountryDTO country, final  Continent continent)
            throws ResourceNotFoundException {
        try {
            getByID(country.getId());
            cache.remove(country.getId());
            var newCountry = Country.builder().name(country.getName())
                    .latitude(country.getLatitude()).longitude(country.getLongitude())
                    .continent(continent).build();
            repository.save(newCountry);
            cache.put(newCountry.getId(), newCountry);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Can't update country with this id"
                    + country.getId() + DONT_EXIST);
        }
    }

    @Override
    public List<Country> read() {
        return repository.findAll(Sort.by("id"));
    }

    @Override
    public Country getByName(final String name) throws ResourceNotFoundException {
        var optionalContinent = repository.getCountryByName(name);
        if (optionalContinent.isPresent()) {
            cache.put(optionalContinent.get().getId(), optionalContinent.get());
        } else {
            throw new ResourceNotFoundException(
                    "Can't find country because with this name doesn't exist");
        }
        return optionalContinent.get();
    }

    @Override
    public Country getByID(final Long id) throws ResourceNotFoundException {
        var optionalCountry = cache.get(id);
        if (optionalCountry.isEmpty()) {
            optionalCountry = repository.getCountryById(id);
            if (optionalCountry.isPresent()) {
                cache.put(id, optionalCountry.get());
            } else {
                throw new ResourceNotFoundException(
                        "Cannot find country with id = " + id + DONT_EXIST);
            }
        }
        return optionalCountry.get();
    }


    @Override
    public void update(final Country country) throws ResourceNotFoundException {
        if (repository.getCountryById(country.getId()).isPresent()) {
            cache.remove(country.getId());
            repository.save(country);
            cache.put(country.getId(), country);
        } else {
            throw new ResourceNotFoundException("Can't find country with id: "
                    + country.getId() + DONT_EXIST);
        }
    }
    public void update(CountryDTO country) throws ResourceNotFoundException {

        var oldCountry = cache.get(country.getId());
        if (oldCountry.isEmpty()) {
            oldCountry = repository.getCountryById(country.getId());
            if (oldCountry.isEmpty()) {
                throw new ResourceNotFoundException("Can't find country with id = "
                        + country.getId() + DONT_EXIST);
            }
        }
        cache.remove(country.getId());
        oldCountry.get().setName(country.getName());
        oldCountry.get().setLatitude(country.getLatitude());
        oldCountry.get().setLongitude(country.getLongitude());
        repository.save(oldCountry.get());
        cache.put(country.getId(), oldCountry.get());
    }

    @Override
    public void delete(final Long id)
            throws ResourceNotFoundException {
        if (repository.getCountryById(id).isPresent()) {
            repository.deleteById(id);
            cache.remove(id);
        } else {
            throw new ResourceNotFoundException("Can't delete country with id = "
                    + id + DONT_EXIST);
        }
    }

    public List<Country> getBetweenLatitudes(final Double first, final Double second) {
        return repository.findAllCountryWithLatitudeBetween(first, second);
    }
}