import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:ledger_app/models/transaction.dart';
import 'package:ledger_app/models/category.dart';
import 'package:ledger_app/providers/transaction_provider.dart';
import 'package:provider/provider.dart';

class AddEditTransactionScreen extends StatefulWidget {
  const AddEditTransactionScreen({super.key});

  @override
  State<AddEditTransactionScreen> createState() => _AddEditTransactionScreenState();
}

class _AddEditTransactionScreenState extends State<AddEditTransactionScreen> {
  final _formKey = GlobalKey<FormState>();
  final _amountController = TextEditingController();
  final _noteController = TextEditingController();

  int _selectedType = 2; // 默认支出
  String _selectedCategory = '';
  DateTime _selectedDate = DateTime.now();
  Transaction? _editingTransaction;

  // 模拟分类数据，实际应该从后端获取
  final List<Category> _categories = [
    Category(id: '1', name: '餐饮', icon: '🍔', type: 2, sort: 1),
    Category(id: '2', name: '交通', icon: '🚗', type: 2, sort: 2),
    Category(id: '3', name: '购物', icon: '🛒', type: 2, sort: 3),
    Category(id: '4', name: '娱乐', icon: '🎮', type: 2, sort: 4),
    Category(id: '5', name: '医疗', icon: '💊', type: 2, sort: 5),
    Category(id: '6', name: '工资', icon: '💰', type: 1, sort: 1),
    Category(id: '7', name: '奖金', icon: '🎁', type: 1, sort: 2),
    Category(id: '8', name: '理财', icon: '📈', type: 1, sort: 3),
  ];

  @override
  void initState() {
    super.initState();
    // 检查是否是编辑模式
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final args = ModalRoute.of(context)?.settings.arguments;
      if (args != null && args is Transaction) {
        _editingTransaction = args as Transaction;
        _selectedType = _editingTransaction!.type;
        _selectedCategory = _editingTransaction!.categoryId;
        _amountController.text = _editingTransaction!.amount.toString();
        _noteController.text = _editingTransaction!.note ?? '';
        _selectedDate = _editingTransaction!.date;
      } else {
        _selectedCategory = _categories.firstWhere((c) => c.type == _selectedType).id;
      }
    });
  }

  @override
  void dispose() {
    _amountController.dispose();
    _noteController.dispose();
    super.dispose();
  }

  List<Category> get _filteredCategories {
    return _categories.where((c) => c.type == _selectedType).toList();
  }

  Category get _selectedCategoryObj {
    return _categories.firstWhere((c) => c.id == _selectedCategory);
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;

    final transaction = Transaction(
      id: _editingTransaction?.id ?? '',
      ledgerId: '', // 将由provider填充
      categoryId: _selectedCategory,
      categoryName: _selectedCategoryObj.name,
      categoryIcon: _selectedCategoryObj.icon,
      type: _selectedType,
      amount: double.tryParse(_amountController.text) ?? 0,
      note: _noteController.text.trim().isEmpty ? null : _noteController.text.trim(),
      date: _selectedDate,
    );

    bool success;
    if (_editingTransaction != null) {
      success = await context.read<TransactionProvider>().updateTransaction(transaction);
    } else {
      success = await context.read<TransactionProvider>().addTransaction(transaction);
    }

    if (!mounted) return;

    if (success) {
      Navigator.of(context).pop();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(_editingTransaction != null ? '更新成功' : '添加成功')),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('保存失败')),
      );
    }
  }

  Future<void> _selectDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate,
      firstDate: DateTime(2020),
      lastDate: DateTime.now(),
    );

    if (picked != null && mounted) {
      setState(() {
        _selectedDate = picked!;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_editingTransaction != null ? '编辑账单' : '添加账单'),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            // 类型选择
            SegmentedButton<int>(
              segments: const [
                ButtonSegment(
                  value: 1,
                  label: Text('收入'),
                  icon: Icon(Icons.arrow_upward, color: Colors.green),
                ),
                ButtonSegment(
                  value: 2,
                  label: Text('支出'),
                  icon: Icon(Icons.arrow_downward, color: Colors.red),
                ),
              ],
              selected: _selectedType,
              onSelectionChanged: (Set<int> newSelection) {
                setState(() {
                  _selectedType = newSelection.first;
                  _selectedCategory = _filteredCategories.first.id;
                });
              },
            ),
            const SizedBox(height: 24),

            // 分类选择
            DropdownButtonFormField<String>(
              value: _selectedCategory,
              decoration: const InputDecoration(
                labelText: '分类',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.category),
              ),
              items: _filteredCategories.map((category) {
                return DropdownMenuItem(
                  value: category.id,
                  child: Row(
                    children: [
                      Text(category.icon, style: const TextStyle(fontSize: 24)),
                      const SizedBox(width: 12),
                      Text(category.name),
                    ],
                  ),
                );
              }).toList(),
              onChanged: (value) {
                if (value != null) {
                  setState(() {
                    _selectedCategory = value;
                  });
                }
              },
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return '请选择分类';
                }
                return null;
              },
            ),
            const SizedBox(height: 16),

            // 金额输入
            TextFormField(
              controller: _amountController,
              decoration: const InputDecoration(
                labelText: '金额',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.attach_money),
                suffixText: '元',
              ),
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return '请输入金额';
                }
                final amount = double.tryParse(value);
                if (amount == null || amount <= 0) {
                  return '金额必须大于0';
                }
                return null;
              },
            ),
            const SizedBox(height: 16),

            // 日期选择
            InkWell(
              onTap: _selectDate,
              child: InputDecorator(
                decoration: const InputDecoration(
                  labelText: '日期',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.calendar_today),
                ),
                child: Padding(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        DateFormat('yyyy-MM-dd').format(_selectedDate),
                        style: const TextStyle(fontSize: 16),
                      ),
                      const Icon(Icons.arrow_drop_down),
                    ],
                  ),
                ),
              ),
            ),
            const SizedBox(height: 16),

            // 备注
            TextFormField(
              controller: _noteController,
              decoration: const InputDecoration(
                labelText: '备注（可选）',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.note),
              ),
              maxLines: 3,
            ),
            const SizedBox(height: 32),

            // 保存按钮
            ElevatedButton(
              onPressed: _save,
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child: const Text('保存', style: TextStyle(fontSize: 16)),
            ),
          ],
        ),
      ),
    );
  }
}
