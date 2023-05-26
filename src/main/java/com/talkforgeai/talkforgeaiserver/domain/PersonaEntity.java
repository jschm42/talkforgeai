package com.talkforgeai.talkforgeaiserver.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "PERSONA")
public class PersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 32)
    private String name;

    @Column(length = 256)
    private String description;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String system;

    @Column(length = 128)
    private String imagePath;

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

}
