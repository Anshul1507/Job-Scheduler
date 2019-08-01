package com.practice.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class ExampleJobService extends JobService {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;

    private Handler mJobHandler = new Handler( new Handler.Callback() {
        @Override
        public boolean handleMessage( Message msg ) {
            Toast.makeText( getApplicationContext(), "JobService task running", Toast.LENGTH_SHORT ).show();
            jobFinished( (JobParameters) msg.obj, false );
            return true;
        }
    } );

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job Started ");
        mJobHandler.sendMessage(Message.obtain(mJobHandler,1,jobParameters));
//        doBackgroundWork(jobParameters);

        return true;
    }

    private void doBackgroundWork(final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                   for(int i=0;i<10;i++){
                       Log.d(TAG, "run: " + i);
                       if(jobCancelled){
                           return;
                       }

                       try {
                           Thread.sleep(1000);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
                Log.d(TAG, "Job Finished");
                   jobFinished(params,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job Cancelled before completion");
//        jobCancelled = true;
        mJobHandler.removeMessages( 1 );
        return false;
    }
}
