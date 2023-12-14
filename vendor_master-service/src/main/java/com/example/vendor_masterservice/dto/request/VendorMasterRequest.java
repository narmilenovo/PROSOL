package com.example.vendor_masterservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorMasterRequest {
    @Schema(description = "Short Description of the vendor name", example = "Trial for All")
    private String shortDescName;

    @Schema(description = "Vendor Name", example = "Trial for All Vendor Name Ltd.")
    @Size(min = 2, max = 100, message = "Vendor Name must be between 2 and 100 characters long")
    private String name;

    @Schema(description = "Vendor Name2", example = "Trial for All Vendor Name Ltd.")
    @Size(min = 2, max = 100, message = "Vendor Name must be between 2 and 100 characters long")
    private String name2;

    @Schema(description = "Vendor Name3", example = "Trial for All Vendor Name Ltd.")
    @Size(min = 2, max = 100, message = "Vendor Name must be between 2 and 100 characters long")
    private String name3;

    @Schema(description = "Vendor Name4", example = "Trial for All Vendor Name Ltd.")
    @Size(min = 2, max = 100, message = "Vendor Name must be between 2 and 100 characters long")
    private String name4;

    @Schema(description = "Vendor Address", example = "Trial for All Vendor Name Ltd. Address")
    @Size(min = 2, max = 255, message = "Vendor Address must be between 2 and 255 characters long")
    private String address;

    @Schema(description = "Vendor Address2", example = "Trial for All Vendor Name Ltd. Address")
    @Size(min = 2, max = 255, message = "Vendor Address must be between 2 and 255 characters long")
    private String address2;

    @Schema(description = "Vendor Address3", example = "Trial for All Vendor Name Ltd. Address")
    @Size(min = 2, max = 255, message = "Vendor Address must be between 2 and 255 characters long")
    private String address3;

    @Schema(description = "Vendor Address4", example = "Trial for All Vendor Name Ltd. Address")
    @Size(min = 2, max = 255, message = "Vendor Address must be between 2 and 255 characters long")
    private String address4;

    @Schema(description = "Vendor City", example = "Trial for All Vendor Name Ltd. City")
    private String city;

    @Schema(description = "Vendor State", example = "Trial for All Vendor Name Ltd. State")
    private String state;

    @Schema(description = "Vendor Country", example = "Trial for All Vendor Name Ltd. Country")
    private String country;

    @Schema(description = "Vendor Postal Code", example = "Trial for All Vendor Name Ltd. Postal Code")
    private String postalCode;

    @Pattern(regexp = "^\\+*\\(?\\d{1,4}\\)?[-\\s./0-9]*$", message = "Invalid Telephone Number")
    private String telephoneNo;

    @Pattern(regexp = "\\+?\\d{7,}", message = "Invalid Fax Number")
    private String fax;

    @Pattern(regexp = "^\\+?\\(?\\d{3}\\)?[-\\s.]?\\d{3}[-\\s.]?\\d{4,6}$", message = "Invalid Mobile Number")
    private String mobileNo;

    @Schema(description = "Vendor Email", example = "trialforall2022@gmail.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private String email;

    @Pattern(regexp = "^(https?://(?:www\\.)?)[a-zA-Z]{2,}(?:\\.[a-zA-Z]{2,})++(?:/[a-zA-Z0-9]{2,})*+$", message = "Invalid Website")
    private String website;

    @Schema(description = "Vendor Acquired By", example = "Trial for All Vendor Name Ltd. Acquired By")
    private String acquiredBy;

    @Schema(description = "Vendor Status", example = "Trial for All Vendor Name Ltd. Status")
    private Boolean status;
}
