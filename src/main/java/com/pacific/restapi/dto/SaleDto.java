package com.pacific.restapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pacific.restapi.model.Bank;
import com.pacific.restapi.model.Goods;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SaleDto {
    private Long id;
    private String invoiceNo;
    private String proformaInvoiceNo;
    private String lcDays;
    private Integer lcOpenDays;
    private Integer paymentDays;
    private Integer shipmentWithinDays;
    private String lcNo;
    private String challanNo;
    private String exportLcNo;
    private String county;
    private String loadingPlace;
    private String partialShipment;
    private String placeOfDischarge;
    private String truckChallanSerialNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate truckChallanDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate date;
    private String vatNo;
    private String finalDestination;
    private String shipmentMode;
    private String tinNo;
    private String truckNo;
    private String termsOfPayment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate piDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate challanDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate lcDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate exchangeDate;
    private CustomerDto customer;
    private BankDto advisingBank;
    private BankDto lcBank;
    private String lcBranch;
    private String advisingBranch;
    private List<GoodsDto> goodsList;
    private Double totalAmount;
}
