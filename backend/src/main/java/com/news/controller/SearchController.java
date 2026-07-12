package com.news.controller;

import com.news.dto.Result;
import com.news.service.SearchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/searchAll")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public Result<?> searchAll(@RequestParam String keyword) {
        return Result.success(searchService.searchAll(keyword));
    }
}
