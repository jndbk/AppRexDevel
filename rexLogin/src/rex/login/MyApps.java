package rex.login;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import rex.login.AppInfoHelper.AppDetails;
import rex.login.AppInfoHelper.AppDetails.Times;

import android.R.drawable;
import android.R.string;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

public class MyApps extends Activity {
	private TabHost mTabHost;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewGroup v = (ViewGroup) getLayoutInflater().inflate(R.layout.myappscontainer, null);
		setContentView(v);
		
		ViewGroup banner = (ViewGroup) getLayoutInflater().inflate(R.layout.banner,null);
		ViewGroup putbannerhere = (ViewGroup) findViewById(R.id.putbannerhere);
		putbannerhere.addView(banner);
		
		ArrayList<String> values = new ArrayList<String>();
		List<String> cats = AppInfoHelper.instance().getCategories();
		for(String cat:  cats)
		{
			values.add("Category: " + cat);
			List<AppInfoHelper.AppSummary>appsByUsage = AppInfoHelper.instance().getAppsSortedByUsage(cat);
			int i = 0;
			for(AppInfoHelper.AppSummary sum: appsByUsage)
			{
				try
				{
					ViewGroup vg = (ViewGroup) v.findViewById(R.id.putappshere);
					if((i&1) == 0)
					{
						MyAppList1 myapplist1 = new MyAppList1(sum.appName, sum.timeLastPlayed, sum.icon, this);
						vg.addView(myapplist1.getMyappslist1());
						Button panelHandle = (Button) myapplist1.getMyappslist1().findViewById(R.id.panelHandle);
						panelHandle.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								//Toast.makeText(getApplicationContext(), "Chart goes here", 2000).show();
								
							}
						});
					}
					else
					{	
						MyAppList myapplist = new MyAppList(sum.appName, sum.timeLastPlayed, sum.icon, this);
						vg.addView(myapplist.getMyappslist());

					}
					i++;/*
					AppDetails details = AppInfoHelper.instance().getDetails(sum.packageName);
					Times firstTime = details.times.getFirst();
					long st = firstTime.start;
                        for(Times times: details.times)
                        {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                            String startTime = formatter.format(times.start);
                            String stopTime = formatter.format(times.stop);

                            MyAppList myapplist = new MyAppList(startTime + " to " + stopTime, 0, sum.icon, this);
                            vg.addView(myapplist.getMyappslist());
                        }**/
				}
				catch(Exception e)
				{
					Toast.makeText(getApplicationContext(), e.toString(),
							2000).show();

				}
			}
		}
		Toast.makeText(getApplicationContext(), Integer.toString(v.getChildCount()),
				2000).show();

		Button button2 = (Button) findViewById(R.id.button3);

		button2.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
		});
        setupTabHost();
        //mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

        setupTab(new TextView(this), "Games");
        setupTab(new TextView(this), "Social");
        setupTab(new TextView(this), "Media");
	}
    private void setupTabHost() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
    }
    private void setupTab(final View view, final String tag) {
        View tabview = createTabView(mTabHost.getContext(), tag);

        TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabContentFactory() {
            public View createTabContent(String tag) {return view;}
        });
        mTabHost.addTab(setContent);

    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        tv.setLinkTextColor(0x000000);
        return view;
    }
}



