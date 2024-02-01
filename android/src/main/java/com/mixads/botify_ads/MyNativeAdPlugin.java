package com.mixads.botify_ads;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

class MyNativeAdPlugin extends PlatformViewFactory {

    private final BinaryMessenger messenger;

    MyNativeAdPlugin(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object args) {
        return new MyNativeAdView(context, id, (HashMap) args, this.messenger);
    }
}

class MyNativeAdView implements PlatformView {
    View layoutAdView;
    private final MethodChannel channel;
    private final HashMap args;
    private final Context context;
    private NativeAd nativeAd;
    NativeAdLayout native_ad_container;


    MyNativeAdView(Context context, int id, HashMap args, BinaryMessenger messenger) {

        this.channel = new MethodChannel(messenger, FacebookConstants.MY_NATIVE_AD_CHANNEL + "_" + id);
        this.args = args;
        this.context = context;

        nativeAd = new NativeAd(context, (String) this.args.get("id"));
        String adsType = (String) args.get("ads_type");
        Log.e("ads_type", " ------" + adsType);


        layoutAdView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.my_nativ_ads, null);
        native_ad_container = layoutAdView.findViewById(R.id.native_ad_container);


        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.e("Facebook NativeAd Rajjo", " onMediaDownloaded");
                HashMap<String, Object> args = new HashMap<>();
                args.put("placement_id", ad.getPlacementId());
                args.put("invalidated", ad.isAdInvalidated());
                channel.invokeMethod(FacebookConstants.MEDIA_DOWNLOADED_METHOD, args);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("Facebook NativeAd Rajjo", " Error" + adError.getErrorMessage());
                HashMap<String, Object> args = new HashMap<>();
                args.put("placement_id", ad.getPlacementId());
                args.put("invalidated", ad.isAdInvalidated());
                args.put("error_code", adError.getErrorCode());
                args.put("error_message", adError.getErrorMessage());
                channel.invokeMethod(FacebookConstants.ERROR_METHOD, args);

            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e("Facebook NativeAd Rajjo", " onAdLoaded");
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                HashMap<String, Object> argsAd = new HashMap<>();
                argsAd.put("placement_id", ad.getPlacementId());
                argsAd.put("invalidated", ad.isAdInvalidated());
                channel.invokeMethod(FacebookConstants.LOAD_SUCCESS_METHOD, argsAd);
                layoutAdView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (adsType.equals(FacebookConstants.NATIVE_AD_BIG)) {
                            inflateAdBig(nativeAd, native_ad_container, args);
                        } else if (adsType.equals(FacebookConstants.NATIVE_AD_SMALL)) {
                            inflateAdSmall(nativeAd, native_ad_container, args);
                        } else if (adsType.equals(FacebookConstants.NATIVE_AD_BIG_ROW)) {
                            inflateAdBigRow(nativeAd, native_ad_container, args);
                        } else if (adsType.equals(FacebookConstants.NATIVE_AD_FULL_SCREEN)) {
                            inflateAdFullScreen(nativeAd, native_ad_container, args);
                        } else {
                            inflateAdBig(nativeAd, native_ad_container, args);
                        }
                    }
                }, 200);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.e("Facebook NativeAd Rajjo", " onAdClicked");
                HashMap<String, Object> args = new HashMap<>();
                args.put("placement_id", ad.getPlacementId());
                args.put("invalidated", ad.isAdInvalidated());
                channel.invokeMethod(FacebookConstants.CLICKED_METHOD, args);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.e("Facebook NativeAd Rajjo", " onLoggingImpression");
                HashMap<String, Object> args = new HashMap<>();
                args.put("placement_id", ad.getPlacementId());
                args.put("invalidated", ad.isAdInvalidated());
                channel.invokeMethod(FacebookConstants.LOGGING_IMPRESSION_METHOD, args);
            }
        };
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    private void inflateAdBig(NativeAd nativeAd, NativeAdLayout nativeAdLayout, HashMap args) {
        nativeAd.unregisterView();
        LayoutInflater inflater = LayoutInflater.from(context);
        View adView = inflater.inflate(R.layout.ads_nativ_facebook_big, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        RelativeLayout relViewBG = adView.findViewById(R.id.relViewBG);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        ///////////////////////////////////////////////////////////
        if (args.get("bg_color") != null) {
            relViewBG.setBackgroundColor(Color.parseColor((String) args.get("bg_color")));
        }
        if (args.get("title_color") != null) {
            nativeAdTitle.setTextColor(Color.parseColor((String) args.get("title_color")));
        }
        if (args.get("desc_color") != null) {
            sponsoredLabel.setTextColor(Color.parseColor((String) args.get("desc_color")));
            nativeAdSocialContext.setTextColor(Color.parseColor((String) args.get("desc_color")));
            nativeAdBody.setTextColor(Color.parseColor((String) args.get("desc_color")));
        }
        if (args.get("button_color") != null) {
            nativeAdCallToAction.setBackgroundColor(Color.parseColor((String) args.get("button_color")));
        }
        if (args.get("button_title_color") != null) {
            nativeAdCallToAction.setTextColor(Color.parseColor((String) args.get("button_title_color")));
        }
        if (args.get("title_text_size") != null) {
            nativeAdTitle.setTextSize((int) args.get("title_text_size"));
        }
        if (args.get("sponsored_label_text_size") != null) {
            sponsoredLabel.setTextSize((int) args.get("sponsored_label_text_size"));
        }
        if (args.get("social_label_text_size") != null) {
            nativeAdSocialContext.setTextSize((int) args.get("social_label_text_size"));
        }
        if (args.get("body_text_size") != null) {
            nativeAdBody.setTextSize((int) args.get("body_text_size"));
        }
        if (args.get("ad_call_button_text_size") != null) {
            nativeAdCallToAction.setTextSize((int) args.get("ad_call_button_text_size"));
        }
        ///////////////////////////////////////////////////////////

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        clickableViews.add(nativeAdIcon);

        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
        channel.invokeMethod(FacebookConstants.LOADED_METHOD, args);
    }

    private void inflateAdSmall(NativeAd nativeAd, NativeAdLayout nativeAdLayout, HashMap args) {
        nativeAd.unregisterView();
        LayoutInflater inflater = LayoutInflater.from(context);
        View adView = inflater.inflate(R.layout.ads_nativ_facebook_small, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        LinearLayout linViewBG = adView.findViewById(R.id.linViewBG);

        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

        ///////////////////////////////////////////////////////////
        if (args.get("bg_color") != null) {
            linViewBG.setBackgroundColor(Color.parseColor((String) args.get("bg_color")));
        }
        if (args.get("title_color") != null) {
            nativeAdTitle.setTextColor(Color.parseColor((String) args.get("title_color")));
        }
        if (args.get("desc_color") != null) {
            sponsoredLabel.setTextColor(Color.parseColor((String) args.get("desc_color")));
            nativeAdBody.setTextColor(Color.parseColor((String) args.get("desc_color")));
            nativeAdSocialContext.setTextColor(Color.parseColor((String) args.get("desc_color")));
        }
        if (args.get("button_color") != null) {
            nativeAdCallToAction.setBackgroundColor(Color.parseColor((String) args.get("button_color")));
        }
        if (args.get("button_title_color") != null) {
            nativeAdCallToAction.setTextColor(Color.parseColor((String) args.get("button_title_color")));
        }

        if (args.get("title_text_size") != null) {
            nativeAdTitle.setTextSize((int) args.get("title_text_size"));
        }
        if (args.get("sponsored_label_text_size") != null) {
            sponsoredLabel.setTextSize((int) args.get("sponsored_label_text_size"));
        }
        if (args.get("social_label_text_size") != null) {
            nativeAdSocialContext.setTextSize((int) args.get("social_label_text_size"));
        }
        if (args.get("body_text_size") != null) {
            nativeAdBody.setTextSize((int) args.get("body_text_size"));
        }
        if (args.get("ad_call_button_text_size") != null) {
            nativeAdCallToAction.setTextSize((int) args.get("ad_call_button_text_size"));
        }
        ///////////////////////////////////////////////////////////

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        clickableViews.add(nativeAdIcon);

        nativeAd.registerViewForInteraction(adView, nativeAdIcon, clickableViews);
    }

    private void inflateAdBigRow(NativeAd nativeAd, NativeAdLayout nativeAdLayout, HashMap args) {
        nativeAd.unregisterView();
        LayoutInflater inflater = LayoutInflater.from(context);
        View adView = inflater.inflate(R.layout.ads_nativ_facebook_big_row, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        RelativeLayout relViewBG = adView.findViewById(R.id.relViewBG);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        ///////////////////////////////////////////////////////////
        if (args.get("bg_color") != null) {
            relViewBG.setBackgroundColor(Color.parseColor((String) args.get("bg_color")));
        }
        if (args.get("title_color") != null) {
            nativeAdTitle.setTextColor(Color.parseColor((String) args.get("title_color")));
        }
        if (args.get("desc_color") != null) {
            sponsoredLabel.setTextColor(Color.parseColor((String) args.get("desc_color")));
            nativeAdSocialContext.setTextColor(Color.parseColor((String) args.get("desc_color")));
            nativeAdBody.setTextColor(Color.parseColor((String) args.get("desc_color")));
        }
        if (args.get("button_color") != null) {
            nativeAdCallToAction.setBackgroundColor(Color.parseColor((String) args.get("button_color")));
        }
        if (args.get("button_title_color") != null) {
            nativeAdCallToAction.setTextColor(Color.parseColor((String) args.get("button_title_color")));
        }
        if (args.get("title_text_size") != null) {
            nativeAdTitle.setTextSize((int) args.get("title_text_size"));
        }
        if (args.get("sponsored_label_text_size") != null) {
            sponsoredLabel.setTextSize((int) args.get("sponsored_label_text_size"));
        }
        if (args.get("social_label_text_size") != null) {
            nativeAdSocialContext.setTextSize((int) args.get("social_label_text_size"));
        }
        if (args.get("body_text_size") != null) {
            nativeAdBody.setTextSize((int) args.get("body_text_size"));
        }
        if (args.get("ad_call_button_text_size") != null) {
            nativeAdCallToAction.setTextSize((int) args.get("ad_call_button_text_size"));
        }
        ///////////////////////////////////////////////////////////

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        clickableViews.add(nativeAdIcon);

        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
        channel.invokeMethod(FacebookConstants.LOADED_METHOD, args);
    }

    private void inflateAdFullScreen(NativeAd nativeAd, NativeAdLayout nativeAdLayout, HashMap args) {
        nativeAd.unregisterView();
        LayoutInflater inflater = LayoutInflater.from(context);
        View adView = inflater.inflate(R.layout.ads_nativ_facebook_full_screen, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        RelativeLayout relViewBG = adView.findViewById(R.id.relViewBG);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        ///////////////////////////////////////////////////////////
        if (args.get("bg_color") != null) {
            relViewBG.setBackgroundColor(Color.parseColor((String) args.get("bg_color")));
        }
        if (args.get("title_color") != null) {
            nativeAdTitle.setTextColor(Color.parseColor((String) args.get("title_color")));
        }
        if (args.get("desc_color") != null) {
            sponsoredLabel.setTextColor(Color.parseColor((String) args.get("desc_color")));
            nativeAdSocialContext.setTextColor(Color.parseColor((String) args.get("desc_color")));
            nativeAdBody.setTextColor(Color.parseColor((String) args.get("desc_color")));
        }
        if (args.get("button_color") != null) {
            nativeAdCallToAction.setBackgroundColor(Color.parseColor((String) args.get("button_color")));
        }
        if (args.get("button_title_color") != null) {
            nativeAdCallToAction.setTextColor(Color.parseColor((String) args.get("button_title_color")));
        }
        if (args.get("title_text_size") != null) {
            nativeAdTitle.setTextSize((int) args.get("title_text_size"));
        }
        if (args.get("sponsored_label_text_size") != null) {
            sponsoredLabel.setTextSize((int) args.get("sponsored_label_text_size"));
        }
        if (args.get("social_label_text_size") != null) {
            nativeAdSocialContext.setTextSize((int) args.get("social_label_text_size"));
        }
        if (args.get("body_text_size") != null) {
            nativeAdBody.setTextSize((int) args.get("body_text_size"));
        }
        if (args.get("ad_call_button_text_size") != null) {
            nativeAdCallToAction.setTextSize((int) args.get("ad_call_button_text_size"));
        }
        ///////////////////////////////////////////////////////////

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        clickableViews.add(nativeAdIcon);

        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
        channel.invokeMethod(FacebookConstants.LOADED_METHOD, args);
    }

    @Override
    public View getView() {
        return layoutAdView;
    }

    @Override
    public void dispose() {
    }
}
