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
    private final ExecutorService executor = Executors.newFixedThreadPool(6);

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
        @JavascriptInterface public void get(final String requestId, final String url) {
            executor.execute(() -> {
                String result;
                try { result = httpGetWithRetry(url, 3); }
                catch (Exception e) { result = "__ERROR__" + e.getClass().getSimpleName() + ": " + e.getMessage(); }
                final String js = "window.__nativeResult(" + JSONObject.quote(requestId) + "," + JSONObject.quote(result) + ");";
                runOnUiThread(() -> webView.evaluateJavascript(js, null));
            });
        }
    }

    private String httpGetWithRetry(String target, int attempts) throws Exception {
        Exception last = null;
        for (int i=0;i<attempts;i++) {
            try { return httpGet(target); }
            catch (Exception e) {
                last = e;
                try { Thread.sleep(450L * (i + 1)); } catch (InterruptedException ignored) {}
            }
        }
        throw last != null ? last : new RuntimeException("请求失败");
    }

    private String httpGet(String target) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(target).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(9000);
        conn.setReadTimeout(12000);
        conn.setUseCaches(false);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 16) AppleWebKit/537.36 Chrome/131 Mobile Safari/537.36 ShortHunter/3.0");
        conn.setRequestProperty("Accept", "application/json,text/plain,*/*");
        conn.setRequestProperty("Accept-Encoding", "identity");
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("Referer", "https://quote.eastmoney.com/");
        int code = conn.getResponseCode();
        InputStream in = code >= 200 && code < 400 ? conn.getInputStream() : conn.getErrorStream();
        if (in == null) { conn.disconnect(); throw new RuntimeException("HTTP " + code); }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close(); conn.disconnect();
        if (code < 200 || code >= 400) throw new RuntimeException("HTTP " + code);
        if (sb.length() == 0) throw new RuntimeException("服务器返回空数据");
        return sb.toString();
    }

    @Override protected void onDestroy() {
        executor.shutdownNow();
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
