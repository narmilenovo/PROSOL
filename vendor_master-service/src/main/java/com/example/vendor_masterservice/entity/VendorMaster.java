package com.example.vendor_masterservice.entity;

import java.util.Map;

import com.example.vendor_masterservice.configuration.ObjectToJsonConverter;

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
public class VendorMaster extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String shortDescName;
	private String name;
	private String name2;
	private String name3;
	private String name4;
	private String address;
	private String address2;
	private String address3;
	private String address4;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String telephoneNo;
	private String fax;
	private String mobileNo;
	private String email;
	private String website;
	private String acquiredBy;
	private Boolean status;

	@ElementCollection
	@CollectionTable(name = "vendor_fields", joinColumns = @JoinColumn(name = "vendor_id"))
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@Convert(converter = ObjectToJsonConverter.class)
	private Map<String, Object> dynamicFields;
}
