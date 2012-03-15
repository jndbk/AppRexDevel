package rex.login;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
	// This is a data access object used for persisting stock information.
	private AppsDb db;
	Map<String, ParseObject> mAppInfos; 
	
	@Override
	public void onCreate() {
		Log.d("AppsDb", "Started up service");
        Parse.initialize(this, "gentv1lxXI5DEP7K3kQbfNOOIIUoVSdSwTu8RQ8d", "S1BixT6dROphY1hYMn2JRv3BZuNSRZcEibpyeeaj");   
        mAppInfos = new HashMap<String, ParseObject>();
		ParseUser currentUser = ParseUser.getCurrentUser();
		Log.d("Parse", "C: " + currentUser.getUsername());
		if (currentUser != null) 
		{
	        ParseQuery query = new ParseQuery("AppInfo");
	        query.whereContains("user", currentUser.getUsername());
	        query.findInBackground(new FindCallback() 
	        {
	            public void done(List<ParseObject> objects, ParseException e) 
	            {
	                if (e == null) 
	                {
	                	for(ParseObject appInfo: objects)
	                	{
	                		mAppInfos.put(appInfo.getString("appName"), appInfo);
	                		Log.d("Parse", "Got:  " + appInfo.getString("user"));
	                		Log.d("Parse", "Got:  " + appInfo.getString("appName"));
	                		Log.d("Parse", "Got:  " + appInfo.getInt("hits"));
	                		
	                	}
	                } 
	                else 
	                {
	                	Log.d("Parse", "Error in retrieving data");
	                }
	            }
	        });
		}
		super.onCreate();
		db = new AppsDb(this);
//		android.os.Debug.waitForDebugger();

		Context context = this.getApplicationContext();
		AlarmManager mgr = 
			(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, 
				i, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, 2);
		mgr.setRepeating(AlarmManager.RTC, now.getTimeInMillis(), 10 * 1000, sender);
//		mgr.setRepeating(AlarmManager.RTC, now.getTimeInMillis(), 120 * 1000, sender);
		Log.d("APPREX", "End start up");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//if (db == null){
		//	db = new AppsDb(this);
		//}
		//try {
		//	updateStockData();
		//} catch (IOException e) {
		//	Log.e(TAG, "Exception updating stock data", e);
		//}
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) 
		{
			Log.d("Parse", "Current user (service level): " + currentUser.getUsername());
		} 
		else 
		{
			Log.d("Parse", "No current user in service level");
		}
		try
		{
			db.incrApp("TotalIntervals");
		}
 		catch(Exception e) 
 		{
 			Log.e("Exception", e.toString());
		}
		
		PackageManager pm1 = this.getPackageManager();
		List<ApplicationInfo> allApps1 = pm1.getInstalledApplications(0);
//		for(ApplicationInfo i: allApps1)
//		{
//			Log.d("AppsDb", i.packageName);
//			Log.d("AppsDb", i.processName);
//		}
 		
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        // get the info from the currently running task
        List<RunningAppProcessInfo> taskInfo = am.getRunningAppProcesses();
        for(RunningAppProcessInfo info : taskInfo)
        {
        	String aname = null;
        	int imp = info.importance;
        	if((imp > 0) && (imp <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND))
        	{
     			PackageManager pm = this.getPackageManager();
         		try 
         		{
        		    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
        		    
           		    aname = c.toString();
           		    if(!aname.equals("System UI") && 
           		    		!aname.equals("Android keyboard") && 
           		    		!aname.equals("Phone") && 
           		    		!aname.equals("Launcher") && 
           		    		!aname.equals("Android System"))
           		    {
           		    	Long hits = db.incrApp(aname);
           		    	if(currentUser != null)
           		    	{
           		    		Log.d("Parse", "Saving object");
           		    		ParseObject appInfo = this.mAppInfos.get(aname);
           		    		if(appInfo == null)
           		    		{
           		    			Log.d("Parse", "New Object");
           		    			appInfo = new ParseObject("AppInfo");
           		    			appInfo.put("user", currentUser.getUsername());
           		    			appInfo.put("appName", aname);
           		    			mAppInfos.put(aname, appInfo);
           		    		}
           		    		appInfo.put("hits", hits);
           		    		try {
           		    			appInfo.save();
           		    		} catch (ParseException e) {
           		    			Log.d("Parse", "Error saving: " + e.getMessage());
           		    		    // e.getMessage() will have information on the error.
           		    		}
           		    	}
           		    }
         		}
	     		catch(Exception e) 
	     		{
	     			Log.e("Exception", info.processName + ":" + e.toString());
	    			    //Name Not FOund Exception
	    		}
        	}
        }
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (db == null){
			db = new AppsDb(this);
		}
		// implement the IStockService interface defined in AIDL 
		return new IAppService.Stub() {
			public List<AppInfo> getAppInfo() throws RemoteException {
				ArrayList<AppInfo> info = db.getAppInfo();
				return info;
			}

			public void clearAppInfo() throws RemoteException {
				db.clearAppInfo();
				
			}
		};
	}
}
	
