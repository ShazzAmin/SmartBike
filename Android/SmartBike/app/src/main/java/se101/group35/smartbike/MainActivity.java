package se101.group35.smartbike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private WebView mapWebView;
    private TextView statusTextView;
    private Button pairButton;
    private EventReceiver eventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapWebView = findViewById(R.id.mapWebView);
        mapWebView.loadUrl("https://danielzhang.ddns.net/map.html");
        mapWebView.getSettings().setJavaScriptEnabled(true);

        Intent intent = new Intent(this, BackgroundService.class);
        intent.setAction(BackgroundService.ACTION_START);
        startService(intent);

        statusTextView = findViewById(R.id.statusTextView);

        pairButton = findViewById(R.id.pairButton);
        pairButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // TODO: open pairing activity
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (eventReceiver == null) eventReceiver = new EventReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackgroundService.DEVICE_CONNECTED);
        intentFilter.addAction(BackgroundService.DEVICE_DISCONNECTED);
        intentFilter.addAction(BackgroundService.DEVICE_MOVED);
        registerReceiver(eventReceiver, intentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (eventReceiver != null) unregisterReceiver(eventReceiver);
    }

    private class EventReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (action.equals(BackgroundService.DEVICE_CONNECTED))
            {
                statusTextView.setText("device connected");
                System.out.println("device connected");
            }
            else if (action.equals(BackgroundService.DEVICE_DISCONNECTED))
            {
                statusTextView.setText("device disconnected");
                System.out.println("device disconnected");
            }
            else if (action.equals(BackgroundService.DEVICE_MOVED))
            {
                statusTextView.setText("device moved");
                System.out.println("device moved");
            }
        }
    }
}
