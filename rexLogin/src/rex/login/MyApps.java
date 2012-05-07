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
import android.widget.Toast;

public class MyApps extends Activity {
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        ViewGroup v = (ViewGroup) getLayoutInflater().inflate(R.layout.myappscontainer, null);
	        setContentView(v);
	        
	        ArrayList<String> values = new ArrayList<String>();
	        List<String> cats = AppInfoHelper.instance().getCategories();
            for(String cat: cats)
            {
                values.add("Category: " + cat);
                List<AppInfoHelper.AppSummary>appsByUsage = AppInfoHelper.instance().getAppsSortedByUsage(cat);
                for(AppInfoHelper.AppSummary sum: appsByUsage)
                {
                    try
                    {
                        ViewGroup vg = (ViewGroup) v.findViewById(R.id.putappshere);
                        MyAppList myapplist = new MyAppList(sum.appName, sum.timeLastPlayed, sum.icon, this);
                        vg.addView(myapplist.getMyappslist());
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
	        
	        
	  }	
}
	
	
			
