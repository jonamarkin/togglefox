package com.markin.togglefox.dataaccess.repository;

import com.markin.togglefox.dataaccess.entity.FeatureFlagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureFlagJpaRepository extends JpaRepository<FeatureFlagEntity, String> {

    /**
     * Find by name and environment
     */
    Optional<FeatureFlagEntity> findByNameAndEnvironment(String name, String environment);

    /**
     * Find all flags in an environment
     */
    List<FeatureFlagEntity> findByEnvironmentOrderByNameAsc(String environment);

    /**
     * Check if flag exists by name and environment
     */
    boolean existsByNameAndEnvironment(String name, String environment);

    /**
     * Find flags with strategy eagerly loaded
     */
    @Query("SELECT f FROM FeatureFlagEntity f LEFT JOIN FETCH f.strategy WHERE f.id = :id")
    Optional<FeatureFlagEntity> findByIdWithStrategy(@Param("id") String id);

    /**
     * Find by name and environment with strategy eagerly loaded
     */
    @Query("SELECT f FROM FeatureFlagEntity f LEFT JOIN FETCH f.strategy WHERE f.name = :name AND f.environment = :environment")
    Optional<FeatureFlagEntity> findByNameAndEnvironmentWithStrategy(@Param("name") String name, @Param("environment") String environment);
}
