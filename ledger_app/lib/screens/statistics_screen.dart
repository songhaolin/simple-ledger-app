import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:ledger_app/providers/transaction_provider.dart';
import 'package:provider/provider.dart';

class StatisticsScreen extends StatefulWidget {
  const StatisticsScreen({super.key});

  @override
  State<StatisticsScreen> createState() => _StatisticsScreenState();
}

class _StatisticsScreenState extends State<StatisticsScreen> {
  int _selectedPeriod = 0; // 0: 本月, 1: 本年

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<TransactionProvider>().loadStatistics();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('收支统计'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              context.read<TransactionProvider>().loadStatistics();
            },
          ),
        ],
      ),
      body: Consumer<TransactionProvider>(
        builder: (context, provider, child) {
          final stats = provider.statistics;

          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // 时间选择
                SegmentedButton<int>(
                  segments: const [
                    ButtonSegment(value: 0, label: Text('本月')),
                    ButtonSegment(value: 1, label: Text('本年')),
                  ],
                  selected: _selectedPeriod,
                  onSelectionChanged: (Set<int> newSelection) {
                    setState(() {
                      _selectedPeriod = newSelection.first;
                    });
                    // TODO: 根据选择的周期重新加载数据
                  },
                ),
                const SizedBox(height: 24),

                // 收支卡片
                Row(
                  children: [
                    Expanded(
                      child: _StatCard(
                        title: '总收入',
                        value: '¥${(stats['totalIncome'] ?? 0).toStringAsFixed(2)}',
                        color: Colors.green,
                        icon: Icons.arrow_upward,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _StatCard(
                        title: '总支出',
                        value: '¥${(stats['totalExpense'] ?? 0).toStringAsFixed(2)}',
                        color: Colors.red,
                        icon: Icons.arrow_downward,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                _StatCard(
                  title: '结余',
                  value: '¥${((stats['totalIncome'] ?? 0) - (stats['totalExpense'] ?? 0)).toStringAsFixed(2)}',
                  color: Colors.blue,
                  icon: Icons.account_balance_wallet,
                ),
                const SizedBox(height: 24),

                // 分类占比
                _buildCategorySection(stats),

                const SizedBox(height: 24),

                // 趋势图
                _buildTrendSection(stats),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildCategorySection(Map<String, dynamic> stats) {
    final categories = stats['categorySummary'] as List<dynamic>? ?? [];

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              '分类占比',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            if (categories.isEmpty)
              const Center(
                child: Padding(
                  padding: EdgeInsets.all(32),
                  child: Text('暂无数据'),
                ),
              )
            else
              ...categories.map((cat) {
                final category = cat as Map<String, dynamic>;
                return Padding(
                  padding: const EdgeInsets.symmetric(vertical: 8),
                  child: Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              '${category['categoryName']} ${category['icon'] ?? ''}',
                              style: const TextStyle(fontWeight: FontWeight.w600),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '¥${category['amount']?.toStringAsFixed(2) ?? '0.00'}',
                              style: const TextStyle(fontSize: 14),
                            ),
                          ],
                        ),
                      ),
                      SizedBox(
                        width: 100,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              '${(category['percentage'] ?? 0).toStringAsFixed(1)}%',
                              style: const TextStyle(
                                fontWeight: FontWeight.bold,
                                color: Colors.blue,
                              ),
                            ),
                            const SizedBox(height: 4),
                            ClipRRect(
                              borderRadius: BorderRadius.circular(4),
                              child: LinearProgressIndicator(
                                value: (category['percentage'] ?? 0) / 100,
                                backgroundColor: Colors.grey.shade200,
                                valueColor: AlwaysStoppedAnimation<Color>(
                                  Colors.blue,
                                ),
                                minHeight: 6,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                );
              }).toList(),
          ],
        ),
      ),
    );
  }

  Widget _buildTrendSection(Map<String, dynamic> stats) {
    final dailyTrends = stats['dailyTrends'] as List<dynamic>? ?? [];

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              '每日趋势',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            if (dailyTrends.isEmpty)
              const Center(
                child: Padding(
                  padding: EdgeInsets.all(32),
                  child: Text('暂无数据'),
                ),
              )
            else
              SizedBox(
                height: 200,
                child: LineChart(
                  LineChartData(
                    gridData: FlGridData(show: false),
                    titlesData: FlTitlesData(
                      show: true,
                      rightTitles: AxisTitles(sideTitles: SideTitles(showTitles: false)),
                      topTitles: AxisTitles(sideTitles: SideTitles(showTitles: false)),
                      bottomTitles: AxisTitles(
                        sideTitles: SideTitles(
                          showTitles: true,
                          getTitlesWidget: (value, meta) {
                            if (value.toInt() % 5 != 0) return const Text('');
                            final index = value.toInt();
                            if (index < dailyTrends.length) {
                              final trend = dailyTrends[index] as Map<String, dynamic>;
                              final date = DateTime.parse(trend['date']);
                              return Text('${date.day}日');
                            }
                            return const Text('');
                          },
                        ),
                      ),
                      leftTitles: AxisTitles(
                        sideTitles: SideTitles(
                          showTitles: true,
                          reservedSize: 40,
                          getTitlesWidget: (value, meta) {
                            if (value % 500 != 0) return const Text('');
                            return Text('¥${value.toInt()}');
                          },
                        ),
                      ),
                    ),
                    borderData: FlBorderData(show: false),
                    minX: 0,
                    maxX: (dailyTrends.length - 1).toDouble(),
                    minY: 0,
                    maxY: _calculateMaxY(dailyTrends),
                    lineBarsData: [
                      LineChartBarData(
                        spots: _buildSpots(dailyTrends),
                        isCurved: true,
                        color: Colors.blue,
                        barWidth: 3,
                        dotData: FlDotData(show: false),
                      ),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  double _calculateMaxY(List<dynamic> trends) {
    double max = 0;
    for (var trend in trends) {
      final t = trend as Map<String, dynamic>;
      final amount = t['totalAmount'] ?? 0;
      if (amount > max) max = amount;
    }
    return (max * 1.2).ceilToDouble();
  }

  List<FlSpot> _buildSpots(List<dynamic> trends) {
    return trends.asMap().entries.map((entry) {
      final trend = entry.value as Map<String, dynamic>;
      final amount = trend['totalAmount'] ?? 0;
      return FlSpot(entry.key.toDouble(), amount.toDouble());
    }).toList();
  }
}

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final Color color;
  final IconData icon;

  const _StatCard({
    required this.title,
    required this.value,
    required this.color,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Row(
              children: [
                Icon(icon, color: color, size: 28),
                const SizedBox(width: 8),
                Text(
                  title,
                  style: TextStyle(
                    fontSize: 14,
                    color: Colors.grey.shade600,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              value,
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
