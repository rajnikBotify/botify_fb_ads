import 'package:botify_ads/facebook_audience_network.dart';
import 'package:flutter/material.dart';

class MyFaceBookAds extends StatefulWidget {
  const MyFaceBookAds({super.key});

  @override
  State<MyFaceBookAds> createState() => _MyFaceBookAdsState();
}

class _MyFaceBookAdsState extends State<MyFaceBookAds> {
  String bannerID = 'IMG_16_9_APP_INSTALL#2312433698835503_2964944860251047';
  String nativeID = 'IMG_16_9_APP_INSTALL#2312433698835503_2964953543583512';
  String interstitialID =
      'IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID';
  String rewardedID = 'IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID';

  @override
  void initState() {
    super.initState();
    FacebookAudienceNetwork.init(
      testingId: "16aba1a4-f8bb-4f5e-933b-5a7ed99ebf58",
      iOSAdvertiserTrackingEnabled: true,
    );
  }

  void loadInterstitialAd() {
    FacebookInterstitialAd.loadInterstitialAd(
      placementId: interstitialID,
      listener: (result, value) {
        print(">> FAN > Interstitial Ad: $result --> $value");
        if (result == InterstitialAdResult.LOADED) {
          print(">> FAN > Interstitial Ad:  LOADED");
          FacebookInterstitialAd.showInterstitialAd();
        } else if (result == InterstitialAdResult.ERROR) {
          print(">> FAN > Interstitial Ad:  ERROR");
        } else if (result == InterstitialAdResult.LOGGING_IMPRESSION) {
          print(">> FAN > Interstitial Ad:  LOGGING_IMPRESSION");
        } else if (result == InterstitialAdResult.CLICKED) {
          print(">> FAN > Interstitial Ad:  CLICKED");
        } else if (result == InterstitialAdResult.DISPLAYED) {
          print(">> FAN > Interstitial Ad:  DISPLAYED");
        } else if (result == InterstitialAdResult.DISMISSED &&
            value["invalidated"] == true) {
          print(">> FAN > Interstitial Ad:  DISMISSED");
        }
      },
    );
  }

