import 'package:botify_ads/facebook_audience_network.dart';
import 'package:botify_ads_example/facebookads.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: AdsMainPage(),
    );
  }
}

class AdsMainPage extends StatefulWidget {
  const AdsMainPage({super.key});

  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<AdsMainPage> {
  @override
  void initState() {
    super.initState();
    FacebookAudienceNetwork.init(
      testingId: "16aba1a4-f8bb-4f5e-933b-5a7ed99ebf58",
      iOSAdvertiserTrackingEnabled: true,
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Botify Ads Example'),
        ),
        body: Column(
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
            GestureDetector(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) => const MyFaceBookAds()),
                );
              },
              child: Container(
                height: 100,
                color: Colors.blueAccent,
                child: const Center(
                    child: Text('Facebook  Ads',
                        style: TextStyle(color: Colors.white))),
              ),
            ),
            const SizedBox(
              height: 10,
            ),
            const SizedBox(
              height: 10,
            ),
          ],
        ),
      ),
    );
  }
}
