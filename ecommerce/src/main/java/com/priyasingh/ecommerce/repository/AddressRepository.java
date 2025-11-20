package com.priyasingh.ecommerce.repository;

import com.priyasingh.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
