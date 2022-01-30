package com.example.pkscl.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.pkscl.service.LedgerService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
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

        return ledgerService.getLedgerData(majorNumber, position);
    }

    @GetMapping(value = "/ledger/admin")
    public Map<String, Object> getLedgerAdmin(@RequestParam(value = "majorNumber", required = true) String majorNumber, HttpServletRequest request, HttpServletResponse response) {
        String position = (String) request.getSession(false).getAttribute("position");

        return ledgerService.getLedgerData(majorNumber, position);
    }

    // @PatchMapping(value = "/ledger")
    // public void patchLedger(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {
    //     String position = (String) request.getSession(false).getAttribute("position");
    //     String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
    //     String patchStatus = (String) body.get("status");
    //     List<String> emailList = (List<String>) body.get("email");

    //     // 400 Bad Request
    //     if (emailList == null || emailList.size() == 0 || patchStatus == null) {
    //         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    //         return;
    //     }

    //     // 403 Forbidden
    //     if (!position.equals("president")) {
    //         response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    //         return;
    //     }

    //     // 서비스 호출 및 반환
    //     ledgerService.patchLedger(majorNumber, patchStatus, emailList);
    // }
}
