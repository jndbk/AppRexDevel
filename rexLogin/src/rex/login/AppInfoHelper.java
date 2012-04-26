package rex.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppInfoHelper
{
    private AppsDb mDb = null;
    private static AppInfoHelper mTheInstance = null;
    
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
                if((asum.category == null) || (asum.category.equals(category) != true))
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
        // Returns appname, icon, List of start time, stop time
        AppDetails ret = new AppDetails();
        ret.appInfo = this.getAppSummary(packageName);
        List<AppInfo> ainfo = mDb.getAppInfo(packageName);
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
    

}
