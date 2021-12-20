package com.pacific.restapi.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity(name = "sale")
public class Sale extends BaseModel {
    @Column(name = "invoice_no")
    private String invoiceNo;
    @Column(name = "proforma_invoice_no")
    private String proformaInvoiceNo;
    @Column(name = "lc_days")
    private String lcDays;
    @Column(name = "lc_open_days")
    private Integer lcOpenDays;
    @Column(name = "payment_days")
    private Integer paymentDays;
    @Column(name = "shipment_within_days")
    private Integer shipmentWithinDays;
    @Column(name = "lc_no")
    private String lcNo;
    @Column(name = "challan_no")
    private String challanNo;
    @Column(name = "export_lc_no")
    private String exportLcNo;
    private String county;
    @Column(name = "loading_place")
    private String loadingPlace;
    @Column(name = "partial_shipment")
    private String partialShipment;
    @Column(name = "place_of_discharge")
    private String placeOfDischarge;
    @Column(name = "truck_challan_serial_no")
    private String truckChallanSerialNo;
    @Column(name = "truck_challan_date")
    private LocalDate truckChallanDate;
    private LocalDate date;
    @Column(name = "vat_no")
    private String vatNo;
    @Column(name = "final_destination")
    private String finalDestination;
    @Column(name = "shipment_mode")
    private String shipmentMode;
    @Column(name = "tin_no")
    private String tinNo;
    @Column(name = "truck_no")
    private String truckNo;
    @Column(name = "terms_of_payment")
    private String termsOfPayment;
    @Column(name = "pi_date")
    private LocalDate piDate;
    @Column(name = "challan_date")
    private LocalDate challanDate;
    @Column(name = "lc_date")
    private LocalDate lcDate;
    @Column(name = "exchange_date")
    private LocalDate exchangeDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "advising_bank_id", referencedColumnName = "id")
    private Bank advisingBank;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lc_bank_id", referencedColumnName = "id")
    private Bank lcBank;
    @Column(name = "lc_branch")
    private String lcBranch;
    @Column(name = "advising_branch")
    private String advisingBranch;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sales_good",
            joinColumns = {@JoinColumn(name = "sale_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "goods_id", referencedColumnName = "id")}
    )
    @ToString.Exclude
    private List<Goods> goodsList;
    @Column(name = "total_amount")
    private Double totalAmount;
}
