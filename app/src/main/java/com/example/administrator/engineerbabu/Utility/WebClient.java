package com.example.administrator.engineerbabu.Utility;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.administrator.engineerbabu.WebViewFragment;

public class WebClient extends WebChromeClient {
    private ProgressListener mListener;

    public WebClient(WebViewFragment listener) {
        mListener = (ProgressListener) listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mListener.onUpdateProgress(newProgress);
        super.onProgressChanged(view, newProgress);
    }

    public interface ProgressListener {
        public void onUpdateProgress(int progressValue);
    }
}