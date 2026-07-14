package com.poetry.controller.admin;

import com.poetry.dto.Result;
import com.poetry.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) String keyword) {
        return Result.success(adminUserService.getUserList(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable String id) {
        var user = adminUserService.getUserById(id);
        if (user == null) return Result.error("用户不存在");
        return Result.success(user);
    }
}
