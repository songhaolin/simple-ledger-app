import 'package:flutter/material.dart';
import 'package:ledger_app/providers/transaction_provider.dart';
import 'package:ledger_app/models/ledger.dart';
import 'package:ledger_app/screens/transaction_list_screen.dart';
import 'package:ledger_app/screens/ledger_list_screen.dart';
import 'package:ledger_app/services/ledger_service.dart';
import 'package:provider/provider.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  // 模式：personal（个人） | family（家庭）
  String _mode = 'personal';
  Ledger? _currentLedger;
  List<Ledger> _ledgers = [];
  bool _isLoadingLedgers = false;

  @override
  void initState() {
    super.initState();
    _loadUserPreference();
  }

  Future<void> _loadUserPreference() async {
    // TODO: 从本地存储读取用户偏好的记账模式
    // 暂时默认为个人模式
  }

  Future<void> _loadLedgers() async {
    setState(() {
      _isLoadingLedgers = true;
    });

    try {
      final response = await context.read<LedgerService>().getLedgers();
      if (response.isSuccess && response.data != null) {
        setState(() {
          _ledgers = response.data!;
          // 自动选择第一个账本（如果有）
          if (_ledgers.isNotEmpty && _currentLedger == null) {
            _currentLedger = _ledgers[0];
          }
        });
      }
    } catch (e) {
      print('加载账本失败: $e');
    } finally {
      setState(() {
        _isLoadingLedgers = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('简账'),
        actions: [
          if (_mode == 'family')
            IconButton(
              icon: const Icon(Icons.settings),
              onPressed: () {
                _showSettingsBottomSheet(context);
              },
            ),
        ],
      ),
      body: _mode == 'personal'
          ? _buildPersonalMode()
          : _buildFamilyMode(),
    );
  }

  Widget _buildPersonalMode() {
    return TransactionListScreen();
  }

  Widget _buildFamilyMode() {
    if (_currentLedger == null) {
      return _LedgerSelectionScreen(
        ledgers: _ledgers,
        isLoading: _isLoadingLedgers,
        onSelectLedger: (ledger) {
          setState(() {
            _currentLedger = ledger;
          });
          context.read<TransactionProvider>().setCurrentLedger(ledger);
        },
        onCreateLedger: () async {
          await _showCreateLedgerDialog();
          if (_ledgers.isEmpty) {
            await _loadLedgers();
          }
        },
      );
    } else {
      return TransactionListScreen();
    }
  }

  void _showSettingsBottomSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '切换记账模式',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            ListTile(
              leading: const Icon(Icons.person),
              title: const Text('个人记账'),
              subtitle: const Text('直接记录，无需账本'),
              onTap: () {
                setState(() {
                  _mode = 'personal';
                  _currentLedger = null;
                });
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.family_restroom),
              title: const Text('家庭记账'),
              subtitle: const Text('多人共享，统计家庭支出'),
              onTap: () {
                setState(() {
                  _mode = 'family';
                });
                _loadLedgers();
                Navigator.pop(context);
              },
            ),
            const SizedBox(height: 16),
            if (_mode == 'family' && _currentLedger != null)
              ListTile(
                leading: const Icon(Icons.logout),
                title: const Text('退出当前账本'),
                onTap: () {
                  setState(() {
                    _currentLedger = null;
                  });
                  Navigator.pop(context);
                },
              ),
          ],
        ),
      ),
    );
  }

  Future<void> _showCreateLedgerDialog() async {
    final nameController = TextEditingController();
    final formKey = GlobalKey<FormState>();

    final result = await showDialog<Map<String, dynamic>>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('创建家庭账本'),
        content: Form(
          key: formKey,
          child: TextFormField(
            controller: nameController,
            decoration: const InputDecoration(
              labelText: '账本名称',
              hintText: '例如：我的家庭账本',
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return '请输入账本名称';
              }
              return null;
            },
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
                Navigator.pop(context, {'name': nameController.text});
              }
            },
            child: const Text('创建'),
          ),
        ],
      ),
    );

    if (result != null) {
      await _createLedger(result!['name']);
    }
  }

  Future<void> _createLedger(String name) async {
    final response = await context.read<LedgerService>().createLedger(
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
  }
}

class _LedgerSelectionScreen extends StatelessWidget {
  final List<Ledger> ledgers;
  final bool isLoading;
  final Function(Ledger) onSelectLedger;
  final VoidCallback onCreateLedger;

  const _LedgerSelectionScreen({
    required this.ledgers,
    required this.isLoading,
    required this.onSelectLedger,
    required this.onCreateLedger,
  });

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    return ledgers.isEmpty
        ? _EmptyLedgerState(onCreate: onCreateLedger)
        : _LedgerList(
            ledgers: ledgers,
            onSelect: onSelectLedger,
          );
  }
}

class _EmptyLedgerState extends StatelessWidget {
  final VoidCallback onCreate;

  const _EmptyLedgerState({required this.onCreate});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
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
              '还没有家庭账本',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Colors.grey.shade700,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              '创建一个账本，与家人一起记账',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey.shade500,
              ),
            ),
            const SizedBox(height: 32),
            ElevatedButton.icon(
              onPressed: onCreate,
              icon: const Icon(Icons.add),
              label: const Text('创建账本'),
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(
                  horizontal: 32,
                  vertical: 16,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _LedgerList extends StatelessWidget {
  final List<Ledger> ledgers;
  final Function(Ledger) onSelect;

  const _LedgerList({
    required this.ledgers,
    required this.onSelect,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '选择账本',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Colors.grey.shade800,
            ),
          ),
          const SizedBox(height: 16),
          Expanded(
            child: ListView.builder(
              itemCount: ledgers.length,
              itemBuilder: (context, index) {
                final ledger = ledgers[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: Colors.blue.shade50,
                      child: ledger.type == 'family'
                          ? const Icon(Icons.family_restroom, color: Colors.blue)
                          : const Icon(Icons.person, color: Colors.blue),
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
                    onTap: () => onSelect(ledger),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
