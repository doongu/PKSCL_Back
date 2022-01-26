package com.example.pkscl.controller;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import com.example.pkscl.service.LedgerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LedgerController {

    private final LedgerService ledgerService;

    @Autowired
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping(value = "/ledger")
    public ResponseEntity<LinkedHashMap<String, Object>> pkscl(HttpServletRequest request) {
        String majorNumber = (String) request.getSession(false).getAttribute("majorNumber");
        return new ResponseEntity<>(ledgerService.getMajorData(majorNumber), HttpStatus.OK);
    }

}
