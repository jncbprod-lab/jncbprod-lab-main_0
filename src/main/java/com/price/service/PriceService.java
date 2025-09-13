package com.price.service;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.price.entity.Price;
import com.price.entity.PriceAuditTrial;
import com.price.repository.PriceAuditTrialRepository;
import com.price.repository.PriceRepository;
import com.price.request.PriceReq;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Service
public class PriceService {

	@Autowired private PriceRepository priceRepo;
	@Autowired private PriceAuditTrialRepository priceATRepo;

	//Checking the dbPrice by Id given by RequestBody
	public Price dbPrice(PriceReq req) {
		Price existing = null;
		if(req.getId()	!= null) {
			existing = priceRepo.findById(req.getId()).orElseThrow(() -> new RuntimeException("Enquiry not found with id: " + req.getId()));
		}
		return existing;
	}
	
	
	public Price createPrice(PriceReq req)  {
		Price price = new Price();
		BeanUtils.copyProperties(req, price);
		
		HttpResponse<String> response = Unirest.post("http://localhost:8080/api/master/map/" + req.getProduct())
                .header("Content-Type", "application/json")
                .body("{\"id\":" + req.getProduct() + "}")
                .asString();

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to fetch product map with ID " + req.getProduct() + ". Status: " + response.getStatus());
        }

        String productJson = response.getBody();
        
        price.setProduct(productJson);
        price.setStatus("N");
        price.setCreatedAt(new Date());
		
		Price price2 = priceRepo.save(price);
		return price2;
	}

	//Get Price by dbPrice by Id
	public Price getPrice(PriceReq req) {
		Price existing = dbPrice(req);
		if("D".equals(existing.getStatus())) {
			throw new RuntimeException("Price data not found with Id:"+req.getId());
		}else {
		//existing.getProduct();
		return existing;
		}
	}

	//updating Price
	public Price updatePrice(PriceReq req) throws JsonProcessingException {
		Price existing = dbPrice(req); 
		if("D".equals(existing.getStatus())) {
			throw new RuntimeException("Price data not found with Id:"+req.getId());
		}else {
		savePriceAT(existing);

		if (req.getId() != null) existing.setId(req.getId());
		if (req.getIsActive() != null) existing.setIsActive(req.getIsActive());
		if (req.getAmount() != null) existing.setAmount(req.getAmount());
		if (req.getCreatedAt() != null) existing.setCreatedAt(req.getCreatedAt());
		if (req.getCurrency() != null) existing.setCurrency(req.getCurrency());
		if (req.getEndDate() != null) existing.setEndDate(req.getEndDate());
		if (req.getPriceType() != null) existing.setPriceType(req.getPriceType());
		if (req.getStartDate() != null) existing.setStartDate(req.getStartDate());
		if (req.getUpdatedAt() != null) existing.setUpdatedAt(req.getUpdatedAt());
		
//		String productUrl = PRODUCT_API_BASE + "/" + req.getProduct();
//        ProductDTO product = restTemplate.getForObject(productUrl, ProductDTO.class);
//        
//        ObjectMapper mapper = new ObjectMapper();
//        String ProductJson = mapper.writeValueAsString(product);
        
//        existing.setProduct(ProductJson);
		if(req.getProduct() != null) {
		HttpResponse<String> response = Unirest.post("http://localhost:8080/api/master/map/" + req.getProduct())
                .header("Content-Type", "application/json")
                .body("{\"id\":" + req.getProduct() + "}")
                .asString();

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to fetch product map with ID " + req.getProduct() + ". Status: " + response.getStatus());
        }

        String productJson = response.getBody();
        
        existing.setProduct(productJson);
		
		}
        existing.setStatus("U");
        existing.setUpdatedAt(new Date());
		Price updatedPrice = priceRepo.save(existing);

		return updatedPrice;
		}
	}

	//Deleting Enquiry
	public Price deletePrice(PriceReq req) {
		Price existing = dbPrice(req);
		if("D".equals(existing.getStatus())) {
			throw new RuntimeException("Price data not found with Id:"+req.getId());
		}else {
		savePriceAT(existing);
		existing.setStatus("D");
		existing.setUpdatedAt(new Date());
		Price deletedPrice = priceRepo.save(existing);
		return deletedPrice;
		}

	}

	private void savePriceAT(Price existing) {
		PriceAuditTrial priceAT = new PriceAuditTrial();
		BeanUtils.copyProperties(existing, priceAT);
		priceATRepo.save(priceAT);
	}
	
}
