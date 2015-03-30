package com.hitherejoe.hackernews.util;

import android.view.View;
import android.webkit.WebView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CustomMatcher {

    public static Matcher<View> showsUrl(final String url) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) { }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof WebView)) {
                    return false;
                }
                WebView webView = (WebView) view;
                return webView.getUrl().contains(url);
            }
        };
    }

}
