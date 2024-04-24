package com.example.mrpdataservice.entity;

import java.util.Map;

import com.example.mrpdataservice.configuration.ObjectToJsonConverter;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AvailCheck extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String availCheckCode;
	private String availCheckName;
	private Boolean availCheckStatus;

	@ElementCollection
	@CollectionTable(name = "avail_check_fields", joinColumns = @JoinColumn(name = "avail_check_id"))
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@Convert(converter = ObjectToJsonConverter.class)
	private Map<String, Object> dynamicFields;
}
