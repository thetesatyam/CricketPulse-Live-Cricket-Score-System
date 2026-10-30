package com.cricketpulse.controller;

import com.cricketpulse.dto.ApiResponse;
import com.cricketpulse.dto.UpdateRoleRequest;
import com.cricketpulse.dto.UserDto;
import com.cricketpulse.entity.AuditLog;
import com.cricketpulse.service.AuditService;
import com.cricketpulse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administration", description = "User management, role assignment, and audit logs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AuditService auditService;

    public AdminController(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping("/users")
    @Operation(summary = "List all users in the system")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers()));
    }

    @PatchMapping("/users/{userId}/role")
    @Operation(summary = "Promote or change user role")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRoleRequest request) {

        UserDto updated = userService.updateRole(userId, request.getRole());
        return ResponseEntity.ok(ApiResponse.ok("User role updated successfully", updated));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "View system audit trail")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.ok(auditService.getRecentLogs()));
    }
}
