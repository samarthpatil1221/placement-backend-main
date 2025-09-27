package com.example.placement.service.impl;

import com.example.placement.entity.Drive;
import com.example.placement.repository.DriveRepository;
import com.example.placement.service.DriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DriveServiceImpl implements DriveService {

    @Autowired private DriveRepository driveRepository;

    @Override
    public Drive create(Drive drive) {
        return driveRepository.save(drive);
    }

    @Override
    public List<Drive> listAll() {
        return driveRepository.findAll();
    }

    @Override
    public Drive findById(Long id) {
        return driveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drive not found"));
    }

    @Override
    public Drive update(Drive drive) {
        // You can optionally verify the drive exists before saving
        if (drive.getId() == null || !driveRepository.existsById(drive.getId())) {
            throw new RuntimeException("Drive not found for update");
        }
        return driveRepository.save(drive);
    }
}

