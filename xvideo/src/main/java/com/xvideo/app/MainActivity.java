package com.xvideo.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
        "if(window.__xv5)return;window.__xv5=1;" +
        "var currentSpeed=1.0,seeking=false,activeV=null,activeA=null,rafPending=false;" +
        "var st=document.createElement('style');st.id='xv-style';st.textContent=`" +
        "html,body{margin:0!important;padding:0!important;background:#000!important;overscroll-behavior:none!important;}body{overflow-x:hidden!important;}" +
        "[data-testid=\\\"TopNavBar\\\"],[data-testid=\\\"BottomBar\\\"],[data-testid=\\\"sidebarColumn\\\"],[data-testid=\\\"SideNav_NewTweet_Button\\\"],[data-testid=\\\"FloatingActionButtons_Tweet_Button\\\"],nav[role=\\\"navigation\\\"]{display:none!important;}" +
        "main,main>div,main>div>div,section{width:100%!important;max-width:none!important;margin:0!important;padding:0!important;}" +
        "article[data-testid=\\\"tweet\\\"]{box-sizing:border-box!important;width:100vw!important;height:100dvh!important;min-height:100dvh!important;max-height:100dvh!important;margin:0!important;padding:0!important;border:0!important;background:#000!important;overflow:hidden!important;scroll-snap-align:start!important;scroll-snap-stop:always!important;position:relative!important;contain:layout paint style!important;}" +
        "article[data-testid=\\\"tweet\\\"] video{display:block!important;position:absolute!important;inset:0!important;width:100vw!important;height:100dvh!important;min-width:100vw!important;min-height:100dvh!important;max-width:none!important;max-height:none!important;margin:0!important;padding:0!important;object-fit:contain!important;background:#000!important;z-index:10!important;transform:translateZ(0);}" +
        "#xv-controls{position:fixed;left:12px;right:12px;bottom:12px;z-index:2147483647;padding:9px 10px 8px;background:rgba(12,12,14,.72);backdrop-filter:blur(14px);border:1px solid rgba(255,255,255,.12);border-radius:18px;box-sizing:border-box;font-family:sans-serif;}" +
        "#xv-row{display:flex;align-items:center;gap:9px;height:34px;}#xv-progress{flex:1;min-width:0;height:30px;accent-color:#fff;}#xv-time{color:#eee;font-size:12px;min-width:78px;text-align:right;white-space:nowrap;}" +
        "#xv-actions{position:fixed;right:16px;bottom:78px;z-index:2147483647;display:flex;gap:8px;}#xv-actions button{height:42px;min-width:48px;padding:0 13px;border:1px solid rgba(255,255,255,.14);border-radius:21px;background:rgba(12,12,14,.76);color:#fff;font-size:14px;font-weight:700;backdrop-filter:blur(12px);}" +
        "`;document.head.appendChild(st);" +
        "function fmt(s){if(!isFinite(s)||s<0)s=0;var m=Math.floor(s/60),q=Math.floor(s%60);return m+':'+(q<10?'0':'')+q;}" +
        "function updateProgress(){if(!activeV||seeking)return;var d=activeV.duration;if(isFinite(d)&&d>0){range.value=Math.round((activeV.currentTime/d)*1000);time.textContent=fmt(activeV.currentTime)+' / '+fmt(d);}}" +
        "function setActive(v,a){if(activeV===v)return;if(activeV){activeV.removeEventListener('timeupdate',updateProgress);activeV.removeEventListener('durationchange',updateProgress);}activeV=v;activeA=a;if(!v)return;v.preload='auto';v.playbackRate=currentSpeed;v.addEventListener('timeupdate',updateProgress);v.addEventListener('durationchange',updateProgress);updateProgress();}" +
        "function prepVideo(v,priority){if(!v)return;v.controls=false;v.playsInline=true;v.setAttribute('playsinline','');v.preload=priority?'auto':'metadata';if(priority&&v.readyState<2){try{v.load();}catch(e){}}}" +
        "function normalizeArticle(a){if(a.dataset.xvNorm==='1')return !!a.querySelector('video');var v=a.querySelector('video');if(!v)return false;a.dataset.xvNorm='1';a.style.display='block';var r=a.getBoundingClientRect();a.style.transform='translateX('+(-r.left)+'px) translateZ(0)';a.style.width=innerWidth+'px';var n=v;while(n&&n!==a){n.style.setProperty('position','static','important');n.style.setProperty('width','100%','important');n.style.setProperty('height','100%','important');n.style.setProperty('max-width','none','important');n.style.setProperty('max-height','none','important');n.style.setProperty('margin','0','important');n.style.setProperty('padding','0','important');n=n.parentElement;}function prune(p){Array.from(p.children).forEach(function(c){if(c===v||c.contains(v)){c.style.setProperty('display','block','important');if(c!==v)prune(c);}else c.style.setProperty('display','none','important');});}prune(a);prepVideo(v,false);return true;}" +
        "function setupScroller(){if(window.__xvScroller)return;var a=document.querySelector('article[data-testid=\\\"tweet\\\"]');if(!a)return;var p=a.parentElement;while(p&&p!==document.body){var cs=getComputedStyle(p);if((cs.overflowY==='auto'||cs.overflowY==='scroll')&&p.scrollHeight>p.clientHeight){p.style.scrollSnapType='y mandatory';p.style.overscrollBehavior='none';p.style.webkitOverflowScrolling='touch';window.__xvScroller=p;return;}p=p.parentElement;}document.documentElement.style.scrollSnapType='y mandatory';window.__xvScroller=document.documentElement;}" +
        "var io=new IntersectionObserver(function(es){es.forEach(function(e){var a=e.target,v=a.querySelector('video');if(!v)return;if(e.isIntersecting)prepVideo(v,e.intersectionRatio>.05);if(e.isIntersecting&&e.intersectionRatio>=.55){document.querySelectorAll('video').forEach(function(o){if(o!==v&&!o.paused)o.pause();});setActive(v,a);v.playbackRate=currentSpeed;v.play().catch(function(){});}else if(e.intersectionRatio<.2&&v!==activeV){v.pause();}});},{threshold:[0,.05,.2,.55,.85]});" +
        "var nearIo=new IntersectionObserver(function(es){es.forEach(function(e){if(e.isIntersecting){var v=e.target.querySelector('video');prepVideo(v,true);}});},{rootMargin:'120% 0px 120% 0px',threshold:0});" +
        "function processNew(){rafPending=false;document.querySelectorAll('article[data-testid=\\\"tweet\\\"]:not([data-xv-bound])').forEach(function(a){a.dataset.xvBound='1';var v=a.querySelector('video');if(v){normalizeArticle(a);io.observe(a);nearIo.observe(a);}else a.style.setProperty('display','none','important');});setupScroller();}" +
        "function schedule(){if(rafPending)return;rafPending=true;requestAnimationFrame(processNew);}" +
        "new MutationObserver(schedule).observe(document.body,{childList:true,subtree:true});window.addEventListener('resize',function(){window.__xvScroller=null;schedule();},{passive:true});schedule();" +
        "var actions=document.createElement('div');actions.id='xv-actions';var speed=document.createElement('button');speed.textContent='1x';var sound=document.createElement('button');sound.textContent='🔇';actions.appendChild(speed);actions.appendChild(sound);document.body.appendChild(actions);" +
        "var controls=document.createElement('div');controls.id='xv-controls';var row=document.createElement('div');row.id='xv-row';var range=document.createElement('input');range.id='xv-progress';range.type='range';range.min='0';range.max='1000';range.value='0';range.step='1';var time=document.createElement('span');time.id='xv-time';time.textContent='0:00 / 0:00';row.appendChild(range);row.appendChild(time);controls.appendChild(row);document.body.appendChild(controls);" +
        "sound.onclick=function(){if(activeV){activeV.muted=!activeV.muted;sound.textContent=activeV.muted?'🔇':'🔊';}};" +
        "var speeds=[0.5,1,1.5,2],si=1;speed.onclick=function(){si=(si+1)%speeds.length;currentSpeed=speeds[si];speed.textContent=currentSpeed+'x';if(activeV)activeV.playbackRate=currentSpeed;};" +
        "range.addEventListener('touchstart',function(){seeking=true;},{passive:true});range.addEventListener('mousedown',function(){seeking=true;});range.addEventListener('input',function(){if(activeV&&isFinite(activeV.duration)&&activeV.duration>0){activeV.currentTime=(range.value/1000)*activeV.duration;time.textContent=fmt(activeV.currentTime)+' / '+fmt(activeV.duration);}});function endSeek(){seeking=false;updateProgress();}range.addEventListener('change',endSeek);range.addEventListener('touchend',endSeek,{passive:true});range.addEventListener('mouseup',endSeek);" +
        "})();";

    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

    private GradientDrawable rounded(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    private Button makeButton(String text, int bgColor) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14f);
        b.setAllCaps(false);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(10), 0, dp(10), 0);
        b.setBackground(rounded(bgColor, 14));
        return b;
    }

    private void runSearch() {
        String q = searchBox.getText().toString().trim();
        if (q.isEmpty()) return;
        webView.loadUrl("https://x.com/search?q=" + Uri.encode(q) + "&src=typed_query&f=live");
        searchBox.clearFocus();
    }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) getWindow().getDecorView().setSystemUiVisibility(0);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setFitsSystemWindows(false);

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.setPadding(dp(10), dp(8), dp(10), dp(8));
        searchRow.setBackgroundColor(Color.rgb(8,8,10));

        searchBox = new EditText(this);
        searchBox.setSingleLine(true);
        searchBox.setTextColor(Color.WHITE);
        searchBox.setHintTextColor(Color.rgb(145,145,150));
        searchBox.setHint("搜索视频、用户或话题");
        searchBox.setTextSize(15f);
        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBox.setPadding(dp(14),0,dp(14),0);
        searchBox.setBackground(rounded(Color.rgb(28,28,32), 14));
        searchRow.addView(searchBox,new LinearLayout.LayoutParams(0,dp(44),1f));

        Button searchBtn = makeButton("搜索", Color.rgb(29,155,240));
        LinearLayout.LayoutParams searchBtnLp = new LinearLayout.LayoutParams(dp(66),dp(44));
        searchBtnLp.setMargins(dp(8),0,0,0);
        searchBtn.setOnClickListener(v->runSearch());
        searchRow.addView(searchBtn,searchBtnLp);

        Button homeBtn = makeButton("首页", Color.rgb(42,42,47));
        LinearLayout.LayoutParams homeBtnLp = new LinearLayout.LayoutParams(dp(62),dp(44));
        homeBtnLp.setMargins(dp(7),0,0,0);
        homeBtn.setOnClickListener(v->{searchBox.setText("");webView.loadUrl("https://x.com/home");});
        searchRow.addView(homeBtn,homeBtnLp);

        searchBox.setOnEditorActionListener((v,actionId,event)->{if(actionId==EditorInfo.IME_ACTION_SEARCH){runSearch();return true;}return false;});

        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        webView.setFitsSystemWindows(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        root.addView(searchRow,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dp(60)));
        root.addView(webView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1f));

        root.setOnApplyWindowInsetsListener((v,insets)->{
            int left,top,right,bottom;
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){android.graphics.Insets sys=insets.getInsets(WindowInsets.Type.systemBars());left=sys.left;top=sys.top;right=sys.right;bottom=sys.bottom;}
            else{left=insets.getSystemWindowInsetLeft();top=insets.getSystemWindowInsetTop();right=insets.getSystemWindowInsetRight();bottom=insets.getSystemWindowInsetBottom();}
            v.setPadding(left,top,right,bottom);return insets;
        });
        setContentView(root);
        root.requestApplyInsets();

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(false);
        s.setUseWideViewPort(false);
        s.setTextZoom(100);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) s.setSafeBrowsingEnabled(true);
        String ua=s.getUserAgentString(); if(ua!=null)s.setUserAgentString(ua.replace("; wv",""));

        CookieManager cm=CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(webView,true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override public void onPageFinished(WebView v,String u){super.onPageFinished(v,u);v.evaluateJavascript(FILTER_JS,null);}
        });
        webView.loadUrl("https://x.com/home");
    }

    @Override public void onBackPressed(){if(webView!=null&&webView.canGoBack())webView.goBack();else super.onBackPressed();}
}
