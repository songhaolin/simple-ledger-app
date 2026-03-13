package com.ledger.controller;

import com.ledger.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据导出控制器
 */
@RestController
@RequestMapping("/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * 导出CSV
     */
    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
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

        // 导出CSV数据
        byte[] csvData = exportService.exportCsv(ledgerId, start, end);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv;charset=UTF-8"));

        SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd");
        String fileName = "账单导出_" + fileNameFormat.format(new Date()) + ".csv";

        headers.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }
}
