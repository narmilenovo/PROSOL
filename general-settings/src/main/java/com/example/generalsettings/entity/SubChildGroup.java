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
public class SubChildGroup {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long subId;
	    private String code;
	    private String title;
	    private Boolean status;
	    
	    @ManyToOne
	    @JoinColumn(name = "mainGroupCodes_id")
	    private MainGroupCodes mainGroupCodesId;
	    
	    @ManyToOne
	    @JoinColumn(name = "subMainGroup_id")
	    private SubMainGroup subMainGroupId;
}
