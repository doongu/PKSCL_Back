package com.example.pkscl.service;

import java.util.LinkedHashMap;

import com.example.pkscl.domain.major.Major;

import com.example.pkscl.repository.MajorRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LedgerService {

    private final MajorRepository majorRepository;
    
    @Autowired
    public LedgerService(MajorRepository majorRepository) {
        this.majorRepository = majorRepository;
    }


    public LinkedHashMap<String, Object> getMajorData(String major) {
        LinkedHashMap<String, Object> scldata = new LinkedHashMap<>();
        scldata.put("studentPresident", getPresidentData(major));
        scldata.put("quarterStatus", getQuarterStatus(major));
        scldata.put("quarter", getQuarterData(major));
        return scldata;
    }

    // 해당 학과의 major 정보 반환
    public LinkedHashMap<String, Object> getPresidentData(String majorNumber) {

        LinkedHashMap<String, Object> studentPresident = new LinkedHashMap<>();
        Major major = majorRepository.findByMajornumber(majorNumber);
        String majorName = major.getMajorname();
        String name = major.getName();
        String phoneNumber = major.getPhonenumber();
        String email = major.getEmail();

        studentPresident.put("major", majorName);
        studentPresident.put("name", name);
        studentPresident.put("phoneNumber", phoneNumber);
        studentPresident.put("email", email);

        return studentPresident;
    }

    // 임시 코드
    public LinkedHashMap<String, Object> getQuarterStatus(String majorNumber){
        LinkedHashMap<String, Object> quaterStatus = new LinkedHashMap<>();
        quaterStatus.put("1", "quarterStatus1");
        return quaterStatus;
    }

    public LinkedHashMap<String, Object> getQuarterData(String majorNumber){
        LinkedHashMap<String, Object> quater = new LinkedHashMap<>();
        quater.put("1", "quarter1");
        return quater;
    }
   
    
}
