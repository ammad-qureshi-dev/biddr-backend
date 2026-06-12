/* (C) 2026 
bidder.app */
package com.bidder.service.controllers;

import java.util.List;

import com.bidder.service.models.response.ApiResponse;
import com.bidder.service.models.response.Message;
import com.bidder.service.models.response.MessageCode;
import com.bidder.service.models.response.MessageType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bidder.service.utils.Constants.Controller.BASE_URI;
import static com.bidder.service.utils.Constants.Controller.V1;

@RestController
@RequestMapping(BASE_URI + V1 + "/health")
public class HealthController {

	@GetMapping("/ping")
	public ResponseEntity<ApiResponse<String>> ping() {
		return ResponseEntity.ok()
				.body(ApiResponse.<String>builder().data("pong")
						.messages(
								List.of(Message.generateMessage(MessageType.INFO, MessageCode.SERVICE_RESPONSE_CHECK)))
						.build());
	}

}
