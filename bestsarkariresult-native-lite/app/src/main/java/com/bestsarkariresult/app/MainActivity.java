package com.bestsarkariresult.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.Continue;
import com.onesignal.OneSignal;

public class MainActivity extends Activity {
    private static final String SITE_URL = "https://bestsarkariresult.com/";
    private WebView webView;
    private View loadingOverlay;
    private long loadingStartedAt;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        configureSystemBars();

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(7, 29, 61));
        applySafeInsets(root);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(244, 247, 251));
        root.addView(webView, new FrameLayout.LayoutParams(-1, -1));
        loadingOverlay = createLoadingScreen();
        root.addView(loadingOverlay, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
        configureWebView();
        loadingStartedAt = System.currentTimeMillis();
        webView.loadUrl(getNotificationUrl(getIntent()));
        root.postDelayed(this::showNotificationPromptIfNeeded, 1400);
    }

    private void showNotificationPromptIfNeeded() {
        if (isFinishing() || getPreferences(MODE_PRIVATE).getBoolean("notification_prompt_accepted", false)) return;
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.app_icon)
                .setTitle("नई अपडेट की सूचना पाएँ")
                .setMessage("नई सरकारी नौकरी, Result और Admit Card प्रकाशित होते ही notification पाने के लिए Allow करें।")
                .setNegativeButton("बाद में", null)
                .setPositiveButton("Allow", (dialog, which) -> enableNotifications())
                .show();
    }

    private void enableNotifications() {
        getPreferences(MODE_PRIVATE).edit().putBoolean("notification_prompt_accepted", true).apply();
        OneSignal.getNotifications().requestPermission(true, Continue.none());
        Toast.makeText(this, "Notification permission चालू करें", Toast.LENGTH_SHORT).show();
    }

    private String getNotificationUrl(Intent intent) {
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null && "bestsarkariresult".equalsIgnoreCase(data.getScheme())
                    && "open".equalsIgnoreCase(data.getHost())) {
                String deepLinkUrl = data.getQueryParameter("url");
                if (isAllowedSiteUrl(deepLinkUrl)) return deepLinkUrl;
            }

            if (data != null && isAllowedSiteUrl(data.toString())) {
                return data.toString();
            }

            String url = intent.getStringExtra("post_url");
            if (isAllowedSiteUrl(url)) return url;
        }
        return SITE_URL;
    }

    private boolean isAllowedSiteUrl(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        try {
            Uri uri = Uri.parse(url.trim());
            String host = uri.getHost();
            return "https".equalsIgnoreCase(uri.getScheme())
                    && ("bestsarkariresult.com".equalsIgnoreCase(host)
                    || "www.bestsarkariresult.com".equalsIgnoreCase(host));
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String url = getNotificationUrl(intent);
        if (webView != null) webView.loadUrl(url);
    }

    private View createLoadingScreen() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(android.view.Gravity.CENTER);
        screen.setPadding(dp(28), dp(28), dp(28), dp(28));
        screen.setBackgroundColor(Color.rgb(244, 247, 251));

        ImageView logo = new ImageView(this);
        logo.setImageResource(com.bestsarkariresult.app.R.drawable.app_icon);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(dp(132), dp(132));
        logoParams.bottomMargin = dp(20);
        screen.addView(logo, logoParams);

        TextView welcome = new TextView(this);
        welcome.setText("Welcome to Best Sarkari Result");
        welcome.setTextColor(Color.rgb(7, 29, 61));
        welcome.setTextSize(22);
        welcome.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        welcome.setGravity(android.view.Gravity.CENTER);
        screen.addView(welcome, new LinearLayout.LayoutParams(-1, -2));

        TextView hindi = new TextView(this);
        hindi.setText("सरकारी नौकरी, Result, Admit Card और महत्वपूर्ण updates — एक ही स्थान पर।");
        hindi.setTextColor(Color.rgb(55, 77, 101));
        hindi.setTextSize(14);
        hindi.setGravity(android.view.Gravity.CENTER);
        hindi.setLineSpacing(0, 1.18f);
        LinearLayout.LayoutParams hindiParams = new LinearLayout.LayoutParams(-1, -2);
        hindiParams.setMargins(dp(10), dp(12), dp(10), dp(22));
        screen.addView(hindi, hindiParams);

        ProgressBar progress = new ProgressBar(this);
        progress.setIndeterminate(true);
        screen.addView(progress, new LinearLayout.LayoutParams(dp(42), dp(42)));

        TextView loading = new TextView(this);
        loading.setText("नवीनतम जानकारी लोड हो रही है...");
        loading.setTextColor(Color.rgb(210, 13, 34));
        loading.setTextSize(13);
        loading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        loading.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams loadingParams = new LinearLayout.LayoutParams(-1, -2);
        loadingParams.topMargin = dp(12);
        screen.addView(loading, loadingParams);
        return screen;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void hideLoadingScreen() {
        if (loadingOverlay == null || loadingOverlay.getVisibility() != View.VISIBLE) return;
        long remaining = Math.max(0, 900 - (System.currentTimeMillis() - loadingStartedAt));
        loadingOverlay.postDelayed(() -> loadingOverlay.animate().alpha(0f).setDuration(260).withEndAction(() -> {
            loadingOverlay.setVisibility(View.GONE);
        }).start(), remaining);
    }

    private void configureSystemBars() {
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(7, 29, 61));
        window.setNavigationBarColor(Color.rgb(7, 29, 61));
        if (Build.VERSION.SDK_INT >= 30) window.setDecorFitsSystemWindows(true);
    }

    private void applySafeInsets(View root) {
        if (Build.VERSION.SDK_INT >= 21) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                int left, top, right, bottom;
                if (Build.VERSION.SDK_INT >= 30) {
                    android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                    left = bars.left; top = bars.top; right = bars.right; bottom = bars.bottom;
                } else {
                    left = insets.getSystemWindowInsetLeft(); top = insets.getSystemWindowInsetTop();
                    right = insets.getSystemWindowInsetRight(); bottom = insets.getSystemWindowInsetBottom();
                }
                view.setPadding(left, top, right, bottom);
                return insets;
            });
        }
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);
        settings.setUserAgentString(settings.getUserAgentString() + " BestSarkariResultAndroid/3.0");
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingScreen();
            }

            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("bestsarkariresult.com".equalsIgnoreCase(uri.getHost()) || "www.bestsarkariresult.com".equalsIgnoreCase(uri.getHost())) return false;
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            }
        });
        webView.setDownloadListener((url, userAgent, disposition, mime, length) -> {
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mime);
                request.addRequestHeader("User-Agent", userAgent);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, android.webkit.URLUtil.guessFileName(url, disposition, mime));
                ((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
                Toast.makeText(this, "Download शुरू हो गया", Toast.LENGTH_SHORT).show();
            } catch (Exception e) { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
        });
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (webView != null) { webView.stopLoading(); webView.destroy(); }
        super.onDestroy();
    }
}
