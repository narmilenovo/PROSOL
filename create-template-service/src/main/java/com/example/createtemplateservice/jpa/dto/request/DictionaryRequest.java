package com.example.createtemplateservice.jpa.dto.request;

import java.util.List;

import com.example.createtemplateservice.jpa.entity.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryRequest {

	@Schema(description = "Noun", example = "Bed")
	@NotBlank(message = "Noun is mandatory")
	private String noun;

	@Schema(description = "Noun synonyms")
	private String nounSynonyms;

	@Schema(description = "Noun Modifier Abbreviation")
	private String nmAbbreviation;

	@Schema(description = "Modifier", example = "a")
	@NotBlank(message = "Modifier is mandatory")
	private String modifier;

	@Schema(description = "Modifier synonyms")
	private String modifierSynonyms;

	@Schema(description = "Noun Modifier Definition", example = "A place where people sleep")
	private String nmDefinition;

	@Schema(description = "Noun Modifier Type")
	private Type type;

	@Schema(description = "Similar Search Items")
	private String similarSearchItems;

	@Schema(description = "Select Noun Modifier UOMs", allowableValues = "range[1,infinity]")
	private List<Long> nmUoms;

	@Schema(description = "Dictionary Attributes")
	private List<DictionaryAttributeRequest> attributes;

	@Schema(description = "Image")
	private String image;
}
