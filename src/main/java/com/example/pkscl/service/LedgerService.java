package com.example.pkscl.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.example.pkscl.domain.ledger.Event;
import com.example.pkscl.domain.ledger.Quarter;
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


    public Map<String, Object> getLedgerData(String major, String position) {
        LinkedHashMap<String, Object> scldata = new LinkedHashMap<>();
        scldata.put("studentPresident", getPresidentData(major));
        scldata.put("quarter", getQuarterData(major, position));
        return scldata;
    }

    // 해당 학과의 major 정보 반환
    public Map<String, Object> getPresidentData(String majorNumber) {

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

    private String getCurrentDate() {
        java.util.Date date = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }


    public boolean getQuarterStatus(String majorNumber, String quarterNumber){
        Quarter quarter = quarterRepository.findByMajornumberAndQuarternumber(Integer.parseInt(majorNumber), quarterNumber);
        if(quarter == null){
            return false;
        }
        String opendate = quarter.getOpendate();
        String closedate = quarter.getClosedate();
        
        if(opendate.compareTo(getCurrentDate()) <= 0 && closedate.compareTo(getCurrentDate()) >= 0){
            return true;
        }else{
            return false;
        }
    }


    public Map<String, Object> getQuarterData(String majorNumber, String position) {
        LinkedHashMap<String, Object> ledger = new LinkedHashMap<>();

        for(int i = 1; i <= 4; i++){
            LinkedHashMap<String, Object> quarterMap = new LinkedHashMap<>();
            String quarterNumber = "quarter" + i;
            boolean status = getQuarterStatus(majorNumber, quarterNumber);
            quarterMap.put("status", String.valueOf(status));
            List<Object> eventList = getEventList(majorNumber, quarterNumber);
            if((!status && position.equals("student")) || eventList.isEmpty()){

            }else{
                quarterMap.put("eventList", eventList);
            }
            ledger.put(quarterNumber, quarterMap);
        }

        return ledger;
    }

    public List<Object> getEventList(String majorNumber, String quarterNumber){
        ArrayList<Object> result = new ArrayList<>();
        Quarter quarter = quarterRepository.findByMajornumberAndQuarternumber(Integer.parseInt(majorNumber), quarterNumber);
        if(quarter == null) return result;
        List<Event> eventList = eventRepository.findByQuarterid(quarter.getQuarterid());
        for(Event event : eventList){
            LinkedHashMap<String, Object> eventMap = new LinkedHashMap<>();
            String eventNumber = String.valueOf(event.getEventnumber());
            String eventTitle = event.getEventtitle();
            String eventContext = event.getEventcontext();
            List<Object> receiptList = getReceiptList(event.getEventnumber());
            eventMap.put("eventNumber", eventNumber);
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

    // public void addLedgerData(String majorNumber, Map<String,Object> body){
    //     String quarter = (String) body.get("quater");
    //     // 이벤트 추가
    //     String eventTitle = (String) body.get("eventTitle");
    //     String eventContext = (String) body.get("eventContext");
    //     Event event = new Event();
    //     event.setMajornumber(Integer.parseInt(majorNumber));
    //     event.setQuarternumber(quarter);

    // }


    
   
    
}
