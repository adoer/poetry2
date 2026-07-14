package com.poetry.controller.admin;

import com.poetry.dto.Result;
import com.poetry.service.AdminStatsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping("/overview")
    public Result<?> overview() {
        return Result.success(adminStatsService.getOverview());
    }

    @GetMapping("/registration")
    public Result<?> registration() {
        return Result.success(adminStatsService.getRegistrationTrend());
    }

    @GetMapping("/favorites")
    public Result<?> favorites() {
        return Result.success(adminStatsService.getFavoriteDistribution());
    }
}
