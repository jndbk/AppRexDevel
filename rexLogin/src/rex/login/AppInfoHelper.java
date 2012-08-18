package rex.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rex.login.AppInfoHelper.AppDetails.Times;

public class AppInfoHelper
{
    private AppsDb mDb = null;
    private static AppInfoHelper mTheInstance = null;
    
    enum DeviceAppState
    {
        DASTATE_IDLE,
        DASTATE_APP,
        DASTATE_OTHER
    }
    
    public static AppInfoHelper create(AppsDb db)
    {
        mTheInstance = new AppInfoHelper(db);
        return mTheInstance;
    }
    
    public static AppInfoHelper instance()
    {
        return mTheInstance;
    }
    public List<AppInfo> getAllAppInfos()
    {
        return mDb.getAppInfo(null);
        
    }
    List<String>getCategories()
    {
        List<String> cats = new LinkedList<String>();
        List<AppAttributes> attr = mDb.getAttributes(null);
        if(attr == null)
            return null;
        for(AppAttributes a: attr)
        {
            boolean found = false;
            for(String ss: cats)
            {
                if(ss.equals(a.getCategory()))
                   found = true;
            }
            if(!found)
            {
                cats.add(a.getCategory());
            }
        }
        return cats; 
    }
    
    public class AppSummary
    {
        public String appName;
        public String packageName;
        public String icon;
        public String category;
        public long totalTime = 0; // seconds
        public long timeLastPlayed = 0;
        
    }
    
    AppSummary getAppSummary(String packageName)
    {
        AppSummary asum = null;
        
        List<AppAttributes> aInfos = mDb.getAttributes(packageName);
        for(AppAttributes info: aInfos)
        {
            asum = new AppSummary();
            asum.packageName = info.getPackageName();
            asum.appName = info.getAppName();
            asum.icon = info.getIcon();
            asum.category = info.getCategory();
            break;
        }
        return asum;
    }
    
    public class AppSummaryComparator implements Comparator<AppSummary> {
        public int compare(AppSummary o1, AppSummary o2) 
        {
            if(o1.totalTime > o2.totalTime)
                return -1;
            else if(o1.totalTime == o2.totalTime)
                return 0;
            return 1;
        }
    }
  
    List<AppSummary> getAppsSortedByUsage(String category)
    {
        Map<String, AppSummary> appMap = new HashMap<String, AppSummary>();
        List<AppInfo> aInfos = mDb.getAppInfo(null);
        for(AppInfo info: aInfos)
        {
            AppSummary asum = appMap.get(info.getPackageName());
            if(asum == null)
            {
                asum = this.getAppSummary(info.getPackageName());
                if((asum == null) || (asum.category == null) || (asum.category.equals(category) != true))
                    continue;
                appMap.put(asum.packageName, asum);
            }
            asum.totalTime += info.getStopTime() - info.getStartTime();
            asum.timeLastPlayed = Math.max(asum.timeLastPlayed, info.getStopTime());
        }
        LinkedList<AppSummary> alist = new LinkedList<AppSummary>();
        for(String key: appMap.keySet())
        {
            alist.add(appMap.get(key));
        }
        Collections.sort(alist, new AppSummaryComparator());
        // Returns List appname, package name, icon, total time used, time last played
        return alist;
    }
    
    class AppDetails
    {
        public class Times
        {
            long start;
            long stop;
        }
        AppSummary appInfo;
        LinkedList<Times> times;
    }
    AppDetails getDetails(String packageName)
    {
        return getDetails(packageName, true);
    }
    AppDetails getDetails(String packageName, boolean descending)
    {
        // Returns appname, icon, List of start time, stop time
        AppDetails ret = new AppDetails();
        ret.appInfo = this.getAppSummary(packageName);
        ret.times = new LinkedList<AppDetails.Times>();
        List<AppInfo> ainfo = mDb.getAppInfo(packageName, descending);
        for(AppInfo info: ainfo)
        {
            AppDetails.Times times = ret.new Times();
            times.start = info.getStartTime();
            times.stop = info.getStopTime();
            ret.times.add(times);
        }
        return ret;
    }
    
    private AppInfoHelper(AppsDb db)
    {
        mDb = db;
    }
    
    /* Divides a time interval into one of three states: idle (device off), running the specified all,
     * or running a different app.
     * 
     * startTime: The number of milli-seconds since 1970 that the first element of the first bin represents
     * binSize: The number of milli-seconds in a bin
     * numBins: The number of bins returned
     * 
     * For example, to get an array in which every element represented 15 minutes of July 15, 2012 for the app
     * rex.login, use:
     * 
     * getApp("rex.login", 1342335600000, 60*15*1000, 4 * 24);
     * 
     */
    DeviceAppState[] getAppBins(String packageName, long startTime, int binSize, int numBins)
    {
        DeviceAppState[] bins = new DeviceAppState[numBins];
        for(int n = 0; n < numBins; ++n)
        {
            bins[n] = DeviceAppState.DASTATE_IDLE;
        }
        /*
        AppDetails test = new AppDetails();
        test.appInfo = new AppSummary();
        test.appInfo.appName = "Test1";
        test.appInfo.category = "Cat1";
        test.appInfo.icon = "";
        test.appInfo.packageName = "Test1";
        
        AppDetails.Times testTimes = test.new Times();
        testTimes.start = 10;
        testTimes.stop = 20;
        test.times = new LinkedList<AppDetails.Times>();
        test.times.add(testTimes);
        testTimes = test.new Times();
        testTimes.start = 40;
        testTimes.stop = 50;
        test.times.add(testTimes);
        testTimes = test.new Times();
        testTimes.start = 70;
        testTimes.stop = 100;
        test.times.add(testTimes);
        testTimes = test.new Times();
        testTimes.start = 120;
        testTimes.stop = 140;
        test.times.add(testTimes);
        binIt(bins, test, DeviceAppState.DASTATE_APP, 0, 5, 20);
        */
        AppDetails allApps = getDetails("PKG_ALL", false);
        allApps.appInfo.appName = "All Apps";
        binIt(bins, allApps, DeviceAppState.DASTATE_OTHER, startTime, binSize, numBins);
        AppDetails thisApp = getDetails(packageName, false);
        thisApp.appInfo.appName = packageName;
        binIt(bins, thisApp, DeviceAppState.DASTATE_APP, startTime, binSize, numBins);

        return bins;
    }
    
    void binIt(DeviceAppState[] bins, AppDetails appDetails, DeviceAppState state, long startTime, int binSize, int numBins)
    {
        long binStart = startTime;
        long binEnd = startTime + binSize;
        int bin = 0;
        for(AppDetails.Times t: appDetails.times)
        {
            if(t.stop < startTime)
                continue;
            while(bin < numBins)
            {
                if(binStart > t.stop)
                {
                    // If this bin starts later than the end of this interval, need to go on to the next interval
                    break;
                }
                if((t.start <= binEnd) && (t.stop >= binStart))
                    bins[bin] = state;
                bin++;
                binStart += binSize;
                binEnd += binSize;
            }
            if(bin >= numBins)
                break;
        }
    }        

}
