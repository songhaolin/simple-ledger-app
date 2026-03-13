class Category {
  final String id;
  final String name;
  final String icon;
  final int type; // 1: 收入, 2: 支出
  final int sort;

  Category({
    required this.id,
    required this.name,
    required this.icon,
    required this.type,
    required this.sort,
  });

  factory Category.fromJson(Map<String, dynamic> json) {
    return Category(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      icon: json['icon'] ?? '',
      type: json['type'] ?? 2,
      sort: json['sort'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'icon': icon,
      'type': type,
      'sort': sort,
    };
  }

  String get typeText => type == 1 ? '收入' : '支出';
}
