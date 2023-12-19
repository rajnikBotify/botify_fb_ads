import 'dart:io';

import 'package:botify_ads/constants.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

enum MyNativeAdType {
  NATIVE_AD_BIG,
  NATIVE_AD_SMALL,
  NATIVE_AD_BIG_ROW,
  NATIVE_AD_FULL_SCREEN
}

enum MyNativeAdResult {
  ERROR,
  LOADED,
  CLICKED,
  LOGGING_IMPRESSION,
  MEDIA_DOWNLOADED,
}

class MyNativeAd extends StatefulWidget {
  final String placementId;
  final void Function(MyNativeAdResult, dynamic)? listener;
  final MyNativeAdType adType;
  final double width;
  final double height;
  final Color? backgroundColor;
  final Color? titleColor;
  final Color? descriptionColor;
  final Color? buttonColor;
  final Color? buttonTitleColor;
  final bool keepAlive;
  final bool keepExpandedWhileLoading;
  final int expandAnimationDuraion;
  final int titleTextSize;
  final int sponsoredLabelTextSize;
  final int socialLabelTextSize;
  final int bodyTextSize;
  final int adCallButtonTextSize;

  const MyNativeAd({
    Key? key,
    this.placementId = "YOUR_PLACEMENT_ID",
    this.listener,
    required this.adType,
    this.width = double.infinity,
    this.height = 250,
    this.backgroundColor,
    this.titleColor,
    this.descriptionColor,
    this.buttonColor,
    this.buttonTitleColor,
    this.keepAlive = false,
    this.keepExpandedWhileLoading = true,
    this.expandAnimationDuraion = 0,
    this.titleTextSize = 12,
    this.sponsoredLabelTextSize = 10,
    this.socialLabelTextSize = 10,
    this.bodyTextSize = 10,
    this.adCallButtonTextSize = 10,
  }) : super(key: key);

  @override
  _FacebookNativeAdState createState() => _FacebookNativeAdState();
}

class _FacebookNativeAdState extends State<MyNativeAd>
    with AutomaticKeepAliveClientMixin {
  final double containerHeight = Platform.isAndroid ? 1.0 : 0.1;
  bool isAdReady = false;

  @override
  bool get wantKeepAlive => widget.keepAlive;

  Widget build(BuildContext context) {
    super.build(context);
    double width = widget.width == double.infinity
        ? MediaQuery.of(context).size.width
        : widget.width;
    return AnimatedContainer(
      color: Colors.transparent,
      width: width,
      height: isAdReady || widget.keepExpandedWhileLoading
          ? widget.height
          : containerHeight,
      duration: Duration(milliseconds: widget.expandAnimationDuraion),
      child: Stack(
        alignment: Alignment.center,
        children: <Widget>[
          Positioned.fill(
            top: isAdReady || widget.keepExpandedWhileLoading
                ? 0
                : -(widget.height - containerHeight),
            child: ConstrainedBox(
              constraints: BoxConstraints(
                maxHeight: widget.height,
                maxWidth: MediaQuery.of(context).size.width,
              ),
              child: buildPlatformView(width),
            ),
          ),
        ],
      ),
    );
  }

  _getAdsType(MyNativeAdType type) {
    if (type == MyNativeAdType.NATIVE_AD_BIG) {
      return NATIVE_AD_BIG;
    } else if (type == MyNativeAdType.NATIVE_AD_SMALL) {
      return NATIVE_AD_SMALL;
    } else if (type == MyNativeAdType.NATIVE_AD_BIG_ROW) {
      return NATIVE_AD_BIG_ROW;
    } else if (type == MyNativeAdType.NATIVE_AD_FULL_SCREEN) {
      return NATIVE_AD_FULL_SCREEN;
    } else {
      return NATIVE_AD_BIG;
    }
  }

  Widget buildPlatformView(double width) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return SizedBox(
        width: width,
        height: widget.height,
        child: AndroidView(
          viewType: My_NATIVE_AD_CHANNEL,
          onPlatformViewCreated: _onNativeAdViewCreated,
          creationParamsCodec: const StandardMessageCodec(),
          creationParams: <String, dynamic>{
            "id": widget.placementId,
            "ads_type": _getAdsType(widget.adType),
            "bg_color": widget.backgroundColor == null
                ? null
                : _getHexStringFromColor(widget.backgroundColor!),
            "title_color": widget.titleColor == null
                ? null
                : _getHexStringFromColor(widget.titleColor!),
            "desc_color": widget.descriptionColor == null
                ? null
                : _getHexStringFromColor(widget.descriptionColor!),
            "button_color": widget.buttonColor == null
                ? null
                : _getHexStringFromColor(widget.buttonColor!),
            "button_title_color": widget.buttonTitleColor == null
                ? null
                : _getHexStringFromColor(widget.buttonTitleColor!),
            "title_text_size": widget.titleTextSize ?? 10,
            "sponsored_label_text_size": widget.sponsoredLabelTextSize ?? 10,
            "social_label_text_size": widget.socialLabelTextSize ?? 10,
            "body_text_size": widget.bodyTextSize ?? 10,
            "ad_call_button_text_size": widget.adCallButtonTextSize ?? 10,
          },
        ),
      );
    } else {
      return SizedBox(
        width: width,
        height: widget.height,
        child: const Text(
            "Native Ads for this platform is currently not supported"),
      );
    }
  }

  String _getHexStringFromColor(Color color) =>
      '#${color.value.toRadixString(16)}';

  void _onNativeAdViewCreated(int id) {
    final channel = MethodChannel('${My_NATIVE_AD_CHANNEL}_$id');

    channel.setMethodCallHandler((MethodCall call) {
      switch (call.method) {
        case ERROR_METHOD:
          if (widget.listener != null) {
            widget.listener!(MyNativeAdResult.ERROR, call.arguments);
          }
          break;
        case LOADED_METHOD:
          if (widget.listener != null) {
            widget.listener!(MyNativeAdResult.LOADED, call.arguments);
          }

          if (!isAdReady) {
            setState(() {
              isAdReady = true;
            });
          }

          break;
        case LOAD_SUCCESS_METHOD:
          if (!mounted) Future<dynamic>.value(true);
          if (!isAdReady) {
            setState(() {
              isAdReady = true;
            });
          }
          break;
        case CLICKED_METHOD:
          if (widget.listener != null) {
            widget.listener!(MyNativeAdResult.CLICKED, call.arguments);
          }
          break;
        case LOGGING_IMPRESSION_METHOD:
          if (widget.listener != null) {
            widget.listener!(
                MyNativeAdResult.LOGGING_IMPRESSION, call.arguments);
          }
          break;
        case MEDIA_DOWNLOADED_METHOD:
          if (widget.listener != null) {
            widget.listener!(MyNativeAdResult.MEDIA_DOWNLOADED, call.arguments);
          }
          break;
      }
      return Future<dynamic>.value(true);
    });
  }
}
