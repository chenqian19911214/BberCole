package com.bber.company.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.bber.company.android.R;


public class ProgressWebView extends WebView {

    private ProgressBar progressbar;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);

        progressbar.setProgressDrawable(getResources().getDrawable(R.drawable.icon_progressbar));
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                6, 0, 0));
        progressbar.setId(R.id.webprogress);
        addView(progressbar);
        // setWebViewClient(new WebViewClient(){});
//		setWebChromeClient(new WebChromeClient());
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
