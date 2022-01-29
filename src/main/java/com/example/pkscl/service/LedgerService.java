package com.example.pkscl.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.text.Position;

import com.example.pkscl.domain.ledger.Event;
import com.example.pkscl.domain.ledger.Quarter;
import com.example.pkscl.domain.ledger.QuarterKey;
import com.example.pkscl.domain.ledger.Receipt;
import com.example.pkscl.domain.ledger.Receiptdetail;
import com.example.pkscl.domain.major.Major;
import com.example.pkscl.repository.EventRepository;
import com.example.pkscl.repository.MajorRepository;
import com.example.pkscl.repository.QuarterRepository;
import com.example.pkscl.repository.ReceiptRepository;
import com.example.pkscl.repository.ReceiptdetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LedgerService {

    private final MajorRepository majorRepository;
    private final QuarterRepository quarterRepository;
    private final EventRepository eventRepository;
    private final ReceiptRepository receiptRepository;
    private final ReceiptdetailRepository receiptdetailRepository;
    
    @Autowired
    public LedgerService(MajorRepository majorRepository, QuarterRepository quarterRepository, EventRepository eventRepository, ReceiptRepository receiptRepository, ReceiptdetailRepository receiptdetailRepository) {
        this.majorRepository = majorRepository;
        this.quarterRepository = quarterRepository;
        this.eventRepository = eventRepository;
        this.receiptRepository = receiptRepository;
        this.receiptdetailRepository = receiptdetailRepository;
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
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        List<Quarter> quarterList = quarterRepository.findByMajornumber(Integer.parseInt(majorNumber));

        for(Quarter quarter : quarterList){
            String opendate = quarter.getOpendate();
            String enddate = quarter.getEnddate();
            // String quarterstatus는 현재날짜가 opendate와 enddate 사이에 있는지 확인하는 값

            // 현재 날짜
            String quarterstatus = "";
            if(opendate.compareTo(getCurrentDate()) <= 0 && enddate.compareTo(getCurrentDate()) >= 0){
                quarterstatus = "true";
            }else{
                quarterstatus = "false";
            }
            String quarterNumber = "quarter"+quarter.getQuarternumber();
            result.put(quarterNumber, quarterstatus);
        }

        // 나머지 quarterstatus는 false
        for(int i = quarterList.size()+1; i <= 4; i++){
            String QuarterNumber = "quarter"+String.valueOf(i);
            result.put(QuarterNumber, "False");
        }
        
        return result;
    }

    private String getCurrentDate() {
        java.util.Date date = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date);
        return currentDate;
    }

    public LinkedHashMap<String, Object> getQuarterData(String majorNumber){

        LinkedHashMap<String, Object> ledger = new LinkedHashMap<>();
        List<Quarter> quarterList = quarterRepository.findByMajornumber(Integer.parseInt(majorNumber));
        
        for(Quarter quarter : quarterList){
            LinkedHashMap<String, Object> quarterMap = new LinkedHashMap<>();
            // majorNumber는 int, i는 String
            String openDate = quarter.getOpendate();
            String endDate = quarter.getEnddate();
            String quarterNumber = "quarter" + quarter.getQuarternumber();
            List<Object> eventList = getEventList(majorNumber, quarterNumber);
            quarterMap.put("openDate", openDate);
            quarterMap.put("endDate", endDate);
            quarterMap.put("eventList", eventList);
            ledger.put(quarterNumber, quarterMap);
        }

        return ledger;
    }

    public List<Object> getEventList(String majorNumber, String quarterNumber){
        ArrayList<Object> result = new ArrayList<>();
        List<Event> eventList = eventRepository.findByMajornumberAndQuarternumber(Integer.parseInt(majorNumber), quarterNumber);
        for(Event event : eventList){
            LinkedHashMap<String, Object> eventMap = new LinkedHashMap<>();
            String eventTitle = event.getEventtitle();
            String eventContext = event.getEventcontext();
            List<Object> receiptList = getReceiptList(event.getEventnumber());
            eventMap.put("eventTitle", eventTitle);
            eventMap.put("eventContext", eventContext);
            eventMap.put("receiptList", receiptList);

            result.add(eventMap);
        }
        return result;
    }

    public List<Object> getReceiptList(int eventNumber){
        ArrayList<Object> result = new ArrayList<>();
        List<Receipt> receiptList = receiptRepository.findByEventnumber(eventNumber);
        for(Receipt receipt : receiptList){
            LinkedHashMap<String, Object> receiptMap = new LinkedHashMap<>();
            String receiptTitle = receipt.getReceipttitle();
            String receiptImg = receipt.getReceiptimg();
            String receiptContext = receipt.getReceiptcontext();
            List<Object> receiptdetailList = getReceiptDetailList(receipt.getReceiptnumber());

            receiptMap.put("receiptTitle", receiptTitle);
            receiptMap.put("receiptImg", receiptImg);
            receiptMap.put("receiptContext", receiptContext);
            receiptMap.put("receiptDetailList", receiptdetailList);

            result.add(receiptMap);
        }
        return result;
    }

    public List<Object> getReceiptDetailList(int receiptNumber){
        ArrayList<Object> result = new ArrayList<>();
        List<Receiptdetail> receiptdetailList = receiptdetailRepository.findByReceiptnumber(receiptNumber);
        for(Receiptdetail receiptdetail : receiptdetailList){
            LinkedHashMap<String, Object> receiptdetailMap = new LinkedHashMap<>();
            String context = receiptdetail.getContext();
            String price = receiptdetail.getPrice();
            String amount = receiptdetail.getAmount();
            receiptdetailMap.put("context", context);
            receiptdetailMap.put("price", price);
            receiptdetailMap.put("amount", amount);
            result.add(receiptdetailMap);
        }
        return result;
    }


    
   
    
}
