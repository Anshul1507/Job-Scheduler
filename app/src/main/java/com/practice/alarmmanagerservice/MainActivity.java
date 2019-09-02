package com.practice.alarmmanagerservice;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public boolean dailyNotify = true;
    private BroadcastReceiver mNetworkReceiver;

    int Request_code_for_usage = 0;

    /** FOR USAGE */
    String TAG = "Usage Read Acitvity";
    DateFormat formatter;
    long totalTimeUsageByNetflix=0;
    long totalTimeUsageByInstagram=0;
    SharedPreferences.Editor editor ;

    void pickContact() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if(mode == AppOpsManager.MODE_ALLOWED){
            //Toast.makeText(this, "Permission already given", Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), Request_code_for_usage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickContact();



        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this,MyBroadCastReceiver.class);


        Intent alarmIntent = new Intent(this,AlarmReceiver.class);
//        alarmIntent.setClass(getApplicationContext(), AlarmReceiver.class);
//        alarmIntent.putExtra("instagramTime",totalTimeUsageByInstagram);
//        alarmIntent.putExtra("netflixTime",totalTimeUsageByNetflix);
        alarmIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,alarmIntent,0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(dailyNotify){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY,22);
            calendar.set(Calendar.MINUTE,51);
            calendar.set(Calendar.SECOND,30);

            if(calendar.before(Calendar.getInstance())) {
                System.out.println("FIX TIME ");
                mNetworkReceiver = new AlarmReceiver();
                registerNetworkBroadcastForNougat();
                calendar.add(Calendar.DATE,1);
            }

            if(manager != null){
                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,pendingIntent);

//


                usageRead();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                }else{
                    manager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                }
            }

            //to enable boot receiver class
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }


    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    public void usageRead() {
        editor = getApplicationContext().getSharedPreferences("GETUSAGE", MODE_PRIVATE).edit();
        UsageStatsManager mUsageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }
        Calendar calendar = Calendar.getInstance();
        long startD = calendar.getTimeInMillis();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDDate = formatter.format(startD);
        long start = startD - 1000;
        try {
            start = new SimpleDateFormat("yyyy-MM-dd").parse(startDDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        String startDate = formatter.format(start);

        long end = System.currentTimeMillis();
//        String endDate = formatter.format(end);
//        System.out.println("START " + startDate);
//        System.out.println("END " + endDate);

        assert mUsageStatsManager != null;
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.
                queryAndAggregateUsageStats(start, end);

        if (isPackageExisted("com.instagram.android")) {
//            System.out.println("Instagram installed");
            try {
                totalTimeUsageByInstagram = Objects.requireNonNull(lUsageStatsMap.get("com.instagram.android")).
                        getTotalTimeInForeground() / 1000;
                editor.putLong("timeInstagram",totalTimeUsageByInstagram);
//                Base.getInstance().setUsageInstagram(String.valueOf(totalTimeUsageByInstagram));
//                Log.d(TAG, "time by instagram: " + totalTimeUsageByInstagram);
            }
            catch(Exception e) {
//                System.out.println("ERROR IN TRY Instagram");
                e.printStackTrace();
            }

        } else {
            editor.putLong("timeInstagram",totalTimeUsageByInstagram);
//            Base.getInstance().setUsageInstagram(String.valueOf(totalTimeUsageByInstagram));
//            System.out.println("Instagram not installed");
//            Log.d(TAG, "Instagram not installed ");

        }

        // TODO:Facebook is deprecated for now
        /**
         //        if (isPackageExisted("com.facebook.katana")) {
         //            try {
         //                System.out.println("Facebook installed");
         //                long totalTimeUsageByFacebook = Objects.requireNonNull(lUsageStatsMap.get("com.facebook.katana")).
         //                        getTotalTimeInForeground() / 1000;
         //            }
         //            catch(Exception e) {
         //                System.out.println("ERROR IN TRY Facebook");
         //                e.printStackTrace();
         //            }
         //
         //        } else {
         //            System.out.println("Facebook not installed");
         //
         //        }
         **/

        if (isPackageExisted("com.netflix.mediaclient")) {
            try {
//                System.out.println("Netflix installed");
                totalTimeUsageByNetflix = Objects.requireNonNull(lUsageStatsMap.get("com.netflix.mediaclient")).
                        getTotalTimeInForeground() / 1000;
                editor.putLong("timeNetflix",totalTimeUsageByNetflix);
//                Base.getInstance().setUsageInstagram(String.valueOf(totalTimeUsageByNetflix));
//                Log.d(TAG, "time taken by netflix: " + totalTimeUsageByNetflix);
            }
            catch(Exception e) {
//                System.out.println("ERROR IN TRY Netflix");
                e.printStackTrace();
            }

        } else {
            editor.putLong("timeNetflix",totalTimeUsageByNetflix);
//            Base.getInstance().setUsageInstagram(String.valueOf(totalTimeUsageByNetflix));
//            System.out.println("Netflix not installed");
//            Log.d(TAG, "Netflix not installed ");

        }

        editor.apply();
        editor.commit();
        System.out.println("End of program");
    }
    public boolean isPackageExisted(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }
}