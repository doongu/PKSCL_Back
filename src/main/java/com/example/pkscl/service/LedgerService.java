package com.example.pkscl.service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.example.pkscl.domain.ledger.Event;
import com.example.pkscl.domain.ledger.Quarter;
import com.example.pkscl.domain.ledger.Receipt;
import com.example.pkscl.domain.ledger.ReceiptModel;
import com.example.pkscl.domain.ledger.Receiptdetail;
import com.example.pkscl.domain.major.Major;
import com.example.pkscl.repository.EventRepository;
import com.example.pkscl.repository.MajorRepository;
import com.example.pkscl.repository.QuarterRepository;
import com.example.pkscl.repository.ReceiptRepository;
import com.example.pkscl.repository.ReceiptdetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        Major major = majorRepository.findByMajornumber(Integer.parseInt(majorNumber));
        String majorName = major.getMajorname();
        String name = major.getName();
        String phoneNumber = major.getPhonenumber();
        String email = major.getEmail();
        String majorLogo = major.getMajorlogo();
        
        studentPresident.put("major", majorName);
        studentPresident.put("name", name);
        studentPresident.put("phoneNumber", phoneNumber);
        studentPresident.put("email", email);
        studentPresident.put("majorLogo", majorLogo);

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
        // 이벤트 리스트를 eventsequence로 오름차순
        List<Event> eventList = eventRepository.findByQuarteridOrderByEventsequence(quarter.getQuarterid());
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
            try{
                String totalAmount = Integer.parseInt(price) * Integer.parseInt(amount) + "";
                receiptdetailMap.put("totalAmount", totalAmount);
            }catch(Exception e){
                receiptdetailMap.put("totalAmount", "");
            }
            result.add(receiptdetailMap);
        }
        return result;
    }
    

    @Transactional
    public void deleteEvent(String eventNumber){
        
        // eventNumber를 가진 receipt 삭제
        deleteReceipt(eventNumber);
        // eventNumber를 가진 event 삭제
        eventRepository.deleteByEventnumber(Integer.parseInt(eventNumber));

    }

    @Transactional
    public void deleteReceipt(String eventNumber){

        List<Receipt> receiptList = receiptRepository.findByEventnumber(Integer.parseInt(eventNumber));
        for(Receipt receipt : receiptList){

            // receiptNumber를 가진 receiptDetail 삭제
            List<Receiptdetail> receiptDetailList = receiptdetailRepository.findByReceiptnumber(receipt.getReceiptnumber());
            for(Receiptdetail receiptDetail : receiptDetailList){
                receiptdetailRepository.delete(receiptDetail);
            }

            // eventNumber를 가진 receipt 삭제
            receiptRepository.delete(receipt);

            // receiptImg 삭제
            String receiptImgPath = receipt.getReceiptimg();
            String fileName = receiptImgPath.substring(receiptImgPath.lastIndexOf("/")+1);
            fileDelete(fileName);
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


    public boolean checkMajorAndEvent(String majorNumber, String eventNumber) {
        int quarterID = eventRepository.findByEventnumber(Integer.parseInt(eventNumber)).getQuarterid();
        if(majorNumber.equals(quarterRepository.findByQuarterid(quarterID).getMajornumber() + "")) return true;
        return false;
    }


    public void patchEvent(String eventNumber, String eventTitle, String eventContext) {
        Event event = eventRepository.findByEventnumber(Integer.parseInt(eventNumber));
        event.setEventtitle(eventTitle);
        event.setEventcontext(eventContext);
        eventRepository.save(event);
    }


    public void addEvent(String majorNumber, String quarter) {
        int quarterID = quarterRepository.findByMajornumberAndQuarternumber(Integer.parseInt(majorNumber), quarter).getQuarterid();
        Event event = new Event();
        event.setQuarterid(quarterID);
        event.setEventtitle("");
        event.setEventcontext("");
        Event savedEvent = eventRepository.save(event);

        // eventSequence 설정
        int eventNumber = savedEvent.getEventnumber();
        event.setEventsequence(eventNumber);
        eventRepository.save(event);
    }


    public void postReceipt(ReceiptModel receiptModel) {

        MultipartFile receiptImg = receiptModel.getReceiptImgFile();
        Receipt receipt = new Receipt();

        if(receiptImg != null){
            log.info("file found");
            String dir = "./static/receiptImg/";
            String filename = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
            String ext = receiptImg.getOriginalFilename().substring(receiptImg.getOriginalFilename().lastIndexOf("."));
            try {
                fileUpload(filename+ ext, receiptImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            receipt.setReceiptimg(dir + filename + ext);
        }else{
            log.info("file not found");
            receipt.setReceiptimg("./static/receiptImg/defaultReceiptImg.jpg");
        }

        receipt.setReceipttitle(receiptModel.getReceiptTitle());
        receipt.setReceiptcontext(receiptModel.getReceiptContext());
        receipt.setEventnumber(Integer.parseInt(receiptModel.getEventNumber()));

        receiptRepository.save(receipt);


        List<String> contextList = receiptModel.getContext();
        List<String> priceList = receiptModel.getPrice();
        List<String> amountList = receiptModel.getAmount();

        for(int i = 0; i < contextList.size(); i++){
            Receiptdetail receiptdetail = new Receiptdetail();
            receiptdetail.setContext(contextList.get(i));
            receiptdetail.setPrice(priceList.get(i));
            receiptdetail.setAmount(amountList.get(i));
            receiptdetail.setReceiptnumber(receipt.getReceiptnumber());
            receiptdetailRepository.save(receiptdetail);
        }



    }


    public void putReceipt(ReceiptModel receiptModel) {

        MultipartFile receiptImg = receiptModel.getReceiptImgFile();
        Receipt receipt = receiptRepository.findByReceiptnumber(Integer.parseInt(receiptModel.getReceiptNumber()));

        if(receiptImg != null){
            log.info("file found");
            String dir = "./static/receiptImg/";
            String filename = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
            String ext = receiptImg.getOriginalFilename().substring(receiptImg.getOriginalFilename().lastIndexOf("."));
            String prevFilePath = receipt.getReceiptimg();
            String prevFileName = prevFilePath.substring(prevFilePath.lastIndexOf("/")+1);

            // 이전 파일 삭제
            fileDelete(prevFileName);
            // 새파일 추가
            try {
                fileUpload(filename+ ext, receiptImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            receipt.setReceiptimg(dir + filename + ext);
        }else{
            log.info("file not found");
        }

        receipt.setReceipttitle(receiptModel.getReceiptTitle());
        receipt.setReceiptcontext(receiptModel.getReceiptContext());
        receiptRepository.save(receipt);

        List<String> contextList = receiptModel.getContext();
        List<String> priceList = receiptModel.getPrice();
        List<String> amountList = receiptModel.getAmount();
        
        //receiptDetail 삭제
        List<Receiptdetail> receiptDetailList = receiptdetailRepository.findByReceiptnumber(receipt.getReceiptnumber());
        for(Receiptdetail receiptDetail : receiptDetailList){
            receiptdetailRepository.delete(receiptDetail);
        }

        //receiptDetail 추가
        for(int i = 0; i < contextList.size(); i++){
            Receiptdetail receiptdetail = new Receiptdetail();
            receiptdetail.setContext(contextList.get(i));
            receiptdetail.setPrice(priceList.get(i));
            receiptdetail.setAmount(amountList.get(i));
            receiptdetail.setReceiptnumber(receipt.getReceiptnumber());
            receiptdetailRepository.save(receiptdetail);
        }

    }

    @Transactional
    public void deleteReceiptList(String receiptNumberList) {
        String[] receiptNumberArray = receiptNumberList.split(",");
        for(String receiptNumber : receiptNumberArray){

            // receiptNumber를 가진 receiptDetail 삭제
            List<Receiptdetail> receiptDetailList = receiptdetailRepository.findByReceiptnumber(Integer.parseInt(receiptNumber));
            for(Receiptdetail receiptDetail : receiptDetailList){
                receiptdetailRepository.delete(receiptDetail);
            }

            // receiptNumber를 가진 receipt 삭제
            Receipt receipt = receiptRepository.findByReceiptnumber(Integer.parseInt(receiptNumber));
            receiptRepository.delete(receipt);

            // receiptImg 삭제
            String receiptImgPath = receipt.getReceiptimg();
            String fileName = receiptImgPath.substring(receiptImgPath.lastIndexOf("/")+1);
            fileDelete(fileName);
        }
    }


    public void patchEventSequence(List<String> eventNumberList) {
        // eventNumberList => ["12","13","16","2"] 와 같이 eventNumber를 받아옴
        // eventNumberList의 인덱스를 기준으로 eventSequence를 업데이트
        for(int i = 0; i < eventNumberList.size(); i++){
            Event event = eventRepository.findByEventnumber(Integer.parseInt(eventNumberList.get(i)));
            event.setEventsequence(i + 1);
            eventRepository.save(event);
        }
    }


    public void fileUpload(String filename, MultipartFile file) throws Exception {
        String path = System.getProperty("user.dir") + "/static/static/receiptImg/";
        File saveFile = new File(path + filename);
        file.transferTo(saveFile);
        // 1초 지연
        Thread.sleep(1000);
    }

    public void fileDelete(String filename) {
        if(filename.equals("defaultReceiptImg.jpg")) return;
        String path = System.getProperty("user.dir") + "/static/static/receiptImg/";
        File saveFile = new File(path + filename);
        saveFile.delete();
    }

    public boolean checkMajor(String object, String objectNumber, String majorNumber) {
        log.info("object : " + object + ", objectNumber : " + objectNumber + ", majorNumber : " + majorNumber);
        if(object.equals("event")){
            log.info("event");
            Event event = eventRepository.findByEventnumber(Integer.parseInt(objectNumber));
            Quarter quarter = quarterRepository.findByQuarterid(event.getQuarterid());
            if(Integer.parseInt(majorNumber) != quarter.getMajornumber()) return false;

        }else if(object.equals("receipt")){
            log.info("receipt");
            Receipt receipt = receiptRepository.findByReceiptnumber(Integer.parseInt(objectNumber));
            Event event = eventRepository.findByEventnumber(receipt.getEventnumber());
            Quarter quarter = quarterRepository.findByQuarterid(event.getQuarterid());
            if(Integer.parseInt(majorNumber) != quarter.getMajornumber()) return false;
        }

        return true;
    }

    
    
}
