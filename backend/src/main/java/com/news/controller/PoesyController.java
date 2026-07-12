package com.news.controller;

import com.news.dto.Result;
import com.news.service.PoesyService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/poesy")
public class PoesyController {

    private final PoesyService poesyService;

    public PoesyController(PoesyService poesyService) {
        this.poesyService = poesyService;
    }

    @GetMapping
    public Result<?> getPoesy(@RequestParam(defaultValue = "all") String keyword,
                               @RequestParam(required = false) Integer id,
                               @RequestParam(defaultValue = "1") int pageNum) {
        if ("id".equals(keyword) && id != null) {
            Map<String, Object> data = poesyService.getPoesyById(id);
            if (data == null) {
                return Result.error("诗词不存在");
            }
            return Result.success(data);
        }

        boolean recommended = "tuijian".equals(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("pageNum", pageNum);
        result.put("list", poesyService.getPoesyList(pageNum, recommended));
        result.put("totalPages", poesyService.getTotalPages());
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<?> getPoesyList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(required = false) String writer,
            @RequestParam(required = false) String keyword) {
        if (keyword != null && ("all".equals(keyword) || "tuijian".equals(keyword))) {
            type = keyword;
            keyword = null;
        }
        boolean recommended = "tuijian".equals(type);
        return Result.success(poesyService.getPoesyListPage(page, size, recommended, keyword, writer));
    }

    @GetMapping("/{id}")
    public Result<?> getPoesyById(@PathVariable Integer id) {
        Map<String, Object> data = poesyService.getPoesyById(id);
        if (data == null) {
            return Result.error("诗词不存在");
        }
        return Result.success(data);
    }
}
