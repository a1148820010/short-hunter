package com.xvideo.app;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;
    private EditText searchBox;

    private static final String FILTER_JS = "javascript:(function(){" +
        "if(window.__xv3)return;window.__xv3=1;" +
        "var st=document.createElement('style');st.id='xv-style';st.textContent=`" +
        "html,body{margin:0!important;padding:0!important;background:#000!important;overscroll-behavior:none!important;}" +
        "body{overflow-x:hidden!important;}" +
        "[data-testid=\\\"TopNavBar\\\"],[data-testid=\\\"BottomBar\\\"],[data-testid=\\\"sidebarColumn\\\"],[data-testid=\\\"SideNav_NewTweet_Button\\\"],[data-testid=\\\"FloatingActionButtons_Tweet_Button\\\"],nav[role=\\\"navigation\\\"]{display:none!important;}" +
        "main,main>div,main>div>div,section{width:100%!important;max-width:none!important;margin:0!important;padding:0!important;}" +
        "article[data-testid=\\\"tweet\\\"]{box-sizing:border-box!important;width:100vw!important;height:100dvh!important;min-height:100dvh!important;max-height:100dvh!important;margin:0!important;padding:0!important;border:0!important;background:#000!important;overflow:hidden!important;scroll-snap-align:start!important;position:relative!important;}" +
        "article[data-testid=\\\"tweet\\\"] video{display:block!important;position:absolute!important;inset:0!important;width:100vw!important;height:100dvh!important;min-width:100vw!important;min-height:100dvh!important;max-width:none!important;max-height:none!important;margin:0!important;padding:0!important;object-fit:contain!important;background:#000!important;z-index:10!important;}" +
        "#xv-sound{position:fixed;right:18px;bottom:28px;z-index:2147483647;width:54px;height:54px;border-radius:50%;border:0;background:rgba(20,20,20,.72);color:#fff;font-size:24px;}" +
        "`;document.head.appendChild(st);" +
        "function normalizeArticle(a){" +
          "var v=a.querySelector('video');if(!v)return false;" +
          "a.style.display='block';" +
          "var r=a.getBoundingClientRect();a.style.transform='translateX('+(-r.left)+'px)';a.style.width=innerWidth+'px';" +
          "var n=v;while(n&&n!==a){n.style.setProperty('position','static','important');n.style.setProperty('width','100%','important');n.style.setProperty('height','100%','important');n.style.setProperty('max-width','none','important');n.style.setProperty('max-height','none','important');n.style.setProperty('margin','0','important');n.style.setProperty('padding','0','important');n=n.parentElement;}" +
          "function prune(parent){Array.from(parent.children).forEach(function(c){if(c===v||c.contains(v)){c.style.setProperty('display','block','important');if(c!==v)prune(c);}else{c.style.setProperty('display','none','important');}});}prune(a);" +
          "v.controls=false;return true;" +
        "}" +
        "function hideChrome(){document.querySelectorAll('body *').forEach(function(el){if(el.id==='xv-sound'||el.closest&&el.closest('article[data-testid=\\\"tweet\\\"]'))return;var p=getComputedStyle(el).position;if(p==='fixed'||p==='sticky'){el.style.setProperty('display','none','important');}});}" +
        "function scroller(){var a=document.querySelector('article[data-testid=\\\"tweet\\\"]');if(!a)return;var p=a.parentElement;while(p&&p!==document.body){var cs=getComputedStyle(p);if((cs.overflowY==='auto'||cs.overflowY==='scroll')&&p.scrollHeight>p.clientHeight){p.style.scrollSnapType='y mandatory';p.style.overscrollBehavior='none';return;}p=p.parentElement;}document.documentElement.style.scrollSnapType='y mandatory';}" +
        "function process(){document.querySelectorAll('article[data-testid=\\\"tweet\\\"]').forEach(function(a){var has=a.querySelector('video');if(has){normalizeArticle(a);}else{a.style.setProperty('display','none','important');}});hideChrome();scroller();}" +
        "var io=new IntersectionObserver(function(es){es.forEach(function(e){var v=e.target.querySelector('video');if(!v)return;if(e.isIntersecting&&e.intersectionRatio>=.55){document.querySelectorAll('video').forEach(function(o){if(o!==v)o.pause();});v.muted=true;v.play().catch(function(){});}else v.pause();});},{threshold:[0,.55,1]});" +
        "function bind(){document.querySelectorAll('article[data-testid=\\\"tweet\\\"]').forEach(function(a){if(!a.dataset.xv){a.dataset.xv='1';io.observe(a);}});process();}" +
        "new MutationObserver(function(){bind();}).observe(document.body,{childList:true,subtree:true});setInterval(bind,900);window.addEventListener('resize',process);bind();" +
        "var b=document.createElement('button');b.id='xv-sound';b.textContent='🔇';b.onclick=function(){var v=[].slice.call(document.querySelectorAll('video')).find(function(x){var r=x.getBoundingClientRect();return r.top<innerHeight*.65&&r.bottom>innerHeight*.35;});if(v){v.muted=!v.muted;b.textContent=v.muted?'🔇':'🔊';}};document.body.appendChild(b);" +
        "})();";

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void runSearch() {
        String q = searchBox.getText().toString().trim();
        if (q.isEmpty()) return;
        String url = "https://x.com/search?q=" + Uri.encode(q) + "&src=typed_query&f=live";
        webView.loadUrl(url);
        searchBox.clearFocus();
    }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getWindow().getDecorView().setSystemUiVisibility(0);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setFitsSystemWindows(false);

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.setPadding(dp(8), dp(6), dp(8), dp(6));
        searchRow.setBackgroundColor(Color.rgb(16,16,16));

        searchBox = new EditText(this);
        searchBox.setSingleLine(true);
        searchBox.setTextColor(Color.WHITE);
        searchBox.setHintTextColor(Color.GRAY);
        searchBox.setHint("搜索 X 视频");
        searchBox.setTextSize(16f);
        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBox.setPadding(dp(12), 0, dp(12), 0);
        LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(0, dp(44), 1f);
        searchRow.addView(searchBox, searchLp);

        Button searchBtn = new Button(this);
        searchBtn.setText("搜索");
        searchBtn.setTextSize(14f);
        searchBtn.setOnClickListener(v -> runSearch());
        searchRow.addView(searchBtn, new LinearLayout.LayoutParams(dp(72), dp(44)));

        Button homeBtn = new Button(this);
        homeBtn.setText("首页");
        homeBtn.setTextSize(14f);
        homeBtn.setOnClickListener(v -> { searchBox.setText(""); webView.loadUrl("https://x.com/home"); });
        searchRow.addView(homeBtn, new LinearLayout.LayoutParams(dp(72), dp(44)));

        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { runSearch(); return true; }
            return false;
        });

        webView=new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        webView.setFitsSystemWindows(false);
        root.addView(searchRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(56)));
        root.addView(webView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int left, top, right, bottom;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    android.graphics.Insets sys = insets.getInsets(WindowInsets.Type.systemBars());
                    left=sys.left; top=sys.top; right=sys.right; bottom=sys.bottom;
                } else {
                    left=insets.getSystemWindowInsetLeft(); top=insets.getSystemWindowInsetTop(); right=insets.getSystemWindowInsetRight(); bottom=insets.getSystemWindowInsetBottom();
                }
                v.setPadding(left,top,right,bottom);
                return insets;
            }
        });
        setContentView(root);
        root.requestApplyInsets();

        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(false); s.setUseWideViewPort(false); s.setTextZoom(100);
        String ua=s.getUserAgentString(); if(ua!=null)s.setUserAgentString(ua.replace("; wv",""));
        CookieManager cm=CookieManager.getInstance(); cm.setAcceptCookie(true); cm.setAcceptThirdPartyCookies(webView,true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){@Override public void onPageFinished(WebView v,String u){super.onPageFinished(v,u);v.evaluateJavascript(FILTER_JS,null);}});
        webView.loadUrl("https://x.com/home");
    }

    @Override public void onBackPressed(){
        if(webView!=null&&webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
