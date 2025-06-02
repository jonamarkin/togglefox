package com.markin.togglefox.dataaccess.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "strategies")
public class StrategyEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_flag_id", nullable = false)
    private FeatureFlagEntity featureFlag;

    @Column(name = "strategy_type", nullable = false, length = 50)
    private String strategyType;

    @Column(name = "configuration", columnDefinition = "TEXT")
    private String configuration; // JSON string

    // Default constructor for JPA
    protected StrategyEntity() {}

    public StrategyEntity(String id, String strategyType, String configuration) {
        this.id = id;
        this.strategyType = strategyType;
        this.configuration = configuration;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public FeatureFlagEntity getFeatureFlag() { return featureFlag; }
    public void setFeatureFlag(FeatureFlagEntity featureFlag) { this.featureFlag = featureFlag; }

    public String getStrategyType() { return strategyType; }
    public void setStrategyType(String strategyType) { this.strategyType = strategyType; }

    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StrategyEntity that = (StrategyEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StrategyEntity{" +
                "id='" + id + '\'' +
                ", strategyType='" + strategyType + '\'' +
                '}';
    }
}