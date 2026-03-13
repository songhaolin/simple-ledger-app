import 'package:ledger_app/models/api_response.dart';
import 'package:ledger_app/models/user.dart';
import 'api_service.dart';

class AuthService {
  final ApiService _apiService = ApiService();

  // 用户注册
  Future<ApiResponse<User>> register({
    required String phone,
    required String password,
    String? nickname,
  }) async {
    try {
      final response = await _apiService.post('/users/register', body: {
        'phone': phone,
        'password': password,
        'nickname': nickname,
      });

      final jsonResponse = jsonDecode(response.body);

      // 如果注册成功，保存token
      if (response.statusCode == 200 && jsonResponse['code'] == 1000) {
        final apiResponse = ApiResponse<User>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: User.fromJson(jsonResponse['data']),
        );
        _apiService.setToken(apiResponse.data!.token!);
        return apiResponse;
      }

      return ApiResponse<User>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '注册失败',
      );
    } catch (e) {
      return ApiResponse<User>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }

  // 用户登录
  Future<ApiResponse<User>> login({
    required String phone,
    required String password,
  }) async {
    try {
      final response = await _apiService.post('/users/login', body: {
        'phone': phone,
        'password': password,
      });

      final jsonResponse = jsonDecode(response.body);

      // 如果登录成功，保存token
      if (response.statusCode == 200 && jsonResponse['code'] == 1000) {
        final apiResponse = ApiResponse<User>(
          code: jsonResponse['code'],
          message: jsonResponse['message'],
          data: User.fromJson(jsonResponse['data']),
        );
        _apiService.setToken(apiResponse.data!.token!);
        return apiResponse;
      }

      return ApiResponse<User>(
        code: jsonResponse['code'] ?? -1,
        message: jsonResponse['message'] ?? '登录失败',
      );
    } catch (e) {
      return ApiResponse<User>(
        code: -1,
        message: '网络错误: $e',
      );
    }
  }

  // 登出
  void logout() {
    _apiService.clearToken();
  }
}

import 'dart:convert';
