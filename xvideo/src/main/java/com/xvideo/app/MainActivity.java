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
        "if(window.__xplan9)return;window.__xplan9=1;" +
        "var currentSpeed=1,seeking=false,activeV=null,activeA=null,pending=new Set(),raf=0;" +
        "var st=document.createElement('style');st.id='xp-style';st.textContent=`" +
        "html,body,#react-root,main{margin:0!important;padding:0!important;background:#fff!important;overscroll-behavior:none!important;color:#111!important;}body{overflow-x:hidden!important;}" +
        "[data-testid=\\\"TopNavBar\\\"],[data-testid=\\\"BottomBar\\\"],[data-testid=\\\"sidebarColumn\\\"],[data-testid=\\\"SideNav_NewTweet_Button\\\"],[data-testid=\\\"FloatingActionButtons_Tweet_Button\\\"],nav[role=\\\"navigation\\\"]{display:none!important;}" +
        "main,main>div,main>div>div,section{width:100%!important;max-width:none!important;margin:0!important;padding:0!important;background:#fff!important;}" +
        "article[data-testid=\\\"tweet\\\"]{box-sizing:border-box!important;width:100vw!important;height:100dvh!important;min-height:100dvh!important;max-height:100dvh!important;margin:0!important;padding:0!important;border:0!important;background:#fff!important;overflow:hidden!important;scroll-snap-align:start!important;scroll-snap-stop:always!important;position:relative!important;contain:layout paint style!important;}" +
        "article[data-xp-media=\\\"1\\\"] video,article[data-xp-media=\\\"1\\\"] [data-testid=\\\"tweetPhoto\\\"] img{display:block!important;position:absolute!important;inset:0!important;width:100vw!important;height:100dvh!important;max-width:none!important;max-height:none!important;margin:0!important;padding:0!important;object-fit:contain!important;background:#fff!important;z-index:10!important;transform:translateZ(0);}" +
        ".xp-play{position:absolute!important;left:50%!important;top:50%!important;transform:translate(-50%,-50%)!important;z-index:2147483646!important;width:74px!important;height:74px!important;border-radius:50%!important;border:1px solid rgba(0,0,0,.10)!important;background:rgba(255,255,255,.92)!important;color:#111!important;font-size:30px!important;display:flex!important;align-items:center!important;justify-content:center!important;box-shadow:0 8px 30px rgba(0,0,0,.16)!important;backdrop-filter:blur(12px)!important;}" +
        ".xp-play.xp-playing{opacity:.22!important;}" +
        "#xp-controls{display:none;position:fixed;left:14px;right:14px;bottom:14px;z-index:2147483647;padding:9px 12px 8px;background:rgba(255,255,255,.92);border:1px solid rgba(0,0,0,.08);border-radius:18px;box-sizing:border-box;font-family:sans-serif;box-shadow:0 6px 24px rgba(0,0,0,.10);backdrop-filter:blur(14px);}" +
        "#xp-row{display:flex;align-items:center;gap:10px;height:34px;}#xp-progress{flex:1;min-width:0;height:30px;accent-color:#111;}#xp-time{color:#333;font-size:12px;min-width:78px;text-align:right;white-space:nowrap;}" +
        "#xp-actions{display:none;position:fixed;right:16px;bottom:80px;z-index:2147483647;gap:8px;}#xp-actions button{height:42px;min-width:48px;padding:0 13px;border:1px solid rgba(0,0,0,.08);border-radius:21px;background:rgba(255,255,255,.92);color:#111;font-size:14px;font-weight:700;box-shadow:0 5px 18px rgba(0,0,0,.10);}" +
        "`;document.head.appendChild(st);" +
        "function fmt(s){if(!isFinite(s)||s<0)s=0;var m=Math.floor(s/60),q=Math.floor(s%60);return m+':'+(q<10?'0':'')+q;}" +
        "function mediaOf(a){var v=a.querySelector('video');if(v)return {type:'video',el:v};var im=a.querySelector('[data-testid=\\\"tweetPhoto\\\"] img');if(im)return {type:'image',el:im};return null;}" +
        "function syncBtn(v,a){var b=a&&a.querySelector('.xp-play');if(!b)return;if(v.paused){b.textContent='▶';b.classList.remove('xp-playing');}else{b.textContent='❚❚';b.classList.add('xp-playing');}}" +
        "function updateProgress(){if(!activeV||seeking)return;var d=activeV.duration;if(isFinite(d)&&d>0){range.value=Math.round(activeV.currentTime/d*1000);time.textContent=fmt(activeV.currentTime)+' / '+fmt(d);}else{range.value=0;time.textContent='0:00 / 0:00';}}" +
        "function showVideoControls(show){controls.style.display=show?'block':'none';actions.style.display=show?'flex':'none';}" +
        "function setActive(v,a){if(activeV&&activeV!==v){activeV.removeEventListener('timeupdate',updateProgress);activeV.removeEventListener('durationchange',updateProgress);}activeV=v;activeA=a;if(!v){showVideoControls(false);return;}showVideoControls(true);v.playbackRate=currentSpeed;v.addEventListener('timeupdate',updateProgress);v.addEventListener('durationchange',updateProgress);updateProgress();syncBtn(v,a);}" +
        "function prepVideo(v,near){if(!v)return;v.controls=false;v.autoplay=false;v.removeAttribute('autoplay');v.playsInline=true;v.setAttribute('playsinline','');v.preload=near?'auto':'metadata';if(v.dataset.xpPrepared!=='1'){v.dataset.xpPrepared='1';v.pause();}}" +
        "function addPlay(a,v){if(a.querySelector('.xp-play'))return;var b=document.createElement('button');b.className='xp-play';b.textContent='▶';b.setAttribute('aria-label','播放/暂停');b.onclick=function(e){e.preventDefault();e.stopPropagation();setActive(v,a);if(v.paused){document.querySelectorAll('video').forEach(function(o){if(o!==v&&!o.paused)o.pause();});v.playbackRate=currentSpeed;v.play().catch(function(){});}else v.pause();syncBtn(v,a);};a.appendChild(b);v.addEventListener('play',function(){syncBtn(v,a);});v.addEventListener('pause',function(){syncBtn(v,a);});}" +
        "function isolate(a,el){var n=el;while(n&&n!==a){n.style.setProperty('position','static','important');n.style.setProperty('width','100%','important');n.style.setProperty('height','100%','important');n.style.setProperty('max-width','none','important');n.style.setProperty('max-height','none','important');n.style.setProperty('margin','0','important');n.style.setProperty('padding','0','important');var p=n.parentElement;if(p){Array.from(p.children).forEach(function(c){if(c!==n&&!c.classList.contains('xp-play'))c.style.setProperty('display','none','important');});}n=p;}}" +
        "function bind(a){if(!a||a.dataset.xpBound==='1')return;var m=mediaOf(a);if(!m)return;a.dataset.xpBound='1';a.dataset.xpMedia='1';a.style.setProperty('display','block','important');var r=a.getBoundingClientRect();a.style.transform='translateX('+(-r.left)+'px) translateZ(0)';a.style.width=innerWidth+'px';isolate(a,m.el);if(m.type==='video'){prepVideo(m.el,false);m.el.pause();addPlay(a,m.el);}io.observe(a);nearIo.observe(a);setupScroller();}" +
        "function inspect(a,attempt){if(!a||!a.isConnected||a.dataset.xpBound==='1')return;var m=mediaOf(a);if(m){bind(a);return;}if(attempt<5){setTimeout(function(){inspect(a,attempt+1);},700);}else{a.style.setProperty('display','none','important');}}" +
        "function queue(a){if(!a||a.nodeType!==1)return;var ar=a.matches&&a.matches('article[data-testid=\\\"tweet\\\"]')?a:(a.closest?a.closest('article[data-testid=\\\"tweet\\\"]'):null);if(ar)pending.add(ar);if(a.querySelectorAll)a.querySelectorAll('article[data-testid=\\\"tweet\\\"]').forEach(function(x){pending.add(x);});if(!raf)raf=requestAnimationFrame(flush);}" +
        "function flush(){raf=0;var arr=Array.from(pending);pending.clear();arr.forEach(function(a){inspect(a,0);});}" +
        "function setupScroller(){if(window.__xpScroller)return;var a=document.querySelector('article[data-xp-bound=\\\"1\\\"]');if(!a)return;var p=a.parentElement;while(p&&p!==document.body){var cs=getComputedStyle(p);if((cs.overflowY==='auto'||cs.overflowY==='scroll')&&p.scrollHeight>p.clientHeight){p.style.scrollSnapType='y mandatory';p.style.overscrollBehavior='contain';window.__xpScroller=p;return;}p=p.parentElement;}document.documentElement.style.scrollSnapType='y mandatory';window.__xpScroller=document.documentElement;}" +
        "var io=new IntersectionObserver(function(es){es.forEach(function(e){var a=e.target,m=mediaOf(a);if(!m)return;if(m.type==='video'){prepVideo(m.el,e.isIntersecting&&e.intersectionRatio>.35);if(e.isIntersecting&&e.intersectionRatio>=.55)setActive(m.el,a);if(e.intersectionRatio<.15&&!m.el.paused)m.el.pause();}else if(e.isIntersecting&&e.intersectionRatio>=.55){setActive(null,a);}});},{threshold:[0,.15,.35,.55,.85]});" +
        "var nearIo=new IntersectionObserver(function(es){es.forEach(function(e){if(!e.isIntersecting)return;var m=mediaOf(e.target);if(m&&m.type==='video')prepVideo(m.el,true);if(m&&m.type==='image'){m.el.loading='eager';m.el.decoding='async';}});},{rootMargin:'70% 0px 70% 0px',threshold:0});" +
        "new MutationObserver(function(ms){ms.forEach(function(mu){mu.addedNodes.forEach(function(n){if(n.nodeType===1)queue(n);});var a=mu.target&&mu.target.closest?mu.target.closest('article[data-testid=\\\"tweet\\\"]'):null;if(a&&a.dataset.xpBound!=='1')pending.add(a);});if(pending.size&&!raf)raf=requestAnimationFrame(flush);}).observe(document.body,{childList:true,subtree:true});" +
        "document.querySelectorAll('article[data-testid=\\\"tweet\\\"]').forEach(function(a){pending.add(a);});flush();" +
        "window.addEventListener('resize',function(){window.__xpScroller=null;document.querySelectorAll('article[data-xp-bound=\\\"1\\\"]').forEach(function(a){a.style.width=innerWidth+'px';});setupScroller();},{passive:true});" +
        "var actions=document.createElement('div');actions.id='xp-actions';var speed=document.createElement('button');speed.textContent='1x';var sound=document.createElement('button');sound.textContent='🔇';actions.appendChild(speed);actions.appendChild(sound);document.body.appendChild(actions);" +
        "var controls=document.createElement('div');controls.id='xp-controls';var row=document.createElement('div');row.id='xp-row';var range=document.createElement('input');range.id='xp-progress';range.type='range';range.min='0';range.max='1000';range.value='0';range.step='1';var time=document.createElement('span');time.id='xp-time';time.textContent='0:00 / 0:00';row.appendChild(range);row.appendChild(time);controls.appendChild(row);document.body.appendChild(controls);" +
        "sound.onclick=function(){if(activeV){activeV.muted=!activeV.muted;sound.textContent=activeV.muted?'🔇':'🔊';}};var speeds=[0.5,1,1.5,2],si=1;speed.onclick=function(){si=(si+1)%speeds.length;currentSpeed=speeds[si];speed.textContent=currentSpeed+'x';if(activeV)activeV.playbackRate=currentSpeed;};" +
        "range.addEventListener('touchstart',function(){seeking=true;},{passive:true});range.addEventListener('mousedown',function(){seeking=true;});range.addEventListener('input',function(){if(activeV&&isFinite(activeV.duration)&&activeV.duration>0){activeV.currentTime=range.value/1000*activeV.duration;time.textContent=fmt(activeV.currentTime)+' / '+fmt(activeV.duration);}});function done(){seeking=false;updateProgress();}range.addEventListener('change',done);range.addEventListener('touchend',done,{passive:true});range.addEventListener('mouseup',done);" +
        "})();";

    private int dp(int v){ return (int)(v * getResources().getDisplayMetrics().density + .5f); }

    private GradientDrawable rounded(int color,int radius){
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }

    private GradientDrawable outlined(int fill,int stroke,int radius){
        GradientDrawable d=rounded(fill,radius); d.setStroke(dp(1),stroke); return d;
    }

    private Button makeButton(String text,int textColor,int bgColor){
        Button b=new Button(this); b.setText(text); b.setTextColor(textColor); b.setTextSize(14f); b.setAllCaps(false);
        b.setGravity(Gravity.CENTER); b.setPadding(dp(10),0,dp(10),0); b.setBackground(rounded(bgColor,14)); return b;
    }

    private void runSearch(){
        String q=searchBox.getText().toString().trim(); if(q.isEmpty())return;
        webView.loadUrl("https://x.com/search?q="+Uri.encode(q)+"&src=typed_query&f=live"); searchBox.clearFocus();
    }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.WHITE); getWindow().setNavigationBarColor(Color.WHITE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.WHITE); root.setFitsSystemWindows(false);
        LinearLayout bar=new LinearLayout(this); bar.setOrientation(LinearLayout.HORIZONTAL); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(dp(10),dp(8),dp(10),dp(8)); bar.setBackgroundColor(Color.WHITE);

        searchBox=new EditText(this); searchBox.setSingleLine(true); searchBox.setTextColor(Color.rgb(20,20,22)); searchBox.setHintTextColor(Color.rgb(130,130,136));
        searchBox.setHint("搜索图片、视频、用户或话题"); searchBox.setTextSize(15f); searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH); searchBox.setPadding(dp(14),0,dp(14),0);
        searchBox.setBackground(outlined(Color.rgb(247,247,249),Color.rgb(228,228,232),14)); bar.addView(searchBox,new LinearLayout.LayoutParams(0,dp(44),1f));

        Button searchBtn=makeButton("搜索",Color.WHITE,Color.rgb(15,15,17)); LinearLayout.LayoutParams slp=new LinearLayout.LayoutParams(dp(66),dp(44)); slp.setMargins(dp(8),0,0,0); searchBtn.setOnClickListener(v->runSearch()); bar.addView(searchBtn,slp);
        Button homeBtn=makeButton("首页",Color.rgb(35,35,38),Color.rgb(242,242,245)); LinearLayout.LayoutParams hlp=new LinearLayout.LayoutParams(dp(62),dp(44)); hlp.setMargins(dp(7),0,0,0); homeBtn.setOnClickListener(v->{searchBox.setText("");webView.loadUrl("https://x.com/home");}); bar.addView(homeBtn,hlp);
        searchBox.setOnEditorActionListener((v,a,e)->{if(a==EditorInfo.IME_ACTION_SEARCH){runSearch();return true;}return false;});

        webView=new WebView(this); webView.setBackgroundColor(Color.WHITE); webView.setFitsSystemWindows(false); webView.setVerticalScrollBarEnabled(false); webView.setHorizontalScrollBarEnabled(false); webView.setOverScrollMode(View.OVER_SCROLL_NEVER); webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        root.addView(bar,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dp(60))); root.addView(webView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1f));
        root.setOnApplyWindowInsetsListener((v,insets)->{int l,t,r,bt;if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){android.graphics.Insets s=insets.getInsets(WindowInsets.Type.systemBars());l=s.left;t=s.top;r=s.right;bt=s.bottom;}else{l=insets.getSystemWindowInsetLeft();t=insets.getSystemWindowInsetTop();r=insets.getSystemWindowInsetRight();bt=insets.getSystemWindowInsetBottom();}v.setPadding(l,t,r,bt);return insets;});
        setContentView(root); root.requestApplyInsets();

        WebSettings s=webView.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setMediaPlaybackRequiresUserGesture(false); s.setLoadWithOverviewMode(false); s.setUseWideViewPort(false); s.setTextZoom(100); s.setCacheMode(WebSettings.LOAD_DEFAULT); s.setLoadsImagesAutomatically(true); s.setBlockNetworkImage(false);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) s.setSafeBrowsingEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true); CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){@Override public void onPageFinished(WebView v,String url){super.onPageFinished(v,url);v.evaluateJavascript(FILTER_JS,null);}});
        webView.loadUrl("https://x.com/home");
    }

    @Override public void onBackPressed(){ if(webView!=null&&webView.canGoBack())webView.goBack(); else super.onBackPressed(); }
}
