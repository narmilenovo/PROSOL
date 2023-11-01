package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.EquipmentUnit;
import com.example.generalsettings.repo.EquipmentUnitRepo;
import com.example.generalsettings.request.EquipmentUnitRequest;
import com.example.generalsettings.response.EquipmentUnitResponse;
import com.example.generalsettings.service.EquipmentUnitService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipmentUnitServiceImpl implements EquipmentUnitService{
	  private final ModelMapper modelMapper;
		 private final EquipmentUnitRepo equipmentUnitRepo;

		    public static final String ATTRIBUTE_TYPE_NOT_FOUND_MESSAGE = null;

		    @Override
		    public List<EquipmentUnitResponse> getAllEquipmentUnit() {
		        List<EquipmentUnit> equipmentUnit = equipmentUnitRepo.findAll();
		        return equipmentUnit.stream().map(this::mapToEquipmentUnitResponse).toList();
		    }

		    @Override
		    public EquipmentUnitResponse updateEquipmentUnit(Long id, EquipmentUnitRequest equipmentUnitRequest) throws ResourceNotFoundException, AlreadyExistsException {

		        Optional<EquipmentUnit> existEquipmentUnitName = equipmentUnitRepo.findByEquipmentUnitName(equipmentUnitRequest.getEquipmentUnitName());
		        if (existEquipmentUnitName.isPresent() && !existEquipmentUnitName.get().getEquipmentUnitName().equals(equipmentUnitRequest.getEquipmentUnitName())) {
		            throw new AlreadyExistsException("EquipmentUnit with this name already exists");
		        } else {
		            EquipmentUnit existingEquipmentUnit = this.findEquipmentUnitById(id);
		            modelMapper.map(equipmentUnitRequest, existingEquipmentUnit);
		            equipmentUnitRepo.save(existingEquipmentUnit);
		            return mapToEquipmentUnitResponse(existingEquipmentUnit);
		        }
		    }

		    public void deleteEquipmentUnit(Long id) throws ResourceNotFoundException {
		        EquipmentUnit equipmentUnit = this.findEquipmentUnitById(id);
		        equipmentUnitRepo.deleteById(equipmentUnit.getId());
		    }

		    @Override
		    public EquipmentUnitResponse saveEquipmentUnit(EquipmentUnitRequest equipmentUnitRequest) throws  AlreadyExistsException {

		        Optional<EquipmentUnit> existEquipmentUnitName = equipmentUnitRepo.findByEquipmentUnitName(equipmentUnitRequest.getEquipmentUnitName());
		        if (existEquipmentUnitName.isPresent() ) {
		            throw new AlreadyExistsException("EquipmentUnit with this name already exists");
		        } else {
		            EquipmentUnit equipmentUnit = modelMapper.map(equipmentUnitRequest, EquipmentUnit.class);
		            equipmentUnitRepo.save(equipmentUnit);
		            return mapToEquipmentUnitResponse(equipmentUnit);
		        }
		    }


		    private EquipmentUnitResponse mapToEquipmentUnitResponse(EquipmentUnit equipmentUnit) {
		        return modelMapper.map(equipmentUnit, EquipmentUnitResponse.class);
		    }

		    private EquipmentUnit findEquipmentUnitById(Long id) throws ResourceNotFoundException {
		        Optional<EquipmentUnit> equipmentUnit = equipmentUnitRepo.findById(id);
		        if (equipmentUnit.isEmpty()) {
		            throw new ResourceNotFoundException(ATTRIBUTE_TYPE_NOT_FOUND_MESSAGE);
		        }
		        return equipmentUnit.get();
		    }



		    @Override
		    public EquipmentUnitResponse getEquipmentUnitById(Long id) throws ResourceNotFoundException {
		        EquipmentUnit equipmentUnit = this.findEquipmentUnitById(id);
		        return mapToEquipmentUnitResponse(equipmentUnit);
		    }

		    @Override
		    public List<EquipmentUnitResponse> updateBulkStatusEquipmentUnitId(List<Long> id) {
		        List<EquipmentUnit> existingEquipmentUnit = equipmentUnitRepo.findAllById(id);
		        for (EquipmentUnit equipmentUnit : existingEquipmentUnit) {
		            equipmentUnit.setEquipmentUnitStatus(!equipmentUnit.getEquipmentUnitStatus());
		        }
		        equipmentUnitRepo.saveAll(existingEquipmentUnit);
		        return existingEquipmentUnit.stream().map(this::mapToEquipmentUnitResponse).toList();
		    }

		    @Override
		    public EquipmentUnitResponse updateStatusUsingEquipmentUnitId(Long id) throws ResourceNotFoundException {
		        EquipmentUnit existingEquipmentUnit = this.findEquipmentUnitById(id);
		        existingEquipmentUnit.setEquipmentUnitStatus(!existingEquipmentUnit.getEquipmentUnitStatus());
		        equipmentUnitRepo.save(existingEquipmentUnit);
		        return mapToEquipmentUnitResponse(existingEquipmentUnit);
		    }
}
