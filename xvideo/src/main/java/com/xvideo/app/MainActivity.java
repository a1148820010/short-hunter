package com.xvideo.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;

    private static final String FILTER_JS = "javascript:(function(){" +
            "if(window.__xVideoOnlyInstalled)return;window.__xVideoOnlyInstalled=true;" +
            "var css=document.createElement('style');css.innerHTML='html,body{background:#000!important;} header,[data-testid=\\\"sidebarColumn\\\"]{display:none!important;} article[data-testid=\\\"tweet\\\"]{min-height:100vh!important;scroll-snap-align:start!important;background:#000!important;} main{scroll-snap-type:y mandatory!important;} video{width:100%!important;max-height:100vh!important;object-fit:contain!important;background:#000!important;}';document.head.appendChild(css);" +
            "function process(){document.querySelectorAll('article[data-testid=\\\"tweet\\\"]').forEach(function(a){var has=a.querySelector('video,[data-testid=\\\"videoPlayer\\\"],[data-testid=\\\"videoComponent\\\"]');a.style.display=has?'':'none';});}" +
            "var io=new IntersectionObserver(function(es){es.forEach(function(e){var v=e.target.querySelector('video');if(!v)return;if(e.isIntersecting&&e.intersectionRatio>=0.6){v.muted=true;v.play().catch(function(){});}else{v.pause();}});},{threshold:[0,0.6,1]});" +
            "function bind(){document.querySelectorAll('article[data-testid=\\\"tweet\\\"]').forEach(function(a){if(a.dataset.xvb)return;a.dataset.xvb='1';io.observe(a);});process();}" +
            "new MutationObserver(bind).observe(document.body,{childList:true,subtree:true});setInterval(bind,1200);bind();" +
            "var b=document.createElement('button');b.textContent='🔇';b.style='position:fixed;right:18px;bottom:28px;z-index:999999;width:54px;height:54px;border-radius:27px;border:0;background:rgba(0,0,0,.65);color:#fff;font-size:24px';b.onclick=function(){var vs=[].slice.call(document.querySelectorAll('video'));var v=vs.find(function(x){var r=x.getBoundingClientRect();return r.top<innerHeight&&r.bottom>0;})||vs[0];if(v){v.muted=!v.muted;b.textContent=v.muted?'🔇':'🔊';}};document.body.appendChild(b);" +
            "})();";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        String ua = s.getUserAgentString();
        if (ua != null) s.setUserAgentString(ua.replace("; wv", ""));

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(webView, true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.evaluateJavascript(FILTER_JS, null);
            }
        });
        webView.loadUrl("https://x.com/home");
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
