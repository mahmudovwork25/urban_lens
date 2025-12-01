import 'package:equatable/equatable.dart';

class CameraReport extends Equatable {
  final String id;
  final String imagePath;
  final DateTime timestamp;
  final double latitude;
  final double longitude;

  const CameraReport({required this.id, required this.imagePath, required this.timestamp, required this.latitude, required this.longitude});

  @override
  List<Object?> get props => [id, imagePath, timestamp, latitude, longitude];
}
