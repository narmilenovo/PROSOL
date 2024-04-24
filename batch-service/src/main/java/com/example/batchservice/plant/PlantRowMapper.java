package com.example.batchservice.plant;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.example.batchservice.request.PlantRequest;

public class PlantRowMapper implements RowMapper<PlantRequest> {

	@Override
	public PlantRequest mapRow(RowSet rs) throws Exception {
		if (rs == null || rs.getCurrentRow() == null) {
			return null;
		}
		PlantRequest plantRequest = new PlantRequest();
		return plantRequest;
	}

}
