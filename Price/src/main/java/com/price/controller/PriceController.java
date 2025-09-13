package com.price.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.price.appresource.AppConst;
import com.price.entity.Price;
import com.price.repository.PriceRepository;
import com.price.request.PriceReq;
import com.price.response.EndResult;
import com.price.service.PriceService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

	private static final Logger log = LoggerFactory.getLogger(PriceController.class);

	@Autowired private PriceRepository priceRepo;
	@Autowired private PriceService priceServ;

	@PostMapping
	public String createPrice(@RequestBody PriceReq req) {
		String finalResp = null;
		EndResult result = null;
		try {
			Price createdPrice = priceServ.createPrice(req);
			result= setEndResult(result,AppConst.SUCCESS, "Price data Saved",AppConst.ERRORCODE_SUCCESS);
			result.setData(createdPrice);
			ObjectMapper mapper = new ObjectMapper();
			finalResp = mapper.writeValueAsString(result);
			log.info("In CreatePrice(), finalResp:"+finalResp);
		} catch (Exception e) {
			result= setEndResult(result,AppConst.FAILURE, "Invalid Price",AppConst.ERRORCODE_EXCEPTION);
			finalResp = new Gson().toJson(result);
			log.error("In CreatePrice(), finalResp:"+finalResp);
			return finalResp;
		}
		return finalResp;
	}

//	@GetMapping
//	@Transactional
//	public String getAllPrices() {
//		String finalResp = null;
//		List<Price> priceList = null;
//		EndResult result = null;
//		try {
//			Sort sort = Sort.by(Sort.Direction.ASC, "id");
//			Pageable pageable = PageRequest.of(0, 2, sort);
//			priceList = (List<Price>) priceRepo.findAll(pageable);
//			result= setEndResult(result,AppConst.SUCCESS, "Price data Recieved",AppConst.ERRORCODE_SUCCESS);
//			result.setData(priceList);
//			ObjectMapper mapper = new ObjectMapper();
//			finalResp = mapper.writeValueAsString(result);
//			log.info("In getAllPrices(), finalResp:"+finalResp);
//		} catch (Exception e) {
//			result= setEndResult(result,AppConst.FAILURE, "Something went wrong!",AppConst.ERRORCODE_EXCEPTION);
//			finalResp = new Gson().toJson(result);
//			log.error("In getAllPrices(), finalResp:"+finalResp);
//			return finalResp;
//		}
//		return finalResp;
//	}

	@GetMapping
	@Transactional
	public String getAllUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		String finalResp;
		EndResult result = null;

		try {
			Sort sort = Sort.by(Sort.Direction.ASC, "id");
			Pageable pageable = PageRequest.of(page, size, sort);

			Page<Price> adminPage = priceRepo.findByStatusNot("D", pageable);

			result = setEndResult(result, AppConst.SUCCESS,"Price data received", AppConst.ERRORCODE_SUCCESS);

			// Include both list and total count for frontend pagination
			Map<String, Object> data = new HashMap<>();
			data.put("data", adminPage.getContent());
			data.put("total", adminPage.getTotalElements());
			result.setData(data);

			ObjectMapper mapper = new ObjectMapper();
			finalResp = mapper.writeValueAsString(result);

			log.info("In getAllPrices(), finalResp: {}", finalResp);

		} catch (Exception e) {
			result = setEndResult(result, AppConst.FAILURE,
					"Something went wrong!", AppConst.ERRORCODE_EXCEPTION);
			finalResp = new Gson().toJson(result);
			log.error("In getAllPrice(), finalResp: {}", finalResp, e);
			return finalResp;
		}
		return finalResp;
	}
	
	@PostMapping("/get")
	@Transactional
	public String getPrice(@RequestBody PriceReq req) {
		String finalResp = null;
		EndResult result = null;
		try {
			Price enq = priceServ.getPrice(req);
			result= setEndResult(result,AppConst.SUCCESS, "Price data Recieved",AppConst.ERRORCODE_SUCCESS);
			result.setData(enq);
			ObjectMapper mapper = new ObjectMapper();
			finalResp = mapper.writeValueAsString(result);
			log.info("In getPrice(), finalResp:"+finalResp);
		} catch (Exception e) {
			result= setEndResult(result,AppConst.FAILURE,e.getMessage(),AppConst.ERRORCODE_EXCEPTION);
			finalResp = new Gson().toJson(result);
			log.error("In getPrice(), finalResp:"+finalResp);
			return finalResp;
		}
		return finalResp;
	}

	@PostMapping("/update")
	public String updatePrice(@RequestBody PriceReq req) {
		String finalResp = null;
		EndResult result = null;
		try {
			Price updatedEnq = priceServ.updatePrice(req);
			result= setEndResult(result,AppConst.SUCCESS, "Price data Updated",AppConst.ERRORCODE_SUCCESS);
			result.setData(updatedEnq);
			ObjectMapper mapper = new ObjectMapper();
			finalResp = mapper.writeValueAsString(result);
			log.info("In updatePrice(), finalResp:"+finalResp);
		} catch (Exception e) {
			result= setEndResult(result,AppConst.FAILURE,e.getMessage(),AppConst.ERRORCODE_EXCEPTION);
			finalResp = new Gson().toJson(result);
			log.error("In updatePrice(), finalResp:"+finalResp);
			return finalResp;
		}
		return finalResp;
	}

	@PostMapping("/delete")
	public String deletePrice(@RequestBody PriceReq req) {
		String finalResp = null;
		EndResult result = null;
		try {
			Price deletedPrice = priceServ.deletePrice(req);
			result= setEndResult(result,AppConst.SUCCESS, "Price data Deleted",AppConst.ERRORCODE_SUCCESS);
			result.setData(deletedPrice);
			ObjectMapper mapper = new ObjectMapper();
			finalResp = mapper.writeValueAsString(result);
			log.info("In deletePrice(), finalResp:"+finalResp);
		} catch (Exception e) {
			result= setEndResult(result,AppConst.FAILURE,e.getMessage(),AppConst.ERRORCODE_EXCEPTION);
			finalResp = new Gson().toJson(result);
			log.error("In deletePrice(), finalResp:"+finalResp);
			return finalResp;
		}
		return finalResp;
	}

	public EndResult setEndResult(EndResult result,String status, String message, String errorCode) {
		if(result==null) {
			result = new EndResult();
		}
		result.setMessage(message);
		result.setStatus(status);
		result.setErrorCode(errorCode);
		return result;
	}
}
