package com.shorthunter.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private WebView webView;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.addJavascriptInterface(new MarketBridge(), "NativeMarket");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/index.html");
        setContentView(webView);
    }

    public class MarketBridge {
        @JavascriptInterface
        public void get(final String requestId, final String url) {
            executor.execute(() -> {
                String result;
                try {
                    result = httpGet(url);
                } catch (Exception e) {
                    result = "__ERROR__" + e.getClass().getSimpleName() + ": " + e.getMessage();
                }
                final String js = "window.__nativeResult(" + JSONObject.quote(requestId) + "," + JSONObject.quote(result) + ");";
                runOnUiThread(() -> webView.evaluateJavascript(js, null));
            });
        }
    }

    private String httpGet(String target) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(target).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(12000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android) ShortHunter/2.0");
        conn.setRequestProperty("Accept", "application/json,text/plain,*/*");
        int code = conn.getResponseCode();
        InputStream in = code >= 200 && code < 400 ? conn.getInputStream() : conn.getErrorStream();
        if (in == null) throw new RuntimeException("HTTP " + code);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        conn.disconnect();
        if (code < 200 || code >= 400) throw new RuntimeException("HTTP " + code + " " + sb);
        return sb.toString();
    }

    @Override protected void onDestroy() {
        executor.shutdownNow();
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
