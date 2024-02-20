package com.example.attributemaster.entity;

import com.example.attributemaster.configuration.ObjectToJsonConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuditFields {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fieldName;
	@Convert(converter = ObjectToJsonConverter.class)
	private Object oldValue;
	@Convert(converter = ObjectToJsonConverter.class)
	private Object newValue;
}
