import 'package:flutter/foundation.dart';
import 'package:ledger_app/models/ledger.dart';
import 'package:ledger_app/models/transaction.dart';
import 'package:ledger_app/services/transaction_service.dart';

class TransactionProvider with ChangeNotifier {
  final TransactionService _transactionService = TransactionService();

  // 当前选中的账本
  Ledger? _currentLedger;

  // 账单列表
  List<Transaction> _transactions = [];
  bool _isLoading = false;
  String _errorMessage = '';

  // 统计数据
  Map<String, dynamic> _statistics = {};

  // Getters
  Ledger? get currentLedger => _currentLedger;
  List<Transaction> get transactions => _transactions;
  bool get isLoading => _isLoading;
  String get errorMessage => _errorMessage;
  Map<String, dynamic> get statistics => _statistics;

  // 设置当前账本
  void setCurrentLedger(Ledger? ledger) {
    _currentLedger = ledger;
    notifyListeners();
    if (ledger != null) {
      loadTransactions();
    }
  }

  // 设置账单列表（用于个人模式）
  void setTransactions(List<Transaction> transactions) {
    _transactions = transactions;
    _errorMessage = '';
    _isLoading = false;
    notifyListeners();
  }

  // 加载账单列表
  Future<void> loadTransactions() async {
    if (_currentLedger == null) return;

    _isLoading = true;
    _errorMessage = '';
    notifyListeners();

    try {
      final response = await _transactionService.getTransactions(
        ledgerId: _currentLedger!.id,
        page: 1,
        limit: 100,
      );

      if (response.isSuccess && response.data != null) {
        _transactions = (response.data!['transactions'] as List)
            .map((e) => Transaction.fromJson(e))
            .toList();
        _errorMessage = '';
      } else {
        _errorMessage = response.message;
        _transactions = [];
      }
    } catch (e) {
      _errorMessage = '加载失败: $e';
      _transactions = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // 添加账单
  Future<bool> addTransaction(Transaction transaction) async {
    try {
      final response = await _transactionService.createTransaction(
        ledgerId: _currentLedger!.id,
        categoryId: transaction.categoryId,
        type: transaction.type,
        amount: transaction.amount,
        note: transaction.note,
        date: transaction.date,
      );

      if (response.isSuccess && response.data != null) {
        _transactions.insert(0, response.data!);
        notifyListeners();
        return true;
      } else {
        _errorMessage = response.message;
        notifyListeners();
        return false;
      }
    } catch (e) {
      _errorMessage = '添加失败: $e';
      notifyListeners();
      return false;
    }
  }

  // 更新账单
  Future<bool> updateTransaction(Transaction transaction) async {
    try {
      final response = await _transactionService.updateTransaction(
        transactionId: transaction.id,
        categoryId: transaction.categoryId,
        type: transaction.type,
        amount: transaction.amount,
        note: transaction.note,
        date: transaction.date,
      );

      if (response.isSuccess && response.data != null) {
        final index = _transactions.indexWhere((t) => t.id == transaction.id);
        if (index != -1) {
          _transactions[index] = response.data!;
          notifyListeners();
        }
        return true;
      } else {
        _errorMessage = response.message;
        notifyListeners();
        return false;
      }
    } catch (e) {
      _errorMessage = '更新失败: $e';
      notifyListeners();
      return false;
    }
  }

  // 删除账单
  Future<bool> deleteTransaction(String transactionId) async {
    try {
      final response = await _transactionService.deleteTransaction(transactionId);

      if (response.isSuccess) {
        _transactions.removeWhere((t) => t.id == transactionId);
        notifyListeners();
        return true;
      } else {
        _errorMessage = response.message;
        notifyListeners();
        return false;
      }
    } catch (e) {
      _errorMessage = '删除失败: $e';
      notifyListeners();
      return false;
    }
  }

  // 加载统计数据
  Future<void> loadStatistics() async {
    if (_currentLedger == null) return;

    try {
      final response = await _transactionService.getStatistics(
        ledgerId: _currentLedger!.id,
      );

      if (response.isSuccess && response.data != null) {
        _statistics = response.data!;
        notifyListeners();
      }
    } catch (e) {
      _errorMessage = '加载统计失败: $e';
      notifyListeners();
    }
  }

  // 清除错误
  void clearError() {
    _errorMessage = '';
    notifyListeners();
  }
}
