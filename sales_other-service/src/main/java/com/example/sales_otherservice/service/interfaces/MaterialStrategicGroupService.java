package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.MaterialStrategicGroupRequest;
import com.example.sales_otherservice.dto.response.MaterialStrategicGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface MaterialStrategicGroupService {
	MaterialStrategicGroupResponse saveMsg(MaterialStrategicGroupRequest materialStrategicGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<MaterialStrategicGroupResponse> getAllMsg();

	MaterialStrategicGroupResponse getMsgById(@NonNull Long id) throws ResourceNotFoundException;

	List<MaterialStrategicGroupResponse> findAllStatusTrue();

	MaterialStrategicGroupResponse updateMsg(@NonNull Long id,
			MaterialStrategicGroupRequest updateMaterialStrategicGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteMsgById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchMsg(@NonNull List<Long> ids) throws ResourceNotFoundException;

	MaterialStrategicGroupResponse updateMsgStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<MaterialStrategicGroupResponse> updateBatchMsgStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;
}
