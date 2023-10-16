package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.MaterialStrategicGroupRequest;
import com.example.sales_otherservice.dto.response.MaterialStrategicGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface MaterialStrategicGroupService {
    MaterialStrategicGroupResponse saveMsg(MaterialStrategicGroupRequest materialStrategicGroupRequest) throws ResourceFoundException;

    List<MaterialStrategicGroupResponse> getAllMsg();

    MaterialStrategicGroupResponse getMsgById(Long id) throws ResourceNotFoundException;

    List<MaterialStrategicGroupResponse> findAllStatusTrue();

    MaterialStrategicGroupResponse updateMsg(Long id, MaterialStrategicGroupRequest updateMaterialStrategicGroupRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteMsgById(Long id) throws ResourceNotFoundException;
}
