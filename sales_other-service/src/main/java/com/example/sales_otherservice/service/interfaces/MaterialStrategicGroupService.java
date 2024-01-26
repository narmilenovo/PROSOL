package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.MaterialStrategicGroupRequest;
import com.example.sales_otherservice.dto.response.MaterialStrategicGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface MaterialStrategicGroupService {
	MaterialStrategicGroupResponse saveMsg(MaterialStrategicGroupRequest materialStrategicGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<MaterialStrategicGroupResponse> getAllMsg();

	MaterialStrategicGroupResponse getMsgById(Long id) throws ResourceNotFoundException;

	List<MaterialStrategicGroupResponse> findAllStatusTrue();

	MaterialStrategicGroupResponse updateMsg(Long id, MaterialStrategicGroupRequest updateMaterialStrategicGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteMsgById(Long id) throws ResourceNotFoundException;

	void deleteBatchMsg(List<Long> ids) throws ResourceNotFoundException;

	MaterialStrategicGroupResponse updateMsgStatus(Long id) throws ResourceNotFoundException;

	List<MaterialStrategicGroupResponse> updateBatchMsgStatus(List<Long> ids) throws ResourceNotFoundException;
}
