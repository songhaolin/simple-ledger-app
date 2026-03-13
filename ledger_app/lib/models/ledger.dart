class Ledger {
  final String id;
  final String name;
  final String? icon;
  final int type; // 1: 个人, 2: 家庭
  final DateTime createTime;

  Ledger({
    required this.id,
    required this.name,
    this.icon,
    required this.type,
    required this.createTime,
  });

  factory Ledger.fromJson(Map<String, dynamic> json) {
    return Ledger(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      icon: json['icon'],
      type: json['type'] ?? 1,
      createTime: json['createTime'] != null
          ? DateTime.parse(json['createTime'])
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'icon': icon,
      'type': type,
      'createTime': createTime.toIso8601String(),
    };
  }

  String get typeText => type == 1 ? '个人' : '家庭';
}
