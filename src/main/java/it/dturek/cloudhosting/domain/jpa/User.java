package it.dturek.cloudhosting.domain.jpa;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column
    private Timestamp lastLoggedAt;

    @Column(nullable = false)
    private boolean accountExpired = false;

    @Column(nullable = false)
    private boolean locked = false;

    @Column(nullable = false)
    private boolean credentialsExpired = false;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(nullable = false)
    private long spaceUsed = 0;

    @Column(nullable = false)
    private long spaceAvailable;

    @Column(length = 32)
    private String registrationKey;

    private Timestamp registrationKeyValidTo;

    public String getRegistrationKey() {
        return registrationKey;
    }

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
    }

    public Timestamp getRegistrationKeyValidTo() {
        return registrationKeyValidTo;
    }

    public void setRegistrationKeyValidTo(Timestamp registrationKeyValidTo) {
        this.registrationKeyValidTo = registrationKeyValidTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLoggedAt() {
        return lastLoggedAt;
    }

    public void setLastLoggedAt(Timestamp lastLoggedAt) {
        this.lastLoggedAt = lastLoggedAt;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isCredentialsExpired() {
        return credentialsExpired;
    }

    public void setCredentialsExpired(boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getSpaceUsed() {
        return spaceUsed;
    }

    public void setSpaceUsed(long spaceUsed) {
        this.spaceUsed = spaceUsed;
    }

    public long getSpaceAvailable() {
        return spaceAvailable;
    }

    public void setSpaceAvailable(long spaceAvailable) {
        this.spaceAvailable = spaceAvailable;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", lastLoggedAt=" + lastLoggedAt +
                ", accountExpired=" + accountExpired +
                ", locked=" + locked +
                ", credentialsExpired=" + credentialsExpired +
                ", enabled=" + enabled +
                ", spaceUsed=" + spaceUsed +
                ", spaceAvailable=" + spaceAvailable +
                '}';
    }
}
