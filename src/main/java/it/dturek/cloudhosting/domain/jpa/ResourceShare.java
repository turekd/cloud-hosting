package it.dturek.cloudhosting.domain.jpa;

import it.dturek.cloudhosting.domain.ResourceShareType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class ResourceShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "share_key")
    private String key;

    @ManyToOne
    private Resource resource;

    @Column(nullable = false, name = "share_type")
    @Enumerated(EnumType.STRING)
    private ResourceShareType type;

    private Timestamp createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ResourceShareType getType() {
        return type;
    }

    public void setType(ResourceShareType type) {
        this.type = type;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ResourceShare{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", resource=" + resource +
                ", type=" + type +
                ", createdAt=" + createdAt +
                '}';
    }
}
