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


    public LinkedHashMap<String, Object> getLedgerData(String major) {
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

    public LinkedHashMap<String, Object> getQuarterStatus(String majorNumber){
        LinkedHashMap<String, Object> quarterStatus = new LinkedHashMap<>();
        String quarter1 = "1";
        String quarter2 = "2";
        String quarter3 = "3";
        String quarter4 = "4";
        quarterStatus.put("quarter1", quarter1);
        quarterStatus.put("quarter2", quarter2);
        quarterStatus.put("quarter3", quarter3);
        quarterStatus.put("quarter4", quarter4);
        return quarterStatus;
    }

    public LinkedHashMap<String, Object> getQuarterData(String majorNumber){

        LinkedHashMap<String, Object> ledger = new LinkedHashMap<>();
        
        for(int i = 1; i <= 4; i++){
            LinkedHashMap<String, Object> quarterN = new LinkedHashMap<>();
            LinkedHashMap<String, Object> eventList = new LinkedHashMap<>();
            String openDate = "2020-01-01";
            String endDate = "2020-03-31";
            String quarterNumber = String.valueOf(i);
            String quarter = "quarter" + quarterNumber;
            quarterN.put("openDate", openDate);
            quarterN.put("endDate", endDate);
            quarterN.put("eventList", eventList);
            ledger.put(quarter, quarterN);
        }

        return ledger;
    }
   
    
}
