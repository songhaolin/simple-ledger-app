package com.ledger.controller;

import com.ledger.exception.BusinessException;
import com.ledger.exception.BusinessException.ErrorCodes;
import com.ledger.service.ImportService;
import com.ledger.service.ImportService.ImportResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

/**
 * CSV导入控制器
 */
@RestController
@RequestMapping("/import")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    /**
     * 导入CSV账单数据
     */
    @PostMapping("/csv")
    public ResponseEntity<ApiResponse<ImportResult>> importCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ledgerId") String ledgerId,
            HttpServletRequest httpRequest) {
        // 1. 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "文件不能为空"
            );
        }

        // 2. 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("text/csv")) {
            // 检查文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".csv")) {
                throw new BusinessException(
                        ErrorCodes.INVALID_PARAM,
                        "只支持CSV文件"
                );
            }
        }

        // 3. 验证文件大小（限制5MB）
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "文件大小不能超过5MB"
            );
        }

        // 4. 验证ledgerId
        if (ledgerId == null || ledgerId.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "账本ID不能为空"
            );
        }

        // 5. 从请求属性中获取UserId（JWT拦截器已验证）
        String userId = (String) httpRequest.getAttribute("userId");

        // 6. 导入CSV
        ImportResult result = importService.importFromCsv(
                file,
                ledgerId,
                userId
        );

        // 7. 返回结果
        return ApiResponse.success(result);
    }
}
