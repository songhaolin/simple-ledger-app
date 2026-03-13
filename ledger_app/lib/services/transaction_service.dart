import 'package:ledger_app/models/api_response.dart';
import 'package:ledger_app/models/transaction.dart';
import 'package:ledger_app/models/ledger.dart';
import 'api_service.dart';
import 'dart:convert';

class TransactionService {
  final ApiService _apiService = ApiService();

  // 获取账本列表
  Future<ApiResponse<List<Ledger>>> getLedgers() async {
    try {
      final response = await _apiService.get('/ledgers');
      final jsonResponse = jsonDecode(response.body);

      if (response.statusCode == 200 && jsonResponse['code'] == 1000) {
        final List<dynamic> data = jsonResponse['data'] ?? [];
        final ledgers = data.map((e) => Ledger.fromJson(e)).toList();
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
  Future<ApiResponse<Ledger>> createLedger({
    required String name,
    String? icon,
    required int type,
  }) async {
    try {
      final response = await _apiService.postAuth('/ledgers', body: {
        'name': name,
        'icon': icon,
        'type': type,
      });
      final jsonResponse = jsonDecode(response.body);

      if (response.statusCode == 200 && jsonResponse['code'] == 1000) {
        return ApiResponse<Ledger>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: Ledger.fromJson(jsonResponse['data']),
        );
      }

      return ApiResponse<Ledger>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '创建账本失败',
      );
    } catch (e) {
      return ApiResponse<Ledger>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }

  // 获取账单列表
  Future<ApiResponse<Map<String, dynamic>>> getTransactions({
    required String ledgerId,
    int page = 1,
    int limit = 20,
  }) async {
    try {
      final response = await _apiService.get(
        '/transactions?ledgerId=$ledgerId&page=$page&limit=$limit',
      );
      final jsonResponse = jsonDecode(response.body);

      if (response.statusCode == 200 && jsonResponse['code'] == 1000) {
        return ApiResponse<Map<String, dynamic>>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: jsonResponse['data'],
        );
      }

      return ApiResponse<Map<String, dynamic>>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '获取账单失败',
      );
    } catch (e) {
      return ApiResponse<Map<String, dynamic>>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }

  // 创建账单
  Future<ApiResponse<Transaction>> createTransaction({
    required String ledgerId,
    required String categoryId,
    required int type,
    required double amount,
    String? note,
    DateTime? date,
  }) async {
    try {
      final response = await _apiService.postAuth('/transactions', body: {
        'ledgerId': ledgerId,
        'categoryId': categoryId,
        'type': type,
        'amount': amount,
        'note': note,
        'date': date?.toIso8601String() ?? DateTime.now().toIso8601String(),
      });
      final jsonResponse = jsonDecode(response.body);

      if (response.statusCode == 200 && jsonResponse['code'] == 1000) {
        return ApiResponse<Transaction>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: Transaction.fromJson(jsonResponse['data']),
        );
      }

      return ApiResponse<Transaction>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '创建账单失败',
      );
    } catch (e) {
      return ApiResponse<Transaction>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }

  // 更新账单
  Future<ApiResponse<Transaction>> updateTransaction({
    required String transactionId,
    required String categoryId,
    required int type,
    required double amount,
    String? note,
    DateTime? date,
  }) async {
    try {
      final response = await _apiService.put('/transactions/$transactionId', body: {
        'categoryId': categoryId,
        'type': type,
        'amount': amount,
        'note': note,
        'date': date?.toIso8601String(),
      });
      final jsonResponse = jsonDecode(response.body);

      if (response.statusCode == 200 && jsonResponse['code'] == 1000) {
        return ApiResponse<Transaction>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: Transaction.fromJson(jsonResponse['data']),
        );
      }

      return ApiResponse<Transaction>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '更新账单失败',
      );
    } catch (e) {
      return ApiResponse<Transaction>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }

  // 删除账单
  Future<ApiResponse<void>> deleteTransaction(String transactionId) async {
    try {
      final response = await _apiService.delete('/transactions/$transactionId');
      final jsonResponse = jsonDecode(response.body);

      return ApiResponse<void>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '删除账单失败',
      );
    } catch (e) {
      return ApiResponse<void>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }

  // 获取统计数据
  Future<ApiResponse<Map<String, dynamic>>> getStatistics({
    required String ledgerId,
    String? startDate,
    String? endDate,
  }) async {
    try {
      String queryParams = '?ledgerId=$ledgerId';
      if (startDate != null) {
        queryParams += '&startDate=$startDate';
      }
      if (endDate != null) {
        queryParams += '&endDate=$endDate';
      }

      final response = await _apiService.get('/statistics/summary$queryParams');
      final jsonResponse = jsonDecode(response.body);

      if (response.statusCode == 200 && jsonResponse['code'] == 1000) {
        return ApiResponse<Map<String, dynamic>>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: jsonResponse['data'],
        );
      }

      return ApiResponse<Map<String, dynamic>>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '获取统计失败',
      );
    } catch (e) {
      return ApiResponse<Map<String, dynamic>>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }
}
