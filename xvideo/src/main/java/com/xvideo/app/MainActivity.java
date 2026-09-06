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
        "if(window.__xv2)return;window.__xv2=1;" +
        "var st=document.createElement('style');st.id='xv-style';st.textContent=`" +
        "html,body{background:#000!important;overscroll-behavior:none!important;}" +
        "header,[data-testid=\\\"BottomBar\\\"],[data-testid=\\\"sidebarColumn\\\"],[data-testid=\\\"SideNav_NewTweet_Button\\\"],[data-testid=\\\"FloatingActionButtons_Tweet_Button\\\"],nav[role=\\\"navigation\\\"]{display:none!important;}" +
        "main{width:100%!important;max-width:none!important;margin:0!important;}" +
        "main>div,main>div>div,section{max-width:none!important;width:100%!important;}" +
        "article[data-testid=\\\"tweet\\\"]{box-sizing:border-box!important;width:100vw!important;height:100dvh!important;min-height:100dvh!important;margin:0!important;padding:0!important;border:0!important;background:#000!important;overflow:hidden!important;scroll-snap-align:start!important;position:relative!important;}" +
        "article[data-testid=\\\"tweet\\\"]>div{height:100%!important;padding:0!important;}" +
        "article[data-testid=\\\"tweet\\\"] [data-testid=\\\"tweetText\\\"],article[data-testid=\\\"tweet\\\"] [data-testid=\\\"User-Name\\\"],article[data-testid=\\\"tweet\\\"] [role=\\\"group\\\"]{display:none!important;}" +
        "article[data-testid=\\\"tweet\\\"] video{position:absolute!important;inset:0!important;width:100vw!important;height:100dvh!important;max-height:none!important;object-fit:contain!important;background:#000!important;}" +
        "article[data-testid=\\\"tweet\\\"] [data-testid=\\\"videoPlayer\\\"],article[data-testid=\\\"tweet\\\"] [data-testid=\\\"videoComponent\\\"]{position:absolute!important;inset:0!important;width:100vw!important;height:100dvh!important;max-height:none!important;background:#000!important;}" +
        "article[data-testid=\\\"tweet\\\"] [data-testid=\\\"videoPlayer\\\"]>div,article[data-testid=\\\"tweet\\\"] [data-testid=\\\"videoComponent\\\"]>div{height:100%!important;max-height:none!important;}" +
        "#xv-sound{position:fixed;right:18px;bottom:28px;z-index:2147483647;width:52px;height:52px;border-radius:50%;border:0;background:rgba(20,20,20,.72);color:white;font-size:23px;}" +
        "`;document.head.appendChild(st);" +
        "function scroller(){var a=document.querySelector('article[data-testid=\\\"tweet\\\"]');if(!a)return;var p=a.parentElement;while(p&&p!==document.body){var cs=getComputedStyle(p);if((cs.overflowY==='auto'||cs.overflowY==='scroll')&&p.scrollHeight>p.clientHeight){p.style.scrollSnapType='y mandatory';p.style.overscrollBehavior='none';return;}p=p.parentElement;}document.documentElement.style.scrollSnapType='y mandatory';}" +
        "function process(){document.querySelectorAll('article[data-testid=\\\"tweet\\\"]').forEach(function(a){var has=a.querySelector('video,[data-testid=\\\"videoPlayer\\\"],[data-testid=\\\"videoComponent\\\"]');a.style.display=has?'block':'none';});scroller();}" +
        "var io=new IntersectionObserver(function(es){es.forEach(function(e){var v=e.target.querySelector('video');if(!v)return;if(e.isIntersecting&&e.intersectionRatio>=.55){document.querySelectorAll('video').forEach(function(o){if(o!==v)o.pause();});v.muted=true;v.play().catch(function(){});}else v.pause();});},{threshold:[0,.55,1]});" +
        "function bind(){document.querySelectorAll('article[data-testid=\\\"tweet\\\"]').forEach(function(a){if(!a.dataset.xv){a.dataset.xv='1';io.observe(a);}});process();}" +
        "new MutationObserver(function(){bind();}).observe(document.body,{childList:true,subtree:true});setInterval(bind,900);bind();" +
        "var b=document.createElement('button');b.id='xv-sound';b.textContent='🔇';b.onclick=function(){var v=[].slice.call(document.querySelectorAll('video')).find(function(x){var r=x.getBoundingClientRect();return r.top<innerHeight*.65&&r.bottom>innerHeight*.35;});if(v){v.muted=!v.muted;b.textContent=v.muted?'🔇':'🔊';}};document.body.appendChild(b);" +
        "})();";

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(android.graphics.Color.BLACK);
        getWindow().setNavigationBarColor(android.graphics.Color.BLACK);
        webView=new WebView(this); setContentView(webView);
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(false); s.setUseWideViewPort(false); s.setTextZoom(100);
        String ua=s.getUserAgentString(); if(ua!=null)s.setUserAgentString(ua.replace("; wv",""));
        CookieManager cm=CookieManager.getInstance(); cm.setAcceptCookie(true); cm.setAcceptThirdPartyCookies(webView,true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){@Override public void onPageFinished(WebView v,String u){super.onPageFinished(v,u);v.evaluateJavascript(FILTER_JS,null);}});
        webView.loadUrl("https://x.com/home");
    }
    @Override public void onBackPressed(){if(webView!=null&&webView.canGoBack())webView.goBack();else super.onBackPressed();}
}
