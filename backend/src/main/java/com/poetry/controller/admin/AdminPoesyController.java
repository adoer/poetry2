package com.poetry.controller.admin;

import com.poetry.dto.Result;
import com.poetry.entity.Poesy;
import com.poetry.exception.ResourceNotFoundException;
import com.poetry.service.AdminPoesyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/poesy")
public class AdminPoesyController {

    private final AdminPoesyService adminPoesyService;

    public AdminPoesyController(AdminPoesyService adminPoesyService) {
        this.adminPoesyService = adminPoesyService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) String keyword) {
        return Result.success(adminPoesyService.getPoesyList(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id) {
        var poesy = adminPoesyService.getPoesyById(id);
        if (poesy == null) throw new ResourceNotFoundException("诗词不存在");
        return Result.success(poesy);
    }

    @PostMapping
    public Result<?> create(@RequestBody Poesy poesy) {
        var created = adminPoesyService.createPoesy(poesy);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Poesy poesy) {
        var updated = adminPoesyService.updatePoesy(id, poesy);
        if (updated == null) throw new ResourceNotFoundException("诗词不存在");
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        var existing = adminPoesyService.getPoesyById(id);
        if (existing == null) throw new ResourceNotFoundException("诗词不存在");
        adminPoesyService.deletePoesy(id);
        return Result.success();
    }
}
