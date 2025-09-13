package com.price.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.price.entity.Price;

public interface PriceRepository extends CrudRepository<Price, Long> {

	List<Price> findAll(Pageable pageable);

	Page<Price> findByStatusNot(String string, Pageable pageable);

	
}
