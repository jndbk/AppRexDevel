package rex.login;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.facebook.Facebook;

import rex.login.service.IAppService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Background <code>Service</code> used for managing the list of stocks in a
 * user's portfolio, periodically updating stock price information on those
 * stocks, and publishing the user if stock prices go too high or too low.
 * 
 * @author Michael Galpin
 * 
 */
public class MonitorService extends Service
{
    public class CurAppInfo
    {
        CurAppInfo(int i, long l, long s)
        {
            id = i;
            lastTimeSeen = l;
            started = s;
        }

        public int id;
        public long lastTimeSeen;
        public long started;
    }

    // This is a data access object used for persisting stock information.
    private AppsDb db;
    Map<String, ParseObject> mAppInfos;
    String currentUser;
    HashMap<String, CurAppInfo> currentApps;
    HashMap<String, Long> currentAppLastTimeUpdated;
    private String curPublicizedApp;

    @Override
    public void onCreate()
    {
        currentUser = null;
        currentApps = new HashMap<String, CurAppInfo>();
        Log.d("AppsDb", "Started up service");
        Parse.initialize(this, "gentv1lxXI5DEP7K3kQbfNOOIIUoVSdSwTu8RQ8d",
                "S1BixT6dROphY1hYMn2JRv3BZuNSRZcEibpyeeaj");
        mAppInfos = new HashMap<String, ParseObject>();
        super.onCreate();
        db = new AppsDb(this);
        //android.os.Debug.waitForDebugger();

        Context context = this.getApplicationContext();
        AlarmManager mgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, 2);
        mgr.setRepeating(AlarmManager.RTC, now.getTimeInMillis(), 10 * 1000,
                sender);
        // mgr.setRepeating(AlarmManager.RTC, now.getTimeInMillis(), 120 * 1000,
        // sender);
        Log.d("APPREX", "End start up");
    }

    public String getPackageName(String appName, PackageManager pm)
    {
        try
        {
            List<ApplicationInfo> allApps = pm.getInstalledApplications(0);
            for (ApplicationInfo i : allApps)
            {

                String label = (String) i.loadLabel(pm);
                // Log.d("Market", "From package: " +
                // i.processName + " From info: " + label);
                if (label.equalsIgnoreCase(appName))
                {
                    return i.packageName;
                }
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try
        {
        if (currentUser == null)
        {
            Log.d("AppsDb", "No user id");
        }
        // remove old entries
        long now = new Date().getTime();
        for (String k : currentApps.keySet())
        {
            CurAppInfo cai = currentApps.get(k);
            if (now - cai.lastTimeSeen > 60000)
                currentApps.remove(k);
        }

        List<RunningAppProcessInfo> taskInfo = null;
        try
        {
            ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            // get the info from the currently running task
            taskInfo = am.getRunningAppProcesses();
        }
        catch(Exception e)
        {
            return Service.START_STICKY;           
        }
        for (RunningAppProcessInfo info : taskInfo)
        {
            String aname = null;
            int imp = info.importance;
            if ((imp > 0)
                    && (imp <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND))
            {
                PackageManager pm = this.getPackageManager();
                try
                {
                    CharSequence c = pm.getApplicationLabel(pm
                            .getApplicationInfo(info.processName,
                                    PackageManager.GET_META_DATA));

                    aname = c.toString();
                    if (!aname.equals("System UI")
                            && !aname.equals("Android keyboard")
                            && !aname.equals("Phone")
                            && !aname.equals("Kindle System")
                            && !aname.equals("Status Bar")
                            && !aname.equals("Launcher")
                            && !aname.equals("Android System"))
                    {
                        CurAppInfo curInfo = currentApps.get(aname);
                        if (curInfo == null)
                        {
                            String packageName = this.getPackageName(aname, pm);
                            Log.d("AppsDb", packageName);
                            if(packageName == null)
                                packageName = "";
                            int curId = db.startApp(aname, packageName);
                            Log.d("AppsDb", "Starting new app: " + aname);
                            CurAppInfo ai = new CurAppInfo(curId, now, now);
                            currentApps.put(aname, ai);
                        } else
                        {
                            db.updateApp(curInfo.id);
                            curInfo.lastTimeSeen = now;
                            Log.d("AppsDb", "Updating app: " + aname);
                        }

                    }
                } catch (Exception e)
                {
                    Log.d("Exception", info.processName + ":" + e.toString());
                    // Name Not FOund Exception
                }
            }
        }
        }
        catch (Exception e)
        {
            Log.d("MainException", e.toString());
        }
        return Service.START_STICKY;
    }

    public void publicizeCurApp(String aname, CurAppInfo info)
    {
        if ((currentUser != null) && (curPublicizedApp != aname))
        {
            curPublicizedApp = aname;
            Log.d("Parse", "Saving object");
            ParseObject appInfo = this.mAppInfos.get(aname);
            if (appInfo == null)
            {
                Log.d("Parse", "New Object");
                appInfo = new ParseObject("CurAppInfo");
                appInfo.put("user", currentUser);
                appInfo.put("appName", aname);
                appInfo.put("time", info.lastTimeSeen);
            }
            try
            {
                appInfo.save();
            } catch (ParseException e)
            {
                Log.d("Parse", "Error saving: " + e.getMessage());
                // e.getMessage() will have information on the
                // error.
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        db.close();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        if (db == null)
        {
            db = new AppsDb(this);
        }
        // implement the IStockService interface defined in AIDL
        return new IAppService.Stub()
        {
            public List<AppInfo> getAppInfo() throws RemoteException
            {
                ArrayList<AppInfo> info = db.getAppInfo(null);
                Log.d("Service",
                        "called getAppInfo " + Integer.toString(info.size()));
                return info;
            }

            public void clearAppInfo() throws RemoteException
            {
                db.clearAppInfo();

            }

            public void setUserName(String uname) throws RemoteException
            {
                Log.d("AppsDb", "Setting user name to: " + uname);
                currentUser = uname;
            }
        };
    }
}
