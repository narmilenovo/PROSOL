package com.example.generalsettings.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubMainGroup {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String subMainGroupCode;
	    private String subMainGroupTitle;
	    private Boolean status;

	    @ManyToOne
	    @JoinColumn(name = "MainGroupCodes_id")
	    private MainGroupCodes mainGroupCodesId;
}
