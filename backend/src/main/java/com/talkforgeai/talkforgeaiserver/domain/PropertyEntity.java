package com.talkforgeai.talkforgeaiserver.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "PROPERTY")
public class PropertyEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PropertyType type;

    //@NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PropertyCategory category;

    @Column(name = "property_key", nullable = false)
    private String propertyKey;

    @Column(name = "property_value")
    private String properyValue;

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public PropertyCategory getCategory() {
        return category;
    }

    public void setCategory(PropertyCategory category) {
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getProperyValue() {
        return properyValue;
    }

    public void setProperyValue(String properyValue) {
        this.properyValue = properyValue;
    }
}
