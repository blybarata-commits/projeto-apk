package br.com.diario.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.WebViewListener;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends BridgeActivity {
    private final AtomicBoolean firstPageReady = new AtomicBoolean(false);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> !firstPageReady.get());
        super.onCreate(savedInstanceState);

        if (bridge == null) {
            firstPageReady.set(true);
            return;
        }

        WebView webView = bridge.getWebView();
        if (webView != null) {
            webView.setBackgroundColor(Color.BLACK);
            if (webView.getProgress() >= 100
                && webView.getUrl() != null
                && !"about:blank".equals(webView.getUrl())) {
                firstPageReady.set(true);
            }
            bridge.addWebViewListener(new WebViewListener() {
                @Override
                public void onPageLoaded(WebView loadedView) {
                    firstPageReady.set(true);
                }
            });
        } else {
            firstPageReady.set(true);
        }

        // Avoid leaving the native splash up indefinitely if the WebView cannot load.
        new Handler(Looper.getMainLooper()).postDelayed(() -> firstPageReady.set(true), 20000);

        View content = findViewById(android.R.id.content);
        if (content != null) {
            ViewCompat.setOnApplyWindowInsetsListener(content, (view, windowInsets) -> {
                Insets safeInsets = windowInsets.getInsets(
                        WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
                );
                view.setPadding(safeInsets.left, safeInsets.top, safeInsets.right, safeInsets.bottom);
                return windowInsets;
            });
            ViewCompat.requestApplyInsets(content);
        }
    }
}
