import 'package:flutter/material.dart';
import 'package:ledger_app/screens/home_screen.dart';
import 'package:ledger_app/screens/login_screen.dart';
import 'package:ledger_app/screens/transaction_list_screen.dart';
import 'package:ledger_app/screens/add_edit_transaction_screen.dart';
import 'package:ledger_app/screens/statistics_screen.dart';
import 'package:ledger_app/screens/ledger_list_screen.dart';
import 'package:ledger_app/providers/transaction_provider.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => TransactionProvider(),
      child: MaterialApp(
        title: '简账',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(
            seedColor: Colors.blue,
            brightness: Brightness.light,
          ),
          useMaterial3: true,
          appBarTheme: const AppBarTheme(
            centerTitle: true,
            elevation: 0,
          ),
        ),
        initialRoute: '/login',
        routes: {
          '/login': (context) => const LoginScreen(),
          '/home': (context) => const HomeScreen(),
          '/transactions': (context) => const TransactionListScreen(),
          '/ledgers': (context) => const LedgerListScreen(),
          '/add-transaction': (context) => const AddEditTransactionScreen(),
          '/edit-transaction': (context) => const AddEditTransactionScreen(),
          '/statistics': (context) => const StatisticsScreen(),
        },
      ),
    );
  }
}
