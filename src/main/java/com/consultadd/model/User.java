package com.consultadd.model;

import com.consultadd.model.audit.Auditable;
import com.consultadd.model.enums.JobType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
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
    private UUID clientId;

    @Column(unique = true, length = 12)
    private String twilioNumber;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private JobType jobType;

    private String company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany private Set<Contact> contacts;
}
