/* (C) 2026 
bidder.app */
package com.bidder.service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bidder.service.utils.Constants.Controller.BASE_URI;
import static com.bidder.service.utils.Constants.Controller.V1;

@RestController
@RequestMapping(BASE_URI + V1 + "/message")
@RequiredArgsConstructor
public class MessageController {
	private final NotificationService messageService;

	// @PostMapping("/send")
	// public ResponseEntity<ApiResponse<NotificationRequest>> sendMessage() {
	// return
	// }
}
