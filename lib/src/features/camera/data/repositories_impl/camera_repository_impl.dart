import 'package:fpdart/fpdart.dart';
import 'package:urban_lens/core/error/exseptions.dart';
import 'package:urban_lens/core/error/failure.dart';
import 'package:urban_lens/src/features/camera/data/datasources/camera_remote_data_source.dart';
import 'package:urban_lens/src/features/camera/domain/entities/camera_report.dart';
import 'package:urban_lens/src/features/camera/domain/failures/camera_failure.dart';
import 'package:urban_lens/src/features/camera/domain/repositories/camera_repository.dart';

class CameraRepositoryImpl implements CameraRepository {
  final CameraRemoteDataSource remoteDataSource;

  CameraRepositoryImpl({required this.remoteDataSource});

  @override
  Future<Either<Failure, String>> captureImage() async {
    try {
      final result = await remoteDataSource.captureImage();
      return Right(result);
    } on CameraException catch (e) {
      return Left(CameraFailure(message: e.message));
    } catch (e) {
      return Left(CameraFailure(message: 'Unexpected error: $e'));
    }
  }

  @override
  Future<Either<Failure, void>> uploadReport(CameraReport report) {
    // TODO: implement uploadReport
    throw UnimplementedError();
  }
}
