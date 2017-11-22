package se101.group35.smartbike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView mapWebView = findViewById(R.id.mapWebView);
        mapWebView.loadUrl("https://danielzhang.ddns.net/map.html");
        mapWebView.getSettings().setJavaScriptEnabled(true);

        Intent intent = new Intent(this, BluetoothConnectivity.class);
        startService(intent);

        final TextView statusTextView = findViewById(R.id.statusTextView);

        final Button pairButton = findViewById(R.id.pairButton);
        pairButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // open connect activity
            }
        });
    }
}
