package rex.login;

import java.util.ArrayList;
import java.util.List;

import android.R.drawable;
import android.R.string;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MyApps extends Activity {
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.myapps);
	        
	        ArrayList<String> values = new ArrayList<String>();
	        List<String> cats = AppInfoHelper.instance().getCategories();
            for(String cat: cats)
            {
                values.add("Category: " + cat);
                List<AppInfoHelper.AppSummary>appsByUsage = AppInfoHelper.instance().getAppsSortedByUsage(cat);
                for(AppInfoHelper.AppSummary sum: appsByUsage)
                {
					MyAppList myapplist = new MyAppList(sum.appName, sum.timeLastPlayed, sum.icon, this);
                    ViewGroup myapps = (ViewGroup) findViewById(R.layout.myapps);
                    myapps.addView(myapplist.getMyappslist());
                }
            }
	        
	        Button button2 = (Button) findViewById(R.id.button3);
	        
	        button2.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View view){
	        	Intent intent = new Intent();
	        	setResult(RESULT_OK, intent);
	        	finish();
	        }
	        });
	        
	        
	  }	
}
	
	
			
