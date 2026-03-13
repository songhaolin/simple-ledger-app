class User {
  final String id;
  final String phone;
  final String nickname;
  final String? token;
  final String? refreshToken;

  User({
    required this.id,
    required this.phone,
    required this.nickname,
    this.token,
    this.refreshToken,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['userId'] ?? json['id'] ?? '',
      phone: json['phone'] ?? '',
      nickname: json['nickname'] ?? '',
      token: json['token'],
      refreshToken: json['refreshToken'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'phone': phone,
      'nickname': nickname,
      'token': token,
      'refreshToken': refreshToken,
    };
  }
}
