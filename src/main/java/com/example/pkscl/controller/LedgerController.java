package com.example.pkscl.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.pkscl.domain.ledger.ReceiptModel;
import com.example.pkscl.service.LedgerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@Slf4j
// 문제점 1.  majorNumber랑 API로 수정하고자하는 곳이 다르면 403을 띄워야함.. 근데 이많은걸 다 어떻게?
@RestController
public class LedgerController {

    private final LedgerService ledgerService;

    @Autowired
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping(value = "/major-info")
    public Map<String, Object> getLedger(HttpServletRequest request, HttpServletResponse response) {
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String position = (String) request.getSession(false).getAttribute("position");
        String status = (String) request.getSession(false).getAttribute("status");

        if(!status.equals("approval")) {
            response.setStatus(403);
            return null;
        }

        return ledgerService.getLedgerData(majorNumber, position);
    }

    @GetMapping(value = "/major-info/admin")
    public Map<String, Object> getLedgerAdmin(@RequestParam(value = "major-number", required = true) String majorNumber, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");

        return ledgerService.getLedgerData(majorNumber, position);
    }

    @GetMapping(value = "/temp-major-info")
    public Map<String, Object> getTempLedger(HttpServletRequest request, HttpServletResponse response){
        return ledgerService.getLedgerData("0", "president");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping(value = "/event")
    public void addLedger(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String position = (String) request.getSession(false).getAttribute("position");
        String status = (String) request.getSession(false).getAttribute("status");
        String quarter = (String) body.get("quarter");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval")) {
            response.setStatus(403);
            return;
        }

        ledgerService.addEvent(majorNumber, quarter);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @DeleteMapping(value = "/event")
    public void deleteLedger(@RequestParam(value = "event-number", required = true) String eventNumber, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String status = (String) request.getSession(false).getAttribute("status");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval") || !ledgerService.checkMajor("event", eventNumber, majorNumber)) {
            response.setStatus(403);
            return;
        }
        

        ledgerService.deleteEvent(eventNumber);
    }

    @GetMapping(value = "/ledger-date")
    public Map<String, Object> getLedgerDate(@RequestParam(value = "major-number", required = false) String adminMajorNumber, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        if(position.equals("admin")) {
            return ledgerService.getLedgerDate(adminMajorNumber);
        }
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");

        return ledgerService.getLedgerDate(majorNumber);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PutMapping(value = "/ledger-date")
    public void putLedgerDate(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String status = (String) request.getSession(false).getAttribute("status");
        String quarter = (String) body.get("quarter");
        String openDate = (String) body.get("openDate");
        String closeDate = (String) body.get("closeDate");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval")) {
            response.setStatus(403);
            return;
        }

        ledgerService.putLedgerDate(majorNumber, quarter, openDate, closeDate);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PatchMapping(value = "/event")
    public void patchEvent(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String status = (String) request.getSession(false).getAttribute("status");
        String eventNumber = (String) body.get("eventNumber");
        String eventTitle = (String) body.get("eventTitle");
        String eventContext = (String) body.get("eventContext");

        if(!position.equals("president")){
            log.info("position error");
        }
        if(!status.equals("approval")) {
            log.info("status error");
        }
        if(!ledgerService.checkMajor("event", eventNumber, majorNumber)) {
            log.info("majorNumber error");
        }
        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval") || !ledgerService.checkMajor("event", eventNumber, majorNumber)) {
            response.setStatus(403);
            return;
        }

        ledgerService.patchEvent(eventNumber, eventTitle, eventContext);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PostMapping(value = "/receipt")
    public void postReceipt(@ModelAttribute ReceiptModel receiptModel, HttpServletRequest request, HttpServletResponse response) {
        
        // receiptModel출력
        System.out.println(receiptModel);
        String position = (String) request.getSession(false).getAttribute("position");
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String status = (String) request.getSession(false).getAttribute("status");

        //403 Forbidden
        if(!position.equals("president") || !status.equals("approval") || !ledgerService.checkMajor("event", receiptModel.getEventNumber(), majorNumber)) {
            response.setStatus(403);
            return;
        }

        ledgerService.postReceipt(receiptModel);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PutMapping(value = "/receipt")
    public void putReceipt(@ModelAttribute ReceiptModel receiptModel, HttpServletRequest request, HttpServletResponse response) {

        // receiptModel출력
        System.out.println(receiptModel);
        String position = (String) request.getSession(false).getAttribute("position");
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String status = (String) request.getSession(false).getAttribute("status");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval") || !ledgerService.checkMajor("receipt", receiptModel.getReceiptNumber(), majorNumber)) {
            response.setStatus(403);
            return;
        }

        ledgerService.putReceipt(receiptModel);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @DeleteMapping(value = "/receipt")
    public void deleteReceipt(@RequestParam(value = "receipt-number", required = true) String receiptNumberList, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String status = (String) request.getSession(false).getAttribute("status");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval") || !ledgerService.checkMajor("receipt", receiptNumberList, majorNumber)) {
            response.setStatus(403);
            return;
        }


        ledgerService.deleteReceiptList(receiptNumberList);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PatchMapping(value = "/event-sequence")
    public void patchEventSequence(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String status = (String) request.getSession(false).getAttribute("status");
        List<String> eventNumberList = (List<String>) body.get("eventNumberList");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval")) {
            response.setStatus(403);
            return;
        }
        for(String eventNumber : eventNumberList) {
            if(!ledgerService.checkMajor("event", eventNumber, majorNumber)) {
                response.setStatus(403);
                return;
            }
        }

        ledgerService.patchEventSequence(eventNumberList);
    }

    

}
