import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:ledger_app/models/transaction.dart';
import 'package:ledger_app/models/ledger.dart';
import 'package:ledger_app/providers/transaction_provider.dart';
import 'package:ledger_app/services/transaction_service.dart';
import 'package:provider/provider.dart';

class TransactionListScreen extends StatefulWidget {
  final bool isPersonalMode;
  final Ledger? selectedLedger;

  const TransactionListScreen({
    super.key,
    this.isPersonalMode = false,
    this.selectedLedger,
  });

  @override
  State<TransactionListScreen> createState() => _TransactionListScreenState();
}

class _TransactionListScreenState extends State<TransactionListScreen> {
  final TransactionService _transactionService = TransactionService();

  @override
  void initState() {
    super.initState();
    if (widget.isPersonalMode) {
      _loadPersonalTransactions();
    } else if (widget.selectedLedger != null) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        context.read<TransactionProvider>().setCurrentLedger(widget.selectedLedger!);
      });
    }
  }

  Future<void> _loadPersonalTransactions() async {
    try {
      final response = await _transactionService.getTransactions(
        ledgerId: null,  // 个人模式：ledgerId 为 null
        page: 1,
        limit: 100,
      );
      if (response.isSuccess && response.data != null) {
        context.read<TransactionProvider>().setTransactions(
          (response.data!['transactions'] as List)
              .map((e) => Transaction.fromJson(e))
              .toList(),
        );
      } else {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(response.message)),
          );
        }
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('加载失败: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.isPersonalMode ? '我的账单' : '账单列表'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: widget.isPersonalMode ? _loadPersonalTransactions : () {
              context.read<TransactionProvider>().loadTransactions();
            },
          ),
        ],
      ),
      body: Consumer<TransactionProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (provider.errorMessage.isNotEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 64, color: Colors.red),
                  const SizedBox(height: 16),
                  Text(
                    provider.errorMessage,
                    style: const TextStyle(color: Colors.red, fontSize: 16),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () {
                      provider.clearError();
                      if (widget.isPersonalMode) {
                        _loadPersonalTransactions();
                      } else {
                        provider.loadTransactions();
                      }
                    },
                    child: const Text('重试'),
                  ),
                ],
              ),
            );
          }

          if (provider.transactions.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.receipt_long,
                    size: 64,
                    color: Colors.grey.shade300,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    '暂无账单记录',
                    style: TextStyle(
                      fontSize: 16,
                      color: Colors.grey.shade600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    widget.isPersonalMode
                        ? '开始记录第一笔账单吧'
                        : '账本还没有账单记录',
                    style: TextStyle(
                      fontSize: 14,
                      color: Colors.grey.shade400,
                    ),
                  ),
                ],
              ),
            );
          }

          return ListView.builder(
            padding: const EdgeInsets.symmetric(vertical: 8),
            itemCount: provider.transactions.length,
            itemBuilder: (context, index) {
              final transaction = provider.transactions[index];
              return _TransactionItem(
                transaction: transaction,
                onTap: () {
                  Navigator.of(context).pushNamed(
                    '/edit-transaction',
                    arguments: transaction,
                  );
                },
                onDelete: () {
                  _showDeleteDialog(transaction);
                },
              );
            },
          );
        },
      ),
      floatingActionButton: widget.isPersonalMode
          ? FloatingActionButton(
              onPressed: () {
                Navigator.of(context).pushNamed('/add-transaction');
              },
              child: const Icon(Icons.add),
            )
          : null, // 家庭模式不显示加号
    );
  }

  void _showDeleteDialog(Transaction transaction) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('删除账单'),
        content: Text('确定要删除这条账单吗？\n\n${transaction.note ?? transaction.categoryName}'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () async {
              Navigator.of(context).pop();
              final success = await context
                  .read<TransactionProvider>()
                  .deleteTransaction(transaction.id);
              if (!success && mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('删除失败')),
                );
              }
            },
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('删除'),
          ),
        ],
      ),
    );
  }
}

class _TransactionItem extends StatelessWidget {
  final Transaction transaction;
  final VoidCallback onTap;
  final VoidCallback onDelete;

  const _TransactionItem({
    required this.transaction,
    required this.onTap,
    required this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    final isExpense = transaction.type == 'expense';
    final color = isExpense ? Colors.red : Colors.green;
    final icon = isExpense ? Icons.arrow_downward : Icons.arrow_upward;

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      child: ListTile(
        onTap: onTap,
        leading: CircleAvatar(
          backgroundColor: color.withOpacity(0.1),
          child: Icon(icon, color: color),
        ),
        title: Text(
          transaction.categoryName,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(
          _formatDate(transaction.date),
          style: TextStyle(color: Colors.grey.shade600),
        ),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text(
              '${isExpense ? '-' : '+'}¥${transaction.amount.toStringAsFixed(2)}',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
            if (transaction.note != null)
              Text(
                transaction.note!,
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey.shade500,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
          ],
        ),
      ),
    );
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final yesterday = today.subtract(const Duration(days: 1));
    final transactionDate = DateTime(date.year, date.month, date.day);

    if (transactionDate == today) {
      return '今天 ${DateFormat.Hm().format(date)}';
    } else if (transactionDate == yesterday) {
      return '昨天 ${DateFormat.Hm().format(date)}';
    } else {
      return DateFormat('yyyy-MM-dd HH:mm').format(date);
    }
  }
}
