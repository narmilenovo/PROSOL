package com.example.user_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "roles")
@Table(name = "privileges")
@EntityListeners(AuditingEntityListener.class)
public class Privilege extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    //    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean status;

    //    @JsonIgnore
    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles;
}
