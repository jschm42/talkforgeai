package com.talkforgeai.backend.persona.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

@Entity
@Table(name = "PERSONA")
public class PersonaEntity {
    @CreationTimestamp
    Date createdAt;

    @UpdateTimestamp
    Date modifiedAt;

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(unique = true, length = 32, nullable = false)
    private String name;

    @Column(length = 256, nullable = false)
    private String description;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String system;

    @Enumerated(EnumType.STRING)
    private List<RequestFunction> requestFunctions = new ArrayList<>();

    @Column(length = 128)
    private String imagePath;

    @ElementCollection
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    @CollectionTable(name = "persona_properties", joinColumns = @JoinColumn(name = "persona_id"))
    private Map<String, String> properties = new HashMap<>();

    public List<RequestFunction> getRequestFunctions() {
        return requestFunctions;
    }

    public void setRequestFunctions(List<RequestFunction> requestFunctions) {
        this.requestFunctions = requestFunctions;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
