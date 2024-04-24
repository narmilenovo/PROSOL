package com.example.generalsettings.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubSubGroup extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String subSubGroupCode;
	private String subSubGroupName;
	private Boolean subSubGroupStatus;

	@ManyToOne
	private MainGroupCodes mainGroupCodes;

	@ManyToOne
	private SubGroupCodes subGroupCodes;
}
