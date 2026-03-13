class Transaction {
  final String id;
  final String ledgerId;
  final String categoryId;
  final String categoryName;
  final String categoryIcon;
  final int type; // 1: 收入, 2: 支出
  final double amount;
  final String? note;
  final DateTime date;
  final int? year;
  final int? month;
  final int? day;

  Transaction({
    required this.id,
    required this.ledgerId,
    required this.categoryId,
    required this.categoryName,
    required this.categoryIcon,
    required this.type,
    required this.amount,
    this.note,
    required this.date,
    this.year,
    this.month,
    this.day,
  });

  factory Transaction.fromJson(Map<String, dynamic> json) {
    return Transaction(
      id: json['id'] ?? '',
      ledgerId: json['ledgerId'] ?? '',
      categoryId: json['categoryId'] ?? '',
      categoryName: json['categoryName'] ?? '',
      categoryIcon: json['categoryIcon'] ?? '',
      type: json['type'] ?? 2,
      amount: (json['amount'] ?? 0).toDouble(),
      note: json['note'],
      date: json['date'] != null
          ? DateTime.parse(json['date'])
          : DateTime.now(),
      year: json['year'],
      month: json['month'],
      day: json['day'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'ledgerId': ledgerId,
      'categoryId': categoryId,
      'categoryName': categoryName,
      'categoryIcon': categoryIcon,
      'type': type,
      'amount': amount,
      'note': note,
      'date': date.toIso8601String(),
      'year': year,
      'month': month,
      'day': day,
    };
  }

  String get typeText => type == 1 ? '收入' : '支出';
}
