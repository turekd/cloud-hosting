package it.dturek.cloudhosting.domain.jpa;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class MailQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp createdAt;

    private Timestamp processedAt;

    @Column(length = 65000)
    private String body;

    private String recipent;

    private String subject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Timestamp processedAt) {
        this.processedAt = processedAt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecipent() {
        return recipent;
    }

    public void setRecipent(String recipent) {
        this.recipent = recipent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "MailQueue{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", processedAt=" + processedAt +
                ", recipent='" + recipent + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
