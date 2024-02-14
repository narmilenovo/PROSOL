package com.example.dynamic.entity;

import java.util.Map;

import com.example.dynamic.configuration.ObjectToJsonConverter;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_id")
	private Form form;

	@ElementCollection
	@CollectionTable(name = "form_fields", joinColumns = @JoinColumn(name = "form_data_id"))
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@Convert(converter = ObjectToJsonConverter.class)
	private Map<String, Object> dynamicFields;
}
