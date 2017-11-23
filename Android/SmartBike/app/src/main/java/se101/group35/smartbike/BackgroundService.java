package se101.group35.smartbike;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundService extends Service
{
    public static final String DEVICE_CONNECTED = "DEVICE_CONNECTED";
    public static final String DEVICE_DISCONNECTED = "DEVICE_DISCONNECTED";
    public static final String DEVICE_CONNECTION_STATUS = "DEVICE_CONNECTION_STATUS";
    private static final int BLUETOOTH_SCANNING_TIMEOUT =  20 * 1000; // in milliseconds
    private static final int BLUETOOTH_SCANNING_DELAY = 5 * 60 * 1000; // in milliseconds // TODO: update for demo
    private static final int WEBSERVER_POLLING_DELAY = 30 * 1000; // in milliseconds

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private EventReceiver eventReceiver;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    HttpURLConnection connection;
    private boolean isConnected = false;

    public BackgroundService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        handler = new Handler();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        unregisterReceiver(eventReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        if (eventReceiver == null) eventReceiver = new EventReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackgroundService.DEVICE_CONNECTION_STATUS);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(eventReceiver, intentFilter);

        return START_STICKY;
    }

    private void startBluetoothScanningLoop()
    {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.startDiscovery();
            System.out.println("starting discovery");

            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    System.out.println("cancelling discovery");
                    bluetoothAdapter.cancelDiscovery();
                }
            }, BLUETOOTH_SCANNING_TIMEOUT);

            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    startBluetoothScanningLoop();
                }
            }, BLUETOOTH_SCANNING_DELAY);
        }
    }

    private void skipXMLTag(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        if (parser.getEventType() != XmlPullParser.START_TAG) throw new IllegalStateException();
        int depth = 1;
        while (depth != 0)
        {
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private void startIsMovingLoop()
    {
        if (connection != null)
        {
            connection.disconnect();
            connection = null;
        }

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startIsMovingLoop();
            }
        }, WEBSERVER_POLLING_DELAY);

        if (isConnected) return;

        System.out.println("starting connection");

        InputStream in = null;
        try
        {
            connection = (HttpURLConnection) (new URL(getString(R.string.GPS_FILE_URL))).openConnection();
            in = new BufferedInputStream(connection.getInputStream());

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            double lat1 = 0;
            double long1 = 0;
            double lat2 = 0;
            double long2 = 0;
            while (parser.next() != XmlPullParser.END_DOCUMENT)
            {
                if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                String name = parser.getName();
                System.out.println(name);
                if (name.equals("latitude"))
                {
                    if (parser.next() == XmlPullParser.TEXT)
                    {
                        lat1 = lat2;
                        lat2 = Double.parseDouble(parser.getText());
                        parser.nextTag();
                    }
                }
                else if (name.equals("longitude"))
                {
                    if (parser.next() == XmlPullParser.TEXT)
                    {
                        long1 = long2;
                        long2 = Double.parseDouble(parser.getText());
                        parser.nextTag();
                    }
                }
                else if (!name.equals("locations") && !name.equals("location"))
                {
                    skipXMLTag(parser);
                }
            }

            // System.out.println(lat1 + "," + long1 + "," + lat2 + "," + long2);
            if (didMove(lat1, long1, lat2, long2))
            {
                // TODO: Show notification
            }
        }
        catch (Exception e) { }
        finally
        {
            try { if (in != null) in.close(); } catch (Exception e) { }
            if (connection != null) connection.disconnect();
        }
    }

    private final class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            startBluetoothScanningLoop();
            startIsMovingLoop();
            // TODO: start

            // stopSelf(msg.arg1);
        }
    }

    private boolean didMove(double lat1, double long1, double lat2, double long2)
    {
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double haversine = Math.pow(Math.sin(Math.toRadians(lat2 - lat1) / 2), 2) +
            Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(Math.toRadians(long2 - long1) / 2), 2);

        double distance = 6371000 * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

        return distance > 20;
    }

    private class EventReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            System.out.println(action + "==" + BluetoothDevice.ACTION_FOUND);
            if (action.equals(BackgroundService.DEVICE_CONNECTION_STATUS))
            {
                sendBroadcast(new Intent(isConnected ? DEVICE_CONNECTED : DEVICE_DISCONNECTED));
            }
            else if (action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println("Found device: " + device.getName() + " - " + device.getAddress());
                if (device.getAddress().equals(getString(R.string.BLUETOOTH_DEVICE_ADDRESS)))
                {
                    bluetoothAdapter.cancelDiscovery();
                    // TODO: connect
                }
            }
        }
    }
}
