package com.mixads.botify_ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


public class BotifyAdsPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel, interstitialAdChannel, rewardedAdChannel;
    private Activity _activity;
    private Context _context;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), FacebookConstants.MAIN_CHANNEL);
        channel.setMethodCallHandler(this);
        _context = flutterPluginBinding.getApplicationContext();

        // Interstitial Ad channel
        interstitialAdChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), FacebookConstants.INTERSTITIAL_AD_CHANNEL);
        interstitialAdChannel.setMethodCallHandler(new FacebookInterstitialAdPlugin(_context, interstitialAdChannel));

        // Rewarded video Ad channel
        rewardedAdChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), FacebookConstants.REWARDED_VIDEO_CHANNEL);
        rewardedAdChannel.setMethodCallHandler(new FacebookRewardedVideoAdPlugin(_context, rewardedAdChannel));

        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(FacebookConstants.BANNER_AD_CHANNEL, new FacebookBannerAdPlugin(flutterPluginBinding.getBinaryMessenger()));
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(FacebookConstants.MY_NATIVE_AD_CHANNEL, new MyNativeAdPlugin(flutterPluginBinding.getBinaryMessenger()));
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals(FacebookConstants.INIT_METHOD))
            result.success(init((HashMap) call.arguments));
        else result.notImplemented();
    }

    private boolean init(HashMap initValues) {
        final String testingId = (String) initValues.get("testingId");
        AudienceNetworkAds.initialize(_activity.getApplicationContext());
        if (testingId != null) {ō
            AdSettings.addTestDevice(testingId);
            AdSettings.turnOnSDKDebugger(_activity.getApplicationContext());
            AdSettings.setTestMode(true);
            AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE);
        }
        return true;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        interstitialAdChannel.setMethodCallHandler(null);
        rewardedAdChannel.setMethodCallHandler(null);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        _activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }
}
