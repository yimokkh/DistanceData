package org.example.distancedata.services.implementation;

import lombok.AllArgsConstructor;
import org.example.distancedata.cache.LRUCache;
import org.example.distancedata.dto.ContinentDTO;
import org.example.distancedata.entity.Continent;
import org.example.distancedata.entity.Language;
import org.example.distancedata.exception.BadRequestException;
import org.example.distancedata.exception.ResourceNotFoundException;
import org.example.distancedata.repository.ContinentRepository;
import org.example.distancedata.repository.LanguageRepository;
import org.example.distancedata.services.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class ContinentServiceImpl implements DataService<Continent> {
    private final ContinentRepository continentRepository;
    private final LanguageRepository languageRepository;

    private final LRUCache<Long, Continent> cache;
    private static final String DONT_EXIST = " doesn't exist";

    private long findFreeID() {
        var list = read();
        long i = 1;
        for (Continent continent : list) {
            if (continent.getId() != i) {
                return i;
            }
            i++;
        }
        return i + 1;
    }

    @Override
    public void create(final Continent continent) throws BadRequestException {
        try {
            getByID(continent.getId());
            throw new BadRequestException("Can't create continent with this id = "
                    + continent.getId() + " already exist");
        } catch (ResourceNotFoundException e) {
            cache.put(continent.getId(), continent);
            continentRepository.save(continent);

        }
    }

    @Override
    public List<Continent> read() {
        return continentRepository.findAll(Sort.by("id"));
    }

    @Override
    public Continent getByName(final String name)
            throws ResourceNotFoundException {
        var optionalContinent = continentRepository.getByName(name);
        if (optionalContinent.isPresent()) {
            cache.put(optionalContinent.get().getId(), optionalContinent.get());
        } else {
            throw new ResourceNotFoundException(
                    "Can't find continent because with this name doesn't exist");
        }
        return optionalContinent.get();
    }


    @Override
    public Continent getByID(final Long id) throws ResourceNotFoundException {
        var optionalContinent = cache.get(id);
        if (optionalContinent.isEmpty()) {
            optionalContinent = continentRepository.getContinentById(id);
            if (optionalContinent.isPresent()) {
                cache.put(id, optionalContinent.get());
            } else {
                throw new ResourceNotFoundException(
                        "Can't create continent with id = "
                                + id + " already exist");
            }
        }
        return optionalContinent.get();
    }

    @Override
    public void update(final Continent continent)
            throws ResourceNotFoundException {
        if (continent == null) {
            throw new ResourceNotFoundException("Can't update continent");
        }
        if (continentRepository.getContinentById(continent.getId()).isPresent()) {
            cache.remove(continent.getId());
            continentRepository.save(continent);
        } else {
            throw new ResourceNotFoundException(
                    "Can't update continent with this id = ");
        }
    }

    @Override
    public void delete(final Long id)
            throws ResourceNotFoundException {
        if (getByID(id) != null) {
            continentRepository.deleteById(id);
            cache.remove(id);
        } else {
            throw new ResourceNotFoundException(
                    "Can't delete continent with id = " + id + DONT_EXIST);
        }
    }

    public void create(final ContinentDTO continentDto) throws BadRequestException {
        var listLanguage = new HashSet<Language>();
        for (String ptrLanguage : continentDto.getLanguages()) {
            var language = languageRepository.getByName(ptrLanguage);
            language.ifPresent(listLanguage::add);
        }
        var newContinent = Continent.builder().name(continentDto.getName())
                .languages(new HashSet<>()).id(findFreeID()).build();
        for (Language language : listLanguage) {
            newContinent.addLanguage(language);
        }
        create(newContinent);
    }

    public void updateWithExist(final ContinentDTO continent)
            throws ResourceNotFoundException {
        if (continentRepository.getContinentById(continent.getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Continent with this id doesn't exist");
        }
        var newLanguages = new HashSet<Language>();
        for (String language : continent.getLanguages()) {
            var languageTemp = languageRepository.getByName(language);
            languageTemp.ifPresent(newLanguages::add);
        }
        var updatedContinent = Continent.builder().name(continent.getName())
                .languages(new HashSet<>()).id(continent.getId()).build();
        for (Language language : newLanguages) {
            updatedContinent.addLanguage(language);
        }
        update(updatedContinent);
    }

    public void modifyLanguage(final ContinentDTO continentDto, final boolean deleteFlag)
            throws ResourceNotFoundException {
        var continent = getByID(continentDto.getId());
        if (continent != null) {
            for (String language : continentDto.getLanguages()) {
                var tempLanguage = languageRepository.getByName(language);
                tempLanguage.ifPresent(!deleteFlag
                        ? continent::addLanguage : continent::removeLanguage);
            }
        }
        assert continent != null;
        update(continent);
    }

    public List<Continent> getByLanguage(final Long id)
            throws ResourceNotFoundException {
        var optionalLanguage = languageRepository.getLanguageById(id);
        if (optionalLanguage.isEmpty()) {
            throw new ResourceNotFoundException("No language with this name");
        }
        return continentRepository.findAllContinentWithLanguage(id);
    }
}