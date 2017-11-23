package se101.group35.smartbike;

import android.annotation.TargetApi;
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

import java.util.Set;

public class BackgroundService extends Service
{
    public static final String DEVICE_CONNECTED = "DEVICE_CONNECTED";
    public static final String DEVICE_DISCONNECTED = "DEVICE_DISCONNECTED";
    public static final String DEVICE_CONNECTION_STATUS = "DEVICE_CONNECTION_STATUS";
    private static final int BLUETOOTH_SCANNING_TIMEOUT =  15 * 1000; // in milliseconds
    private static final int BLUETOOTH_SCANNING_DELAY = 1 * 60 * 1000; // in milliseconds

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private EventReceiver eventReceiver;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
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

    private void startIsMovingLoop()
    {
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
//            sendBroadcast(new Intent(DEVICE_CONNECTED));
//            try{Thread.sleep(10000);}catch(Exception e){}
//            sendBroadcast(new Intent(DEVICE_DISCONNECTED));

            //stopSelf(msg.arg1);
        }
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
                if (device.getAddress().equals(R.string.BLUETOOTH_DEVICE_ADDRESS))
                {
                    bluetoothAdapter.cancelDiscovery();
                    // TODO: connect
                }
            }
        }
    }
}
