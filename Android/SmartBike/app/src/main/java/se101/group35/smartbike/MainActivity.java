package se101.group35.smartbike;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_ENABLE_BT = 1;

    private WebView mapWebView;
    private TextView statusTextView;
    private Button pairButton;
    private EventReceiver eventReceiver;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapWebView = findViewById(R.id.mapWebView);
        mapWebView.loadUrl(getString(R.string.MAP_INTERFACE_URL));
        mapWebView.getSettings().setJavaScriptEnabled(true);

        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);

        statusTextView = findViewById(R.id.statusTextView);

        pairButton = findViewById(R.id.pairButton);
        pairButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // TODO: open pairing activity
                sendBroadcast(new Intent(BackgroundService.DEVICE_CONNECTION_STATUS));
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                if (device.getAddress().equals(getString(R.string.BLUETOOTH_DEVICE_ADDRESS)))
                {
                    pairButton.setText("Paired!");
                    pairButton.setEnabled(false);
                    break;
                    // TODO: check when unpaired and re-enable button
                }
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (eventReceiver == null) eventReceiver = new EventReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackgroundService.DEVICE_CONNECTED);
        intentFilter.addAction(BackgroundService.DEVICE_DISCONNECTED);
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
        }
    }
}
