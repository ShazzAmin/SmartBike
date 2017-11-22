package se101.group35.smartbike;

import android.app.IntentService;
import android.content.Intent;

public class BackgroundService extends IntentService
{
    public static final String ACTION_START = "se101.group35.smartbike.action.START";
    public static final String DEVICE_CONNECTED = "DEVICE_CONNECTED";
    public static final String DEVICE_DISCONNECTED = "DEVICE_DISCONNECTED";
    public static final String DEVICE_MOVED = "DEVICE_MOVED";

    public BackgroundService()
    {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (action.equals(ACTION_START))
            {
                start();
            }
        }
    }

    private void start()
    {
        // TODO: start
        sendBroadcast(new Intent(DEVICE_CONNECTED));
        try{Thread.sleep(15000);}catch(Exception e){}
        sendBroadcast(new Intent(DEVICE_DISCONNECTED));
    }
}