package com.example.pkscl.domain.ledger;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ReceiptModel {
    String eventNumber;
    String receiptNumber;
    String receiptTitle;
    String receiptContext;
    List<String> context;
    List<String> price;
    List<String> amount;
    String receiptImgPath;
    MultipartFile receiptImgFile;
}
