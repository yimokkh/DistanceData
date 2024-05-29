package org.example.distancedata.services.implementation;

import lombok.AllArgsConstructor;
import org.example.distancedata.cache.LRUCache;
import org.example.distancedata.dto.LanguageDTO;
import org.example.distancedata.entity.Continent;
import org.example.distancedata.entity.Language;
import org.example.distancedata.exception.BadRequestException;
import org.example.distancedata.exception.ResourceNotFoundException;
import org.example.distancedata.repository.LanguageRepository;
import org.example.distancedata.services.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LanguageServiceImpl implements DataService<Language> {
    private final LanguageRepository repository;
    private final LRUCache<Long, Language> cache;
    private static final String DONT_EXIST = " doesn't exist";

    @Override
    public void create(final Language language) {
        try {
            getByID(language.getId());
            throw new BadRequestException("Can't create language with id = "
                    + language.getId() + " already exist");
        } catch (ResourceNotFoundException e) {
            repository.save(language);
            cache.put(language.getId(), language);
        }
    }


    @Override
    public List<Language> read() {
        return repository.findAll(Sort.by("id"));
    }

    @Override
    public Language getByName(final String name)
            throws ResourceNotFoundException {
        var optionalLanguage = repository.getByName(name);
        if (optionalLanguage.isPresent()) {
            cache.put(optionalLanguage.get().getId(), optionalLanguage.get());
        } else {
            throw new ResourceNotFoundException(
                    "Can't find language because with this name");
        }
        return optionalLanguage.get();
    }

    @Override
    public Language getByID(final Long id) throws ResourceNotFoundException {
        var optionalLanguage = cache.get(id);
        if (optionalLanguage.isEmpty()) {
            optionalLanguage = repository.findById(id);
            if (optionalLanguage.isPresent()) {
                cache.put(id, optionalLanguage.get());
            } else {
                throw new ResourceNotFoundException(
                        "Can't find language with this id = "
                                + id + " doesnt exist");
            }
        }
        return optionalLanguage.get();
    }

    @Override
    public void update(Language language) throws ResourceNotFoundException {
        try {
            getByID(language.getId());
            cache.remove(language.getId());
            repository.save(language);
            cache.put(language.getId(), language);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(
                    "Can't update language with this id = "
                            + language.getId() + DONT_EXIST);
        }
    }

    @Override
    public void delete(final Long id) throws ResourceNotFoundException {
        Language language = getByID(id);
        if (language != null) {
            List<Continent> existingContinents = language.getContinents();
            for (Continent continent : existingContinents) {
                continent.removeLanguage(language);
            }
            repository.delete(language);
        } else {
            throw new ResourceNotFoundException(
                    "Can't delete language with this id = "
                            + id + DONT_EXIST);
        }
    }

    public void update(final LanguageDTO language)
            throws ResourceNotFoundException {
        update(Language.builder().name(language.getName())
                .continents(new ArrayList<>()).id(language.getId()).build());
    }

    public void create(final LanguageDTO language) throws BadRequestException {
        create(Language.builder().name(language.getName())
                .id(language.getId()).continents(new ArrayList<>()).build());
    }

}