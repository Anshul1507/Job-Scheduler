package com.practice.jobscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

    }

//    public void scheduleJob(View v) {
//        ComponentName componentName = new ComponentName(this,ExampleJobService.class);
//        JobInfo info = new JobInfo.Builder(123,componentName)
//                .setRequiresCharging(true)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                .setPersisted(true)
//                .setPeriodic(
//                         16*60*1000)
//                .build();
//
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        int resultCode = scheduler.schedule(info);
//        if(resultCode == JobScheduler.RESULT_SUCCESS){
//            Log.d(TAG, "Job Scheduled ");
//        }else{
//            Log.d(TAG, "Job Scheduling Failed");
//        }
//    }

//    public void cancelJob(View v) {
////        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
////        scheduler.cancel(123);
////        Log.d(TAG, "Job Cancelled");
////    }

    public void scheduleJob(View v){
        JobInfo.Builder builder = new JobInfo.Builder(1,
                new ComponentName(getPackageName(),ExampleJobService.class.getName()));

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            builder.setTriggerContentMaxDelay(2000);
//        }
        builder.setPeriodic(3000);
        if(mJobScheduler.schedule(builder.build()) <=0){
            //if something goes wrong
            Log.d(TAG, "Something goes wrong!!");
        }

    }

    public void cancelJob(View v) {
        mJobScheduler.cancelAll();
    }
}
