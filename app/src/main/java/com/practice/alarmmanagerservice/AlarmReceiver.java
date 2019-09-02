package com.practice.alarmmanagerservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "BR" ;
    long timeInstagram=0,timeNetflix=0;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences("GETUSAGE", context.MODE_PRIVATE);
         timeInstagram = prefs.getLong("timeInstagram",0);
         timeNetflix = prefs.getLong("timeNetflix",0);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

/**                         *********** FOR NOTIFICATION ***************
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "Daily Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Daily Notification");
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
 */

        try {

            if (isOnline(context)) {
                Log.d(TAG, "Data : "
                        + "\n" +
                        "Usage By Instagram : " + timeInstagram
                        + "\n" +
                        "Usage By Netflix : " + timeNetflix
                );
                Toast.makeText(context, "Data Set into DB Current Online", Toast.LENGTH_SHORT).show();



//                    NotificationCompat.Builder b = new NotificationCompat.Builder(context, "default");
//                    b.setAutoCancel(true)
//                            .setDefaults(NotificationCompat.DEFAULT_ALL)
//                            .setWhen(System.currentTimeMillis())
//                            .setSmallIcon(R.mipmap.ic_launcher_round)
//                            .setTicker("{Time to watch some cool stuff!}")
//                            .setContentTitle("My Cool App")
//                            .setContentText("Time to watch some cool stuff!")
//                            .setContentInfo("INFO")
//                            .setContentIntent(pendingI);
//
//                    if (nm != null) {
//                        nm.notify(1, b.build());
//                        Calendar nextNotifyTime = Calendar.getInstance();
//                        nextNotifyTime.add(Calendar.DATE, 1);
//                    }
            } else {

                Toast.makeText(context, "Network issue", Toast.LENGTH_SHORT).show();


                submitDataLater(context);
                Log.d(TAG, "onReceive: Network Issue");
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }



    private void submitDataLater(final Context context) {

        Log.d(TAG, "submitDataLater: ---------------->" );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isOnline(context)){
                    Log.d(TAG, "Data : "
                            + "\n" +
                            "Usage By Instagram : " + timeInstagram
                            + "\n" +
                            "Usage By Netflix : " + timeNetflix
                    );
                    Toast.makeText(context, "Data Set into DB After Online", Toast.LENGTH_SHORT).show();
                }else{
                    submitDataLater(context);
                }
            }
        },5000);


    }

    private boolean isOnline(Context context){
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (networkInfo != null && networkInfo.isConnected());
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }
}
