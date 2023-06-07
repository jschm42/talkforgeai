package com.talkforgeai.talkforgeaiserver.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "PERSONA")
public class PersonaEntity {
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

    @Column(length = 128)
    private String imagePath;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "persona_property_mapping",
            joinColumns = {@JoinColumn(name = "persona_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "property_id", referencedColumnName = "id")})
    @MapKey(name = "propertyKey")
    private Map<String, PropertyEntity> properties = new HashMap<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", nullable = false)
    private Date createdOn;


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

    public Map<String, PropertyEntity> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, PropertyEntity> properties) {
        this.properties = properties;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
