package rex.login;

import it.sephiroth.demo.slider.widget.MultiDirectionSlidingDrawer;
import it.sephiroth.demo.slider.widget.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import it.sephiroth.demo.slider.widget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
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
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyAppList implements OnDrawerCloseListener, OnDrawerOpenListener{
	private View myapps;
	private ActivityGroup mAct = null;
    static int nameNum = 0;
    String mPackageName = null;
    private int containerwidth = 0;
    private int containerheight = 0;
    LayoutParams saveLayout = null;

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
    			slide.setOnDrawerCloseListener(this);
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
            if(containerwidth == 0) // First time this is called, save the original inflated dimensions
            {
                containerwidth = myapps.getWidth();
                containerheight = myapps.getHeight();
                this.saveLayout = new LayoutParams(myapps.getLayoutParams());
            }
            MultiDirectionSlidingDrawer slide = (MultiDirectionSlidingDrawer) myapps.findViewById(R.id.drawer);
            LayoutParams l = slide.getLayoutParams();
            l.width = containerwidth;
            l.height = (int) ((double) containerwidth * 1.1);
            
            /* this code displays from midnight of the current day 
            long now = new Date().getTime();
            long offset = TimeZone.getDefault().getOffset(now);
            now += offset;  // Figure out midnight in terms of local time
            long startTime = now - (now % (60 * 60 * 24 * 1000));
            startTime -= offset; // Now back to UTC
            */
            /* This code displays the last 24 hours */
            long now = new Date().getTime();
            // get one hour from now
            now += 60 * 60 * 1000;
            // Now get the start of the hour
            now = now - (now % (60 * 60 * 1000));
            // Now go back 24 hours
            long startTime = now - (60 * 60 * 24 * 1000);
            
            
            long n = 0;
            DeviceAppState[] binnedApps = AppInfoHelper.instance().getAppBins(mPackageName, startTime, 60*1000, 24 * 60);

            SalesStackedBarChart sb = new SalesStackedBarChart();
            Calendar c = new GregorianCalendar();
            sb.setStartHour(c.get(Calendar.HOUR_OF_DAY));
            
            LinkedList<SalesStackedBarChart.AppIntervals> appIntervals = new LinkedList<SalesStackedBarChart.AppIntervals>();
            for(int m = 0; m < 3; ++m)
            {
                SalesStackedBarChart.AppIntervals aa = sb.new AppIntervals();
                appIntervals.add(aa);
                LinkedList<Boolean> states = new LinkedList<Boolean>();
                aa.active = states;
                if(m == 0)
                    aa.appName = "Idle";
                else if(m == 1)
                    aa.appName = "All Apps";
                else
                    aa.appName = mPackageName;
            }
            for(DeviceAppState s: binnedApps)
            {
                if(s == DeviceAppState.DASTATE_IDLE)
                {
                    appIntervals.get(0).active.add(true);
                    appIntervals.get(1).active.add(false);
                    appIntervals.get(2).active.add(false);
                }
                if(s == DeviceAppState.DASTATE_OTHER)
                {
                    appIntervals.get(0).active.add(false);
                    appIntervals.get(1).active.add(true);
                    appIntervals.get(2).active.add(false);
                }
                else if(s == DeviceAppState.DASTATE_APP)
                {
                    appIntervals.get(0).active.add(false);
                    appIntervals.get(1).active.add(false);
                    appIntervals.get(2).active.add(true);
                }
                ++n;
            }
            sb.setValues(appIntervals, 24, 60);
            
            LinearLayout tv = (LinearLayout) myapps.findViewById(R.id.drawerLayout);
            tv.addView(sb.execute(mAct));
            
        }

        @Override
        public void onDrawerClosed()
        {
            // TODO Auto-generated method stub
            //myapps.setLayoutParams(this.saveLayout);
            
        }

}
