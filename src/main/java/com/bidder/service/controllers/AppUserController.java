/* (C) 2026 
bidder.app */
package com.bidder.service.controllers;

import com.bidder.service.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bidder.service.utils.Constants.Controller.BASE_URI;
import static com.bidder.service.utils.Constants.Controller.V1;

@RestController
@RequestMapping(BASE_URI + V1 + "/app-user")
@RequiredArgsConstructor
@Slf4j
public class AppUserController {

	private final AppUserService appUserService;

}
