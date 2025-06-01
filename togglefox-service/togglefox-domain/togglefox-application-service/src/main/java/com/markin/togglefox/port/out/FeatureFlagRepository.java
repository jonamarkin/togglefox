package com.markin.togglefox.port.out;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.model.FeatureFlagId;

import java.util.List;
import java.util.Optional;

public interface FeatureFlagRepository {

    FeatureFlag save (FeatureFlag featureFlag);
    Optional<FeatureFlag> findByName(String name);
    Optional<FeatureFlag> findByNameAndEnvironment(String name, Environment environment);
    Optional<FeatureFlag> findById(FeatureFlagId id);
    List<FeatureFlag> findAllByEnvironment(Environment environment);
    void deleteById(FeatureFlagId id);
    boolean existsById(FeatureFlagId id);
    boolean existsByNameAndEnvironment(String name, Environment environment);

}
