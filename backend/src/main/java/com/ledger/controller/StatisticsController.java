package com.ledger.controller;

import com.ledger.dto.StatisticsSummary;
import com.ledger.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 收支统计控制器
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * 获取收支统计
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<StatisticsSummary>> getStatisticsSummary(
            @RequestParam(required = true) String ledgerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 解析日期参数
        Date start = null;
        Date end = null;

        if (startDate != null && !startDate.isEmpty()) {
            try {
                start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            } catch (ParseException e) {
                throw new IllegalArgumentException("startDate格式错误，应为yyyy-MM-dd");
            }
        }

        if (endDate != null && !endDate.isEmpty()) {
            try {
                end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            } catch (ParseException e) {
                throw new IllegalArgumentException("endDate格式错误，应为yyyy-MM-dd");
            }
        }

        // 获取统计数据
        StatisticsSummary summary = statisticsService.getStatistics(ledgerId, start, end);

        return ApiResponse.success(summary);
    }
}
