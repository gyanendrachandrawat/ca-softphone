package com.consultadd.model;

import com.consultadd.model.audit.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contact")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contact extends Auditable {

    @Id private Long id;

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String jobTitle;
    private String company;

    @OneToMany private Set<ContactNumber> contactNumbers;
}
