package com.local.gmdeck;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(10, 13, 20));
        window.setNavigationBarColor(Color.rgb(10, 13, 20));

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(true);

        webView.setBackgroundColor(Color.rgb(10, 13, 20));
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new DeckBridge(), "GMDeck");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUri(Uri.parse(url));
            }
        });

        setContentView(webView);
        enterImmersiveMode();
        webView.loadUrl("file:///android_asset/index.html");
    }

    private final class DeckBridge {
        @JavascriptInterface
        public void startAmbient(String scene, int volume) {
            Intent intent = new Intent(MainActivity.this, AmbientService.class);
            intent.setAction(AmbientService.ACTION_PLAY);
            intent.putExtra(AmbientService.EXTRA_SCENE, scene);
            intent.putExtra(AmbientService.EXTRA_VOLUME, volume);
            startService(intent);
        }

        @JavascriptInterface
        public void stopAmbient() {
            Intent intent = new Intent(MainActivity.this, AmbientService.class);
            intent.setAction(AmbientService.ACTION_STOP);
            startService(intent);
        }
    }

    private void enterImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enterImmersiveMode();
        }
    }

    private boolean handleUri(Uri uri) {
        String scheme = uri.getScheme();
        if ("gmdeck".equals(scheme)) {
            String host = uri.getHost();
            if ("macrodeck".equals(host)) {
                launchPackage("com.suchbyte.macrodeck", "Macro Deck is not installed");
            } else if ("kdeconnect".equals(host)) {
                launchPackage("org.kde.kdeconnect_tp", "KDE Connect is not installed");
            } else if ("discord".equals(host)) {
                Intent launch = getPackageManager().getLaunchIntentForPackage("com.discord");
                if (launch != null) {
                    startActivity(launch);
                } else {
                    openExternal(Uri.parse("https://discord.com/app"));
                }
            } else if ("browser".equals(host)) {
                launchPackage("org.lineageos.jelly", "Browser is not installed");
            } else if ("settings".equals(host)) {
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
            return true;
        }
        if ("http".equals(scheme) || "https".equals(scheme)) {
            openExternal(uri);
            return true;
        }
        return false;
    }

    private void launchPackage(String packageName, String fallbackMessage) {
        Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch != null) {
            startActivity(launch);
        } else {
            Toast.makeText(this, fallbackMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void openExternal(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException error) {
            Toast.makeText(this, "No browser can open this link", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
