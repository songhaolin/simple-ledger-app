import 'dart:convert';
import 'package:ledger_app/models/api_response.dart';
import 'package:ledger_app/models/ledger.dart';
import 'api_service.dart';

class LedgerService {
  final ApiService _apiService = ApiService();

  // 获取账本列表
  Future<ApiResponse<List<Ledger>>> getLedgers() async {
    try {
      final response = await _apiService.get('/ledgers');

      if (response.statusCode != 200) {
        return ApiResponse<List<Ledger>>(
          code: -1,
          message: '服务器错误 (${response.statusCode})',
        );
      }

      dynamic jsonResponse;
      try {
        jsonResponse = jsonDecode(response.body);
      } catch (e) {
        return ApiResponse<List<Ledger>>(
          code: -1,
          message: '响应解析失败: $e',
        );
      }

      if (jsonResponse['code'] == 1000 && jsonResponse['data'] != null) {
        final ledgers = (jsonResponse['data'] as List)
            .map((e) => Ledger.fromJson(e))
            .toList();
        return ApiResponse<List<Ledger>>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: ledgers,
        );
      }

      return ApiResponse<List<Ledger>>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '获取账本失败',
      );
    } catch (e) {
      return ApiResponse<List<Ledger>>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }

  // 创建账本
  Future<ApiResponse<Map<String, dynamic>>> createLedger({
    required String name,
    required String type,
  }) async {
    try {
      final response = await _apiService.post('/ledgers', body: {
        'name': name,
        'type': type,
      });

      if (response.statusCode != 200) {
        return ApiResponse<Map<String, dynamic>>(
          code: -1,
          message: '服务器错误 (${response.statusCode})',
        );
      }

      dynamic jsonResponse;
      try {
        jsonResponse = jsonDecode(response.body);
      } catch (e) {
        return ApiResponse<Map<String, dynamic>>(
          code: -1,
          message: '响应解析失败: $e',
        );
      }

      if (jsonResponse['code'] == 1000) {
        return ApiResponse<Map<String, dynamic>>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: jsonResponse['data'],
        );
      }

      return ApiResponse<Map<String, dynamic>>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '创建账本失败',
      );
    } catch (e) {
      return ApiResponse<Map<String, dynamic>>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }
}
