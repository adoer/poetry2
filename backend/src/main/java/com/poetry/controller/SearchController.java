package com.poetry.controller;

import com.poetry.dto.Result;
import com.poetry.service.SearchService;
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
