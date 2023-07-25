package com.consultadd.model;

import com.consultadd.model.audit.Auditable;
import com.consultadd.model.enums.JobType;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends Auditable {
    @Id private Long id;

    private String firstName;
    private String lastName;
    private String email;

    @Column(unique = true)
    private String twilioNumber;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private JobType jobType;

    private String company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany private Set<Contact> contacts;

    public UUID getClientId() {
        return UUID.nameUUIDFromBytes(
                (this.id + this.twilioNumber).getBytes(StandardCharsets.UTF_8));
    }
}
