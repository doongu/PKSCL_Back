package com.example.pkscl.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

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
            String receiptNumber = String.valueOf(receipt.getReceiptnumber());
            String receiptTitle = receipt.getReceipttitle();
            LinkedHashMap<String, Object> filename = new LinkedHashMap<>();
            String receiptImg = receipt.getReceiptimg();
            filename.put("name", receiptImg);
            String receiptContext = receipt.getReceiptcontext();
            List<Object> receiptdetailList = getReceiptDetailList(receipt.getReceiptnumber());
            receiptMap.put("receiptNumber", receiptNumber);
            receiptMap.put("receiptTitle", receiptTitle);
            receiptMap.put("receiptImg", filename);
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
            // price가 "" 이거나 amount가 "" 이면 totalAmount는 ""
            if(price.equals("") || amount.equals("")){
                receiptdetailMap.put("totalAmount", "");
            }else{
                String totalAmount = Integer.parseInt(price) * Integer.parseInt(amount) + "";
                receiptdetailMap.put("totalAmount", totalAmount);
            }
            result.add(receiptdetailMap);
        }
        return result;
    }
    
    public void addLedgerData(String majorNumber, Map<String,Object> body){
        String quarter = (String) body.get("quarter");
        String eventTitle = (String) body.get("eventTitle");
        String eventContext = (String) body.get("eventContext");
        int quarterid = quarterRepository.findByMajornumberAndQuarternumber(Integer.parseInt(majorNumber), quarter).getQuarterid();
        List<Map<String,Object>> receiptList = (List<Map<String,Object>>) body.get("receiptList");

        Event event = new Event();
        event.setEventtitle(eventTitle);
        event.setEventcontext(eventContext);
        event.setQuarterid(quarterid);
        Event savedEvent = eventRepository.save(event);
        for(Map<String,Object> receipt : receiptList){
            addReceiptData(savedEvent.getEventnumber(), receipt);
        }
    }

    @Transactional
    public void putLedgerData(Map<String,Object> body){
        String eventNumber = (String) body.get("eventNumber");
        String eventTitle = (String) body.get("eventTitle");
        String eventContext = (String) body.get("eventContext");

        // 이벤트 수정
        Event event = eventRepository.findByEventnumber(Integer.parseInt(eventNumber));
        event.setEventtitle(eventTitle);
        event.setEventcontext(eventContext);
        eventRepository.save(event);

        // 하위 영수증 삭제
        deleteReceiptData(eventNumber);

        List<Map<String,Object>> receiptList = (List<Map<String,Object>>) body.get("receiptList");
        for(Map<String,Object> receipt : receiptList){
            addReceiptData(Integer.parseInt(eventNumber), receipt);
        }
    }

    public void addReceiptData(int eventNumber, Map<String,Object> body){
        String receiptTitle = (String) body.get("receiptTitle");
        String receiptImg = (String) body.get("receiptImg");
        String receiptContext = (String) body.get("receiptContext");
        List<Map<String,Object>> receiptDetailList = (List<Map<String,Object>>) body.get("receiptDetailList");

        Receipt receipt = new Receipt();
        receipt.setReceipttitle(receiptTitle);
        receipt.setReceiptimg(receiptImg);
        receipt.setReceiptcontext(receiptContext);
        receipt.setEventnumber(eventNumber);
        Receipt savedReceipt = receiptRepository.save(receipt);
        for(Map<String,Object> receiptDetail : receiptDetailList){
            addReceiptDetailData(savedReceipt.getReceiptnumber(), receiptDetail);
        }
    }

    public void addReceiptDetailData(int receiptNumber, Map<String,Object> body){
        String context = (String) body.get("context");
        String price = (String) body.get("price");
        String amount = (String) body.get("amount");

        Receiptdetail receiptdetail = new Receiptdetail();
        receiptdetail.setContext(context);
        receiptdetail.setPrice(price);
        receiptdetail.setAmount(amount);
        receiptdetail.setReceiptnumber(receiptNumber);
        receiptdetailRepository.save(receiptdetail);
    }

    @Transactional
    public void deleteLedgerData(String eventNumber){
        
        // eventNumber를 가진 receipt 삭제
        deleteReceiptData(eventNumber);
        // eventNumber를 가진 event 삭제
        eventRepository.deleteByEventnumber(Integer.parseInt(eventNumber));
        
    }

    @Transactional
    public void deleteReceiptData(String eventNumber){

        List<Receipt> receiptList = receiptRepository.findByEventnumber(Integer.parseInt(eventNumber));
        for(Receipt receipt : receiptList){

            // receiptNumber를 가진 receiptDetail 삭제
            List<Receiptdetail> receiptDetailList = receiptdetailRepository.findByReceiptnumber(receipt.getReceiptnumber());
            for(Receiptdetail receiptDetail : receiptDetailList){
                receiptdetailRepository.delete(receiptDetail);
            }

            // eventNumber를 가진 receipt 삭제
            receiptRepository.delete(receipt);
        }

    }

    public Map<String, Object> getLedgerDate(String majorNumber){
        Map<String, Object> result = new LinkedHashMap<>();
        List<Quarter> quarterList = quarterRepository.findByMajornumber(Integer.parseInt(majorNumber));
        for(Quarter quarter : quarterList){
            String quarterNumber = quarter.getQuarternumber();
            String openDate = quarter.getOpendate();
            String closeDate = quarter.getClosedate();
            result.put(quarterNumber, new String[]{openDate, closeDate});
        }
        return result;
    }

    public void putLedgerDate(String majorNumber, String quarterNumber, String openDate, String closeDate){
        Quarter quarter = quarterRepository.findByMajornumberAndQuarternumber(Integer.parseInt(majorNumber), quarterNumber);
        quarter.setOpendate(openDate);
        quarter.setClosedate(closeDate);
        quarterRepository.save(quarter);
    }
    
   
    
}
