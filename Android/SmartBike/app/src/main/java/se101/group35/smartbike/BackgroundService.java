package se101.group35.smartbike;

import android.app.Service;
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

public class BackgroundService extends Service
{
    public static final String DEVICE_CONNECTED = "DEVICE_CONNECTED";
    public static final String DEVICE_DISCONNECTED = "DEVICE_DISCONNECTED";
    public static final String DEVICE_CONNECTION_STATUS = "DEVICE_CONNECTION_STATUS";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private EventReceiver eventReceiver;
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

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        if (eventReceiver == null) eventReceiver = new EventReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackgroundService.DEVICE_CONNECTION_STATUS);
        registerReceiver(eventReceiver, intentFilter);

        return START_STICKY;
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
            // TODO: start
            sendBroadcast(new Intent(DEVICE_CONNECTED));
            try{Thread.sleep(10000);}catch(Exception e){}
            sendBroadcast(new Intent(DEVICE_DISCONNECTED));

            //stopSelf(msg.arg1);
        }
    }

    private class EventReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (action.equals(BackgroundService.DEVICE_CONNECTION_STATUS))
            {
                sendBroadcast(new Intent(isConnected ? DEVICE_CONNECTED : DEVICE_DISCONNECTED));
            }
        }
    }
}
