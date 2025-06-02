package com.markin.togglefox.dataaccess.repository;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.port.out.FeatureFlagRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FeatureFlagRepositoryImpl implements FeatureFlagRepository {

    private final FeatureFlagJpaRepository jpaRepository;
    private final FeatureFlagMapper mapper;

    public FeatureFlagRepositoryImpl(FeatureFlagJpaRepository jpaRepository, FeatureFlagMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public FeatureFlag save(FeatureFlag featureFlag) {
        var entity = mapper.toEntity(featureFlag);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<FeatureFlag> findById(FeatureFlagId id) {
        return jpaRepository.findByIdWithStrategy(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<FeatureFlag> findByNameAndEnvironment(String name, Environment environment) {
        return jpaRepository.findByNameAndEnvironmentWithStrategy(name, environment.getName())
                .map(mapper::toDomain);
    }

    @Override
    public List<FeatureFlag> findByEnvironment(Environment environment) {
        return jpaRepository.findByEnvironmentOrderByNameAsc(environment.getName())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(FeatureFlagId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(FeatureFlagId id) {
        return jpaRepository.existsById(id.getValue());
    }

    @Override
    public boolean existsByNameAndEnvironment(String name, Environment environment) {
        return jpaRepository.existsByNameAndEnvironment(name, environment.getName());
    }
}
