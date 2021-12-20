package com.pacific.restapi.repository;

import com.pacific.restapi.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    Sale findByIdAndIsActiveTrue(Long id);

    Sale findByProformaInvoiceNoAndIsActiveTrue(String performaInvoice);
}
