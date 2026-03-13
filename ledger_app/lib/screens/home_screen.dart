import 'package:flutter/material.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('简账'),
          bottom: const TabBar(
            tabs: [
              Tab(text: '账单', icon: Icon(Icons.receipt_long)),
              Tab(text: '统计', icon: Icon(Icons.bar_chart)),
              Tab(text: '我的', icon: Icon(Icons.person)),
            ],
          ),
        ),
        body: const TabBarView(
          children: [
            _TransactionTab(),
            _StatisticsTab(),
            _ProfileTab(),
          ],
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () {
            Navigator.of(context).pushNamed('/add-transaction');
          },
          child: const Icon(Icons.add),
        ),
      ),
    );
  }
}

class _TransactionTab extends StatelessWidget {
  const _TransactionTab();

  @override
  Widget build(BuildContext context) {
    // 跳转到账单列表页面
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Navigator.of(context).pushReplacementNamed('/transactions');
    });

    return const Center(child: CircularProgressIndicator());
  }
}

class _StatisticsTab extends StatelessWidget {
  const _StatisticsTab();

  @override
  Widget build(BuildContext context) {
    // 跳转到统计页面
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Navigator.of(context).pushReplacementNamed('/statistics');
    });

    return const Center(child: CircularProgressIndicator());
  }
}

class _ProfileTab extends StatelessWidget {
  const _ProfileTab();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircleAvatar(
            radius: 40,
            child: Icon(Icons.person, size: 48),
          ),
          const SizedBox(height: 16),
          const Text(
            '个人中心',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '功能开发中...',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey.shade400,
            ),
          ),
        ],
      ),
    );
  }
}
