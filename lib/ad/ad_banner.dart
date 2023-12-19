import 'package:botify_ads/constants.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';


class BannerSize {
  final int width;
  final int height;

  static const BannerSize STANDARD = BannerSize(width: 320, height: 50);
  static const BannerSize LARGE = BannerSize(width: 320, height: 90);
  static const BannerSize MEDIUM_RECTANGLE =
      BannerSize(width: 320, height: 250);

  const BannerSize({this.width = 320, this.height = 50});
}

enum BannerAdResult {
   ERROR,
   LOADED,
   CLICKED,
   LOGGING_IMPRESSION,
}

class FacebookBannerAd extends StatefulWidget {
  final Key? key;

  final String placementId;

  final BannerSize bannerSize;

  final void Function(BannerAdResult, dynamic)? listener;

  final bool keepAlive;

  const FacebookBannerAd({
    this.key,
    this.placementId = "YOUR_PLACEMENT_ID",
    this.bannerSize = BannerSize.STANDARD,
    this.listener,
    this.keepAlive = false,
  }) : super(key: key);

  @override
  _FacebookBannerAdState createState() => _FacebookBannerAdState();
}

class _FacebookBannerAdState extends State<FacebookBannerAd>
    with AutomaticKeepAliveClientMixin {
  double containerHeight = 0.5;

  @override
  bool get wantKeepAlive => widget.keepAlive;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    if (defaultTargetPlatform == TargetPlatform.android) {
      return Container(
        height: containerHeight,
        color: Colors.transparent,
        child: AndroidView(
          viewType: BANNER_AD_CHANNEL,
          onPlatformViewCreated: _onBannerAdViewCreated,
          creationParams: <String, dynamic>{
            "id": widget.placementId,
            "width": widget.bannerSize.width,
            "height": widget.bannerSize.height,
          },
          creationParamsCodec: StandardMessageCodec(),
        ),
      );
    } else {
      return SizedBox(
        height: widget.bannerSize.height <= -1
            ? double.infinity
            : widget.bannerSize.height.toDouble(),
        child: const Center(
          child:
              Text("Banner Ads for this platform is currently not supported"),
        ),
      );
    }
  }

  void _onBannerAdViewCreated(int id) async {
    final channel = MethodChannel('${BANNER_AD_CHANNEL}_$id');

    channel.setMethodCallHandler((MethodCall call) {
      switch (call.method) {
        case ERROR_METHOD:
          if (widget.listener != null) {
            widget.listener!(BannerAdResult.ERROR, call.arguments);
          }
          break;
        case LOADED_METHOD:
          setState(() {
            containerHeight = widget.bannerSize.height <= -1
                ? double.infinity
                : widget.bannerSize.height.toDouble();
          });
          if (widget.listener != null) {
            widget.listener!(BannerAdResult.LOADED, call.arguments);
          }
          break;
        case CLICKED_METHOD:
          if (widget.listener != null) {
            widget.listener!(BannerAdResult.CLICKED, call.arguments);
          }
          break;
        case LOGGING_IMPRESSION_METHOD:
          if (widget.listener != null) {
            widget.listener!(BannerAdResult.LOGGING_IMPRESSION, call.arguments);
          }
          break;
      }
      return Future<dynamic>.value(true);
    });
  }
}
