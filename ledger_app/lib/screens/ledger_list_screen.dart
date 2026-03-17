import 'package:flutter/material.dart';
import 'package:ledger_app/models/ledger.dart';
import 'package:ledger_app/services/ledger_service.dart';

class LedgerListScreen extends StatefulWidget {
  const LedgerListScreen({super.key});

  @override
  State<LedgerListScreen> createState() => _LedgerListScreenState();
}

class _LedgerListScreenState extends State<LedgerListScreen> {
  List<Ledger> _ledgers = [];
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadLedgers();
  }

  Future<void> _loadLedgers() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final ledgerService = LedgerService();
      final response = await ledgerService.getLedgers();

      if (response.isSuccess && response.data != null) {
        setState(() {
          _ledgers = response.data!;
          _isLoading = false;
        });
      } else {
        setState(() {
          _errorMessage = response.message;
          _isLoading = false;
          _ledgers = [];
        });
      }
    } catch (e) {
      setState(() {
        _errorMessage = '加载失败: $e';
        _isLoading = false;
        _ledgers = [];
      });
    }
  }

  Future<void> _createLedger() async {
    final nameController = TextEditingController();
    final formKey = GlobalKey<FormState>();

    final result = await showDialog<String>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('创建家庭账本'),
        content: Form(
          key: formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Text(
                '家庭账本可以与家人共享账单数据',
                style: TextStyle(fontSize: 12, color: Colors.grey),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: nameController,
                decoration: const InputDecoration(
                  labelText: '账本名称',
                  hintText: '例如：我的家庭账本',
                  border: OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return '请输入账本名称';
                  }
                  return null;
                },
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () {
              if (formKey.currentState!.validate()) {
                Navigator.pop(context, nameController.text);
              }
            },
            child: const Text('创建'),
          ),
        ],
      ),
    );

    if (result != null && result!.isNotEmpty) {
      await _doCreateLedger(result!);
    }
  }

  Future<void> _doCreateLedger(String name) async {
    try {
      final ledgerService = LedgerService();
      final response = await ledgerService.createLedger(
        name: name,
        type: 'family',
      );

      if (response.isSuccess) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('账本创建成功')),
          );
          await _loadLedgers();
        }
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
          SnackBar(content: Text('创建失败: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        appBar: AppBar(title: Text('账本列表')),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('账本列表'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadLedgers,
          ),
        ],
      ),
      body: _errorMessage != null
          ? _buildErrorState()
          : _ledgers.isEmpty
              ? _buildEmptyState()
              : _buildLedgerList(),
      floatingActionButton: FloatingActionButton(
        onPressed: _createLedger,
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildErrorState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 64, color: Colors.red),
          const SizedBox(height: 16),
          Text(
            _errorMessage!,
            style: const TextStyle(color: Colors.red, fontSize: 16),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: _loadLedgers,
            child: const Text('重试'),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.family_restroom,
            size: 80,
            color: Colors.grey.shade300,
          ),
          const SizedBox(height: 24),
          Text(
            '还没有账本',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Colors.grey.shade700,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            '创建一个账本，开始记录家庭账单',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey.shade500,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLedgerList() {
    return RefreshIndicator(
      onRefresh: _loadLedgers,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _ledgers.length,
        itemBuilder: (context, index) {
          final ledger = _ledgers[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: ledger.type == 'family'
                    ? Colors.blue.shade50
                    : Colors.green.shade50,
                child: ledger.type == 'family'
                    ? const Icon(Icons.family_restroom, color: Colors.blue)
                    : const Icon(Icons.person, color: Colors.green),
              ),
              title: Text(
                ledger.name,
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
              subtitle: Text(
                ledger.type == 'family' ? '家庭账本' : '个人账本',
                style: TextStyle(color: Colors.grey.shade600),
              ),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {
                Navigator.pop(context, ledger);
              },
            ),
          );
        },
      ),
    );
  }
}
