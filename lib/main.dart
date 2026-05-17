import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:nfc_manager/nfc_manager.dart';
import 'package:google_fonts/google_fonts.dart';

void main() {
  SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
    statusBarColor: Colors.transparent,
    statusBarIconBrightness: Brightness.light,
  ));
  runApp(MaterialApp(
    debugShowCheckedModeBanner: false,
    theme: ThemeData(brightness: Brightness.dark, fontFamily: 'Inter'),
    home: const SimpleTasker(),
  ));
}

class SimpleTasker extends StatefulWidget {
  const SimpleTasker({super.key});

  @override
  State<SimpleTasker> createState() => _SimpleTaskerState();
}

class _SimpleTaskerState extends State<SimpleTasker> {
  String _status = "IDLE";
  String _subStatus = "Pronto";
  static const platform = MethodChannel('com.example.task/system_control');
  bool _isActive = false;

  @override
  void initState() {
    super.initState();
    executeTask();
  }

  void _resetToIdle() {
    Future.delayed(const Duration(seconds: 5), () {
      if (mounted) {
        setState(() {
          _status = "IDLE";
          _subStatus = "In attesa di segnale NFC";
          _isActive = false;
        });
      }
    });
  }

  Future<void> executeTask() async {
    HapticFeedback.mediumImpact();
    setState(() => _status = "");
    try {
      await platform.invokeMethod('enableSystemFeatures');
      setState(() {
        _status = "SUCCESS";
        _subStatus = "Configurazione applicata all'avvio";
        _isActive = true;
      });
    } on PlatformException catch (e) {
      setState(() {
        _status = "ERROR";
        _subStatus = e.message ?? "Errore di sistema";
        _isActive = false;
      });
    }
    _resetToIdle();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF000000),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 40),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 100),
            /* Text(_status, style: const TextStyle(fontSize: 48, fontWeight: FontWeight.w200, letterSpacing: -2)), */
            const Spacer(),
            Center(
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 300),
                width: 160, height: 160,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  border: Border.all(color: _isActive ? const Color(0xFF42A5F5) : Colors.white10),
                ),
                child: Icon(_isActive ? Icons.bolt_rounded : Icons.offline_bolt_rounded, size: 40, color: _isActive ? const Color(0xFF42A5F5) : Colors.white24),
              ),
            ),
            const Spacer(),
            SizedBox(
              width: double.infinity,
              child: TextButton(
                onPressed: executeTask,
                child: Text("MANUAL RE-RUN", style: GoogleFonts.inter(fontSize: 13, fontWeight: FontWeight.w600, color: Colors.white24)),
              ),
            ),
            const SizedBox(height: 60),
          ],
        ),
      ),
    );
  }
}