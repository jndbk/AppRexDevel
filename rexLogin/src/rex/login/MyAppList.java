package rex.login;

import it.sephiroth.demo.slider.widget.MultiDirectionSlidingDrawer;
import it.sephiroth.demo.slider.widget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import rex.login.AppInfoHelper.DeviceAppState;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyAppList implements OnDrawerOpenListener{
	private View myapps;
	private ActivityGroup mAct = null;
    static int nameNum = 0;
    String mPackageName = null;

		public MyAppList(String packageName, String appName, long timeLastPlayed, String icon, ActivityGroup act) {
		    mAct = act;
		    mPackageName = packageName;
		    try
		    {
    		    setMyappslist(act.getLayoutInflater().inflate(R.layout.myapps, null));
    			TextView tv = (TextView) myapps.findViewById(R.id.appName);
    			tv.setText(appName);
    			if(!icon.contentEquals("na"))
    			{
    			    ImageView iv = (ImageView) myapps.findViewById(R.id.icon1);
    			    Drawable d = Drawable.createFromPath(icon);
    			    iv.setImageDrawable(d);
    			}
    			String asString = "";
    			if(timeLastPlayed != 0)
    			{
    			    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    			    asString = formatter.format(timeLastPlayed);
    			}
    			tv = (TextView) myapps.findViewById(R.id.atpd);
    			tv.setText(asString);
    			MultiDirectionSlidingDrawer slide = (MultiDirectionSlidingDrawer) myapps.findViewById(R.id.drawer);
    			slide.setOnDrawerOpenListener(this);
		    }
            catch(Exception e)
            {
                Toast.makeText(act, "5:" + e.toString(),
                        2000).show();

            }
		}

		public View getMyappslist() {
			return myapps;
		}

		public void setMyappslist(View myappslist) {
			this.myapps = myappslist;
		}

        @Override
        public void onDrawerOpened()
        {
            long now = new Date().getTime();
            long offset = TimeZone.getDefault().getOffset(now);
            now += offset;  // Figure out midnight in terms of local time
            long startTime = now - (now % (60 * 60 * 24 * 1000));
            startTime -= offset; // Now back to UTC
            long n = 0;
            DeviceAppState[] binnedApps = AppInfoHelper.instance().getAppBins(mPackageName, startTime, 60*1000, 24 * 60);
            for(DeviceAppState s: binnedApps)
            {
                if(s == DeviceAppState.DASTATE_OTHER)
                    Log.d("AppInfoHelper", Long.toString(startTime + (n*60*1000)) + "Running");
                else if(s == DeviceAppState.DASTATE_APP)
                    Log.d("AppInfoHelper", Long.toString(startTime + (n*60*1000)) + "App Running");
                ++n;
            }
            SalesStackedBarChart sb = new SalesStackedBarChart();
            Intent in = sb.execute(mAct);
            
            String sss = "Act" + Integer.toString(nameNum);
            ++nameNum;
            LocalActivityManager mgr = mAct.getLocalActivityManager();
            Window w = mgr.startActivity(sss, in);
//            Window w = mgr.startActivity("unique_per_activity_string", in);
            View wd = w != null ? w.getDecorView() : null;
            Log.d("Parse", wd.toString());
            LinearLayout tv = (LinearLayout) myapps.findViewById(R.id.drawerLayout);
            tv.addView(wd);
            
            
            
        }

}
