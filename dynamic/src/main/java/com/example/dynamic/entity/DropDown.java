package com.example.dynamic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropDown {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    // @ManyToOne(cascade = CascadeType.PERSIST)
    @ManyToOne
    // @JoinColumn(name = "field_id")
    // @JsonIgnore // Add this annotation to break the infinite loop
    private Field field;
}
