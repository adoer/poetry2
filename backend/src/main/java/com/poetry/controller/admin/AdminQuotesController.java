package com.poetry.controller.admin;

import com.poetry.dto.Result;
import com.poetry.entity.Quotes;
import com.poetry.exception.ResourceNotFoundException;
import com.poetry.service.AdminQuotesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/quotes")
public class AdminQuotesController {

    private final AdminQuotesService adminQuotesService;

    public AdminQuotesController(AdminQuotesService adminQuotesService) {
        this.adminQuotesService = adminQuotesService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) String keyword) {
        return Result.success(adminQuotesService.getQuotesList(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id) {
        var quotes = adminQuotesService.getQuotesById(id);
        if (quotes == null) throw new ResourceNotFoundException("名句不存在");
        return Result.success(quotes);
    }

    @PostMapping
    public Result<?> create(@RequestBody Quotes quotes) {
        var created = adminQuotesService.createQuotes(quotes);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Quotes quotes) {
        var updated = adminQuotesService.updateQuotes(id, quotes);
        if (updated == null) throw new ResourceNotFoundException("名句不存在");
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        var existing = adminQuotesService.getQuotesById(id);
        if (existing == null) throw new ResourceNotFoundException("名句不存在");
        adminQuotesService.deleteQuotes(id);
        return Result.success();
    }
}
