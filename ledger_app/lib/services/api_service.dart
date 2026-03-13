import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  // 生产环境API地址
  static const String baseUrl = 'http://43.156.13.236:8080/api/v1';

  // 开发环境（模拟器）使用: static const String baseUrl = 'http://10.0.2.2:8080/api/v1';

  String? _token;

  // 设置JWT Token
  void setToken(String token) {
    _token = token;
  }

  // 清除Token
  void clearToken() {
    _token = null;
  }

  // 获取请求头
  Map<String, String> _getHeaders({bool needAuth = true}) {
    final headers = <String, String>{
      'Content-Type': 'application/json',
    };

    if (needAuth && _token != null) {
      headers['Authorization'] = 'Bearer $_token';
    }

    return headers;
  }

  // GET请求
  Future<http.Response> get(String path) async {
    final url = Uri.parse('$baseUrl$path');
    return await http.get(url, headers: _getHeaders());
  }

  // POST请求
  Future<http.Response> post(String path, {dynamic body}) async {
    final url = Uri.parse('$baseUrl$path');
    return await http.post(
      url,
      headers: _getHeaders(needAuth: false),
      body: body != null ? jsonEncode(body) : null,
    );
  }

  // POST请求（需要认证）
  Future<http.Response> postAuth(String path, {dynamic body}) async {
    final url = Uri.parse('$baseUrl$path');
    return await http.post(
      url,
      headers: _getHeaders(),
      body: body != null ? jsonEncode(body) : null,
    );
  }

  // PUT请求
  Future<http.Response> put(String path, {dynamic body}) async {
    final url = Uri.parse('$baseUrl$path');
    return await http.put(
      url,
      headers: _getHeaders(),
      body: body != null ? jsonEncode(body) : null,
    );
  }

  // DELETE请求
  Future<http.Response> delete(String path) async {
    final url = Uri.parse('$baseUrl$path');
    return await http.delete(url, headers: _getHeaders());
  }
}