  void loadRewardedVideoAd() {
    FacebookRewardedVideoAd.loadRewardedVideoAd(
      placementId: rewardedID,
      listener: (result, value) {
        print("Rewarded Ad: $result --> $value");
        if (result == RewardedVideoAdResult.LOADED) {
          print("Rewarded Ad: LOADED");
        } else if (result == RewardedVideoAdResult.VIDEO_COMPLETE) {
          print("Rewarded Ad: VIDEO_COMPLETE");
        } else if (result == RewardedVideoAdResult.VIDEO_CLOSED &&
            (value == true || value["invalidated"] == true)) {
          print("Rewarded Ad: VIDEO_CLOSED");
          FacebookInterstitialAd.showInterstitialAd();
        } else if (result == RewardedVideoAdResult.CLICKED) {
          print("Rewarded Ad: CLICKED");
        } else if (result == RewardedVideoAdResult.LOGGING_IMPRESSION) {
          print("Rewarded Ad: LOGGING_IMPRESSION");
        } else if (result == RewardedVideoAdResult.ERROR) {
          print("Rewarded Ad: ERROR");
        }
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Facebook Ads'),
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
              Row(
                children: [
                  Expanded(
                    flex: 1,
                    child: GestureDetector(
                      onTap: () {
                        loadInterstitialAd();
                      },
                      child: Container(
                        height: 50,
                        color: Colors.deepOrange,
                        child: const Center(
                            child: Text('Interstitial Ads',
                                style: TextStyle(color: Colors.white))),
                      ),
                    ),
                  ),
                  Expanded(
                    flex: 1,
                    child: GestureDetector(
                      onTap: () {
                        loadRewardedVideoAd();
                      },
                      child: Container(
                        height: 50,
                        color: Colors.teal,
                        child: const Center(
                            child: Text('Rewarded Ads',
                                style: TextStyle(color: Colors.white))),
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(
                height: 10,
              ),
              const SizedBox(
                height: 10,
              ),
              Container(
                color: Colors.grey,
                child: FacebookBannerAd(
                  placementId: bannerID,
                  bannerSize: BannerSize.STANDARD,
                  listener: (result, value) {
                    print("Banner Ad: $result -->  $value");
                  },
                ),
              ),
              const SizedBox(
                height: 10,
              ),
              const SizedBox(
                height: 10,
              ),
              MyNativeAd(
                placementId: nativeID,
                adType: MyNativeAdType.NATIVE_AD_FULL_SCREEN,
                width: double.infinity,
                height: 500,
                backgroundColor: Colors.white,
                titleColor: Colors.amber,
                titleTextSize: 15,
                bodyTextSize: 12,
                socialLabelTextSize: 12,
                sponsoredLabelTextSize: 12,
                adCallButtonTextSize: 14,
                descriptionColor: Colors.brown,
                buttonColor: Colors.lightGreen,
                buttonTitleColor: Colors.teal,
                listener: (result, value) {
                  print("Native Ad: $result --> $value");
                },
                keepExpandedWhileLoading: true,
                expandAnimationDuraion: 1000,
              ),
              const SizedBox(
                height: 10,
              ),
              const SizedBox(
                height: 10,
              ),
              Container(
                height: 100,
                color: Colors.grey,
                child: Center(
                  child: MyNativeAd(
                    placementId: nativeID,
                    adType: MyNativeAdType.NATIVE_AD_SMALL,
                    width: double.infinity,
                    height: 100,
                    backgroundColor: Colors.white,
                    titleColor: Colors.amber,
                    titleTextSize: 15,
                    bodyTextSize: 12,
                    socialLabelTextSize: 12,
                    sponsoredLabelTextSize: 12,
                    adCallButtonTextSize: 14,
                    descriptionColor: Colors.brown,
                    buttonColor: Colors.lightGreen,
                    buttonTitleColor: Colors.teal,
                    listener: (result, value) {
                      print("Native Ad: $result --> $value");
                    },
                    keepExpandedWhileLoading: true,
                    expandAnimationDuraion: 1000,
                  ),
                ),
              ),
              const SizedBox(
                height: 10,
              ),
              const SizedBox(
                height: 10,
              ),
              MyNativeAd(
                placementId: nativeID,
                adType: MyNativeAdType.NATIVE_AD_BIG,
                width: double.infinity,
                height: 250,
                backgroundColor: Colors.deepOrange,
                titleColor: Colors.amber,
                descriptionColor: Colors.brown,
                buttonColor: Colors.lightGreen,
                buttonTitleColor: Colors.teal,
                titleTextSize: 15,
                bodyTextSize: 12,
                socialLabelTextSize: 12,
                sponsoredLabelTextSize: 12,
                adCallButtonTextSize: 14,
                listener: (result, value) {
                  print("Native Ad: $result --> $value");
                },
                keepExpandedWhileLoading: true,
                expandAnimationDuraion: 1000,
              ),
              const SizedBox(
                height: 10,
              ),
              const SizedBox(
                height: 10,
              ),
              Row(
                children: [
                  Expanded(
                      flex: 1,
                      child: Container(
                        height: 250,
                        color: Colors.grey,
                        child: MyNativeAd(
                          placementId: nativeID,
                          adType: MyNativeAdType.NATIVE_AD_BIG_ROW,
                          width: double.infinity,
                          height: 250,
                          backgroundColor: Colors.deepOrange,
                          titleColor: Colors.amber,
                          titleTextSize: 15,
                          bodyTextSize: 10,
                          socialLabelTextSize: 10,
                          sponsoredLabelTextSize: 10,
                          adCallButtonTextSize: 14,
                          descriptionColor: Colors.brown,
                          buttonColor: Colors.lightGreen,
                          buttonTitleColor: Colors.teal,
                          listener: (result, value) {
                            print("Native Ad: $result --> $value");
                          },
                          keepExpandedWhileLoading: true,
                          expandAnimationDuraion: 1000,
                        ),
                      )),
                  Expanded(
                      flex: 1,
                      child: Container(
                        height: 250,
                        color: Colors.blueGrey,
                      ))
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}
