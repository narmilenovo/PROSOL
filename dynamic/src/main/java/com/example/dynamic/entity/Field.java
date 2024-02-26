package com.example.dynamic.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Field extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fieldName;
	private String dataType;
	private String identity;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "field_pattern", joinColumns = @JoinColumn(name = "field_id"))
	private List<String> pattern;

	private Integer min;
	private Integer max;
	private Integer minLength;
	private Integer maxLength;

	private Boolean required;
	private Boolean extraField;
	private Boolean readable;
	private Boolean writable;
	private Boolean showAsColumn;

	@OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
	private List<DropDown> dropDowns;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "field_enums", joinColumns = @JoinColumn(name = "field_id"))
	// @OrderColumn
	private List<String> enums;

	@ManyToOne
	private Form form;

}
