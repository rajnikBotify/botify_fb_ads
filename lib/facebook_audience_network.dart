library facebook_audience_network;

import 'package:flutter/services.dart';

import 'ad/ad_interstitial.dart';
import 'ad/ad_rewarded.dart';
import 'constants.dart';

export 'ad/ad_banner.dart';
export 'ad/ad_interstitial.dart';
export 'ad/ad_my_native.dart';
export 'ad/ad_rewarded.dart';

class FacebookAudienceNetwork {
  static const _channel = MethodChannel(MAIN_CHANNEL);

  static Future<bool?> init(
      {String? testingId, bool iOSAdvertiserTrackingEnabled = false}) async {
    Map<String, String?> initValues = {
      "testingId": testingId,
      "iOSAdvertiserTrackingEnabled": iOSAdvertiserTrackingEnabled.toString(),
    };

    try {
      final result = await _channel.invokeMethod(INIT_METHOD, initValues);
      return result;
    } on PlatformException {
      return false;
    }
  }

  static Future<bool?> loadInterstitialAd({
    String placementId = "YOUR_PLACEMENT_ID",
    Function(InterstitialAdResult, dynamic)? listener,
  }) async {
    return await FacebookInterstitialAd.loadInterstitialAd(
      placementId: placementId,
      listener: listener,
    );
  }

  static Future<bool?> showInterstitialAd({int? delay}) async {
    return await FacebookInterstitialAd.showInterstitialAd(delay: delay);
  }

  static Future<bool?> destroyInterstitialAd() async {
    return await FacebookInterstitialAd.destroyInterstitialAd();
  }

  static Future<bool?> loadRewardedVideoAd({
    String placementId = "YOUR_PLACEMENT_ID",
    Function(RewardedVideoAdResult, dynamic)? listener,
  }) async {
    return await FacebookRewardedVideoAd.loadRewardedVideoAd(
      placementId: placementId,
      listener: listener,
    );
  }

  static Future<bool?> showRewardedVideoAd({int delay = 0}) async {
    return await FacebookRewardedVideoAd.showRewardedVideoAd(delay: delay);
  }

  static Future<bool?> destroyRewardedVideoAd() async {
    return await FacebookRewardedVideoAd.destroyRewardedVideoAd();
  }
}
