package com.example.placement.service;

import com.example.placement.entity.Drive;
import java.util.List;

public interface DriveService {
    Drive create(Drive drive);
    List<Drive> listAll();
    Drive findById(Long id);
    Drive update(Drive drive);
    
}
