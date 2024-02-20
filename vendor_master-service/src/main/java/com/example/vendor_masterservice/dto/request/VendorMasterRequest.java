package com.example.vendor_masterservice.dto.request;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorMasterRequest {
	@Schema(description = "Short Description of the vendor name", example = "Codasol")
	private String shortDescName;

	@Schema(description = "Vendor Name", example = "Coda Technology Solutions Private Limited")
//    @Size(min = 2, max = 100, message = "Vendor Name must be between 2 and 100 characters long")
	private String name;

	@Schema(description = "Vendor Name2", example = "Coda Technology Solutions Private Limited")
//    @Size(min = 2, max = 100, message = "Vendor Name must be between 2 and 100 characters long")
	private String name2;

	@Schema(description = "Vendor Name3", example = "Coda Technology Solutions Private Limited")
//    @Size(min = 2, max = 100, message = "Vendor Name must be between 2 and 100 characters long")
	private String name3;

	@Schema(description = "Vendor Name4", example = "Coda Technology Solutions Private Limited")
//    @Size(min = 2, max = 100, message = "Vendor Name must be between 2 and 100 characters long")
	private String name4;

	@Schema(description = "Vendor Address", example = "Coda Technology Solutions Private Limited Address")
//    @Size(min = 2, max = 255, message = "Vendor Address must be between 2 and 255 characters long")
	private String address;

	@Schema(description = "Vendor Address2", example = "Coda Technology Solutions Private Limited Address")
//    @Size(min = 2, max = 255, message = "Vendor Address must be between 2 and 255 characters long")
	private String address2;

	@Schema(description = "Vendor Address3", example = "Coda Technology Solutions Private Limited Address")
//    @Size(min = 2, max = 255, message = "Vendor Address must be between 2 and 255 characters long")
	private String address3;

	@Schema(description = "Vendor Address4", example = "Coda Technology Solutions Private Limited Address")
//    @Size(min = 2, max = 255, message = "Vendor Address must be between 2 and 255 characters long")
	private String address4;

	@Schema(description = "Vendor City", example = "Coda Technology Solutions Private Limited City")
	private String city;

	@Schema(description = "Vendor State", example = "Coda Technology Solutions Private Limited State")
	private String state;

	@Schema(description = "Vendor Country", example = "Coda Technology Solutions Private Limited Country")
	private String country;

	@Schema(description = "Vendor Postal Code", example = "Coda Technology Solutions Private Limited Postal Code")
	private String postalCode;

	@Schema(description = "Vendor Telephone Number", example = "(044) 3706012")
//    @Pattern(regexp = "^\\+*\\(?\\d{1,4}\\)?[-\\s./0-9]*$", message = "Invalid Telephone Number")
	private String telephoneNo;

//    @Pattern(regexp = "\\+?\\d{7,}", message = "Invalid Fax Number")
	private String fax;

//    @Pattern(regexp = "^\\+?\\(?\\d{3}\\)?[-\\s.]?\\d{3}[-\\s.]?\\d{4,6}$", message = "Invalid Mobile Number")
	private String mobileNo;

	@Schema(description = "Vendor Email", example = "trialforall2022@gmail.com")
//    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
	private String email;

	@Schema(description = "Vendor Website", example = "https://codasol.com")
//    @Pattern(regexp = "^(https?://(?:www\\.)?)[a-zA-Z]{2,}(?:\\.[a-zA-Z]{2,})++(?:/[a-zA-Z0-9]{2,})*+$", message = "Invalid Website")
	private String website;

	@Schema(description = "Vendor Acquired By", example = "Trial for All Vendor Name Ltd. Acquired By")
	private String acquiredBy;

	@Schema(description = "Vendor Status", example = "true")
	private Boolean status;

	@JsonIgnore
	private Map<String, Object> dynamicFields = new HashMap<>(); // Changed the value type to String

	@JsonAnyGetter
	public Map<String, Object> getDynamicFields() {
		return dynamicFields;
	}

	@JsonAnySetter
	public void setDynamicFields(String key, Object value) {
		this.dynamicFields.put(key, value);
	}
}
