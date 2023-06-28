package org.apache.fineract.portfolio.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientBusinessDetailRepository
        extends JpaRepository<ClientBusinessDetail, Long>, JpaSpecificationExecutor<ClientBusinessDetail> {

}
