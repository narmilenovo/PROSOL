package com.example.vendor_masterservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
