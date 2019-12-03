package com.inforall.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SyncAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 85236;
    public static final String ACTION = "com.inforall.inforall.service.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent i = new Intent(context, ContactSyncService.class);
//        Toast.makeText(context,"Received Alarm ",Toast.LENGTH_LONG).show();
////        i.putExtra("foo", "bar");
//        context.startService(i);

        ContactSyncService.startActionFoo(context,"FOO","BAR");
    }
}
