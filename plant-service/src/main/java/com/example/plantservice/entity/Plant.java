package com.example.plantservice.entity;

import java.util.HashMap;
import java.util.Map;

import com.example.plantservice.config.ObjectToJsonConverter;

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
public class Plant extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@NotNull
	private Long id;
//	@Min(value = 2)
//	@Max(value = 30)
	private String plantCode;
	private String plantName;
	private Boolean plantStatus;

	@ElementCollection
	@CollectionTable(name = "plant_dynamic_fields", joinColumns = @JoinColumn(name = "plant_id"))
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@Convert(converter = ObjectToJsonConverter.class)
	private Map<String, Object> dynamicFields = new HashMap<>();
}