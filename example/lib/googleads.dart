import 'package:flutter/material.dart';

class MyGoogleAds extends StatefulWidget {
  const MyGoogleAds({super.key});

  @override
  State<MyGoogleAds> createState() => _MyGoogleAdsState();
}

class _MyGoogleAdsState extends State<MyGoogleAds> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Google Ads'),
        ),
        body: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.max,
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              const SizedBox(
                height: 10,
              ),
              const SizedBox(
                height: 10,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
