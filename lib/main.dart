import 'package:flutter/material.dart';
import 'package:urban_lens/src/features/camera/data/datasources/camera_remote_data_source.dart';
import 'package:urban_lens/src/features/camera/data/repositories_impl/camera_repository_impl.dart';

void main() {
  runApp(const MaterialApp(home: UrbanLensTest()));
}

class UrbanLensTest extends StatefulWidget {
  const UrbanLensTest({super.key});

  @override
  State<UrbanLensTest> createState() => _UrbanLensTestState();
}

class _UrbanLensTestState extends State<UrbanLensTest> {
  String _status = "Idle";

  Future<void> _testCamera() async {
    setState(() => _status = "Connecting to Native...");

    // Manual Dependency Injection (We will use Riverpod later)
    final dataSource = CameraRemoteDataSourceImpl();
    final repository = CameraRepositoryImpl(remoteDataSource: dataSource);

    final result = await repository.captureImage();

    result.fold((failure) => setState(() => _status = "Error: ${failure.message}"), (path) => setState(() => _status = "Success! Path: $path"));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_status, textAlign: TextAlign.center),
            const SizedBox(height: 20),
            ElevatedButton(onPressed: _testCamera, child: const Text("Test Native Bridge")),
          ],
        ),
      ),
    );
  }
}
