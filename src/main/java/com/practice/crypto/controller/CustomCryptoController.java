package com.practice.crypto.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.practice.crypto.service.CryptoService;

@RestController
public class CustomCryptoController {

	@Autowired
	private CryptoService cryptoService;

	@PostMapping(value = "/crypto/v1/enc-dec", produces = "application/json")
	public ResponseEntity<String> testExceptionWithCustomData(
			@RequestParam(value = "cryptoType", required = true) String cryptoType,
			@RequestParam(value = "cryptoInput", required = true) String cryptoInput,
			@RequestParam(value = "trackingId", required = false) String trackingId) {

		if (StringUtils.isEmpty(trackingId)) {
			trackingId = "TEST-".concat(UUID.randomUUID().toString());
		}

		// Purposely not sanitizing input data as 'required' flag set to 'true'
		return new ResponseEntity<>(cryptoService.getCryptoData(cryptoType, cryptoInput, trackingId), HttpStatus.OK);
	}
}
