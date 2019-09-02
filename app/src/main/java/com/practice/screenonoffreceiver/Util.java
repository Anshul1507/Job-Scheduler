package com.practice.screenonoffreceiver;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class Util {
    // schedule the start of the service every 10 - 30 seconds
    public static void schedulerJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context,TestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0,serviceComponent);
        builder.setMinimumLatency(1*1000);    // wait at least
        builder.setOverrideDeadline(3*1000);  //delay time
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);  // require unmetered network
        builder.setRequiresCharging(false);  // we don't care if the device is charging or not
        builder.setRequiresDeviceIdle(true); // device should be idle
        System.out.println("(scheduler Job");

        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            jobScheduler = context.getSystemService(JobScheduler.class);
        }
        jobScheduler.schedule(builder.build());
    }
}