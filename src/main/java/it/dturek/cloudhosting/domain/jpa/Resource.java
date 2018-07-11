package it.dturek.cloudhosting.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Resource {

    public static final int ROOT_ID = 0;

    public enum Type {
        DIRECTORY, FILE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnore
    private Resource parent;

    @Column(nullable = false)
    private Timestamp modificationTime;

    @Column(nullable = false)
    private long size;

    @JsonIgnore
    private String path;

    @JsonIgnore
    private String contentType;

    @Transient
    private String friendlySize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Resource getParent() {
        return parent;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    public Timestamp getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Timestamp modificationTime) {
        this.modificationTime = modificationTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFriendlySize() {
        return friendlySize;
    }

    public void setFriendlySize(String friendlySize) {
        this.friendlySize = friendlySize;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", type=" + type +
                ", user=" + (user != null ? user.getId() : "null") +
                ", name='" + name + '\'' +
                ", parent=" + (parent != null ? parent.getId() : "null") +
                ", modificationTime=" + modificationTime +
                ", size=" + size +
                ", path=" + path +
                '}';
    }
}
