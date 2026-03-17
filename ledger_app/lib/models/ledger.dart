class Ledger {
  final String id;
  final String name;
  final String type;
  final String ownerId;
  final int budget;
  final String currency;
  final DateTime createdAt;
  final DateTime updatedAt;

  Ledger({
    required this.id,
    required this.name,
    required this.type,
    required this.ownerId,
    required this.budget,
    required this.currency,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Ledger.fromJson(Map<String, dynamic> json) {
    return Ledger(
      id: json['id'] ?? json['ledgerId'] ?? '',
      name: json['name'] ?? '',
      type: json['type'] ?? 'personal',
      ownerId: json['ownerId'] ?? '',
      budget: json['budget'] ?? 0,
      currency: json['currency'] ?? 'CNY',
      createdAt: DateTime.parse(json['createdAt'] ?? DateTime.now().toIso8601String()),
      updatedAt: DateTime.parse(json['updatedAt'] ?? DateTime.now().toIso8601String()),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'type': type,
      'ownerId': ownerId,
      'budget': budget,
      'currency': currency,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }
}
