package com.casemgr.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.entity.User;
import com.casemgr.repository.UserRepository;
import com.casemgr.response.FeeCodeResponse;
import com.casemgr.service.FeeCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Admin FeeCode 管理控制器
 * 提供建立與查詢費用代碼之 API
 */
@Tag(name = "Admin FeeCode Management", description = "Administrative APIs for creating and listing fee codes")
@RestController
@RequestMapping("/api/admin/feecodes")
@RequiredArgsConstructor
@Slf4j
public class AdminFeeCodeController {

    private final FeeCodeService feeCodeService;
    private final UserRepository userRepository;

    /**
     * 建立費用代碼
     * 權限：ROLE_CERTIFICATION_MANAGE
     * @param code 代碼字串
     * @param principal 目前登入者（用於設定 creator）
     * @return 201 已建立；409 已存在；401/403 權限不足
     */
    @Secured("ROLE_CERTIFICATION_MANAGE")
    @PostMapping("/{code}")
    @Operation(summary = "Create FeeCode", description = "Create a new unique fee code")
    public ResponseEntity<FeeCodeResponse> createFeeCode(
            @Parameter(description = "Unique fee code value", required = true) @PathVariable String code,
            Principal principal) {
        try {
            String username = principal != null ? principal.getName() : null;
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User creator = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Creator not found"));

            FeeCodeResponse resp = feeCodeService.create(code, creator);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalStateException e) {
            log.warn("Create FeeCode conflict: {}", code);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Create FeeCode failed: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 取得所有費用代碼
     * 權限：ROLE_CERTIFICATION_MANAGE
     */
    @Secured("ROLE_CERTIFICATION_MANAGE")
    @GetMapping
    @Operation(summary = "List FeeCodes", description = "List all fee codes")
    public ResponseEntity<List<FeeCodeResponse>> listFeeCodes() {
        List<FeeCodeResponse> list = feeCodeService.listAll();
        return ResponseEntity.ok(list);
    }
}

