package com.example.pkscl.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.pkscl.service.LedgerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LedgerController {

    private final LedgerService ledgerService;

    @Autowired
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping(value = "/ledger")
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

    @GetMapping(value = "/ledger/admin")
    public Map<String, Object> getLedgerAdmin(@RequestParam(value = "majorNumber", required = true) String majorNumber, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");

        return ledgerService.getLedgerData(majorNumber, position);
    }

    @PostMapping(value = "/ledger")
    public void addLedger(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        String position = (String) request.getSession(false).getAttribute("position");
        String status = (String) request.getSession(false).getAttribute("status");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval")) {
            response.setStatus(403);
            return;
        }

        ledgerService.addLedgerData(majorNumber, body);
    }

    @DeleteMapping(value = "/ledger")
    public void deleteLedger(@RequestParam(value = "eventNumber", required = true) String eventNumber, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        String status = (String) request.getSession(false).getAttribute("status");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval")) {
            response.setStatus(403);
            return;
        }

        ledgerService.deleteLedgerData(eventNumber);
    }

    @PutMapping(value = "/ledger")
    public void putLedger(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        String status = (String) request.getSession(false).getAttribute("status");

        // 403 Forbidden
        if(!position.equals("president") || !status.equals("approval")) {
            response.setStatus(403);
            return;
        }

        ledgerService.putLedgerData(body);
    }

    @GetMapping(value = "/ledger-date")
    public Map<String, Object> getLedgerDate(@RequestParam(value = "majorNumber", required = false) String adminMajorNumber, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");
        if(position.equals("admin")) {
            return ledgerService.getLedgerDate(adminMajorNumber);
        }
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");

        return ledgerService.getLedgerDate(majorNumber);
    }

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


}
