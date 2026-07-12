package com.news.controller;

import com.news.dto.Result;
import com.news.service.QuotesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuotesController {

    private final QuotesService quotesService;

    public QuotesController(QuotesService quotesService) {
        this.quotesService = quotesService;
    }

    @GetMapping
    public Result<?> getQuotes(@RequestParam(defaultValue = "1") int pageNum) {
        return Result.success(quotesService.getQuotesList(pageNum));
    }

    @GetMapping("/list")
    public Result<?> getQuotesList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(quotesService.getQuotesListPage(page, size, keyword));
    }
}
