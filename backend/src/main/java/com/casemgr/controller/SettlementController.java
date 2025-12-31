package com.casemgr.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Settlement", description = "Settlement management APIs for handling financial settlements and payment processing between parties.")
@RestController
@RequestMapping("/api/settlements")
@CrossOrigin
public class SettlementController {

}
