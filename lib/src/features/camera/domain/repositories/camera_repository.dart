import 'package:fpdart/fpdart.dart';
import 'package:urban_lens/core/error/failure.dart';
import 'package:urban_lens/src/features/camera/domain/entities/camera_report.dart';

abstract class CameraRepository {
  /// Returns the [String] path to the image on the device.
  Future<Either<Failure, String>> captureImage();

  /// Saves the report metadata to the backend.
  Future<Either<Failure, void>> uploadReport(CameraReport report);
}
