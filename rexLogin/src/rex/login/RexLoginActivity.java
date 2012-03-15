package rex.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import rex.login.service.IAppService;


public class RexLoginActivity extends Activity implements OnClickListener{
	protected static final String LOGGING_TAG = "AppRex";
	private IAppService appService;
	private ArrayList<AppInfo> appInfo;
	private AppsDb db;
	private boolean bound = false;
	@Override
	public void onStart(){
		Log.d("RexLogin", "Started app");
		super.onStart();
		db = new AppsDb(this);
        // create initial list
		if (!bound){
			bound = bindService(
					new Intent(RexLoginActivity.this, MonitorService.class), 
					connection, Context.BIND_AUTO_CREATE);
			Log.d(LOGGING_TAG, "Bound to service: " + bound);
		}
		if (!bound){
			Log.e(LOGGING_TAG, "Failed to bind to service");
			throw new RuntimeException("Failed to find to service");
		}
	}
	
	// Connection to the stock service, handles lifecycle events
	private ServiceConnection connection = new ServiceConnection(){

		public void onServiceConnected(ComponentName className, 
				IBinder service) {
			appService = IAppService.Stub.asInterface(service);
			Log.d(LOGGING_TAG,"Connected to service");
			try {
				appInfo = (ArrayList<AppInfo>) appService.getAppInfo();
				if (appInfo == null){
					appInfo = new ArrayList<AppInfo>(0);
					Log.d(LOGGING_TAG, "No appInfo returned from service");
				} else {
					Log.d(LOGGING_TAG, "Got "+ appInfo.size() +" appInfo from service");
				}

				refresh();
			} catch (RemoteException e) {
				Log.e(LOGGING_TAG, "Exception retrieving portfolio from service",e);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			appService = null;
			Log.d(LOGGING_TAG,"Disconnected from service");
		}
		
	};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button createAccount = (Button) findViewById(R.id.createAccount);
        Button login = (Button) findViewById(R.id.login);
        createAccount.setOnClickListener(this);
        login.setOnClickListener(this);
        Parse.initialize(this, "gentv1lxXI5DEP7K3kQbfNOOIIUoVSdSwTu8RQ8d", "S1BixT6dROphY1hYMn2JRv3BZuNSRZcEibpyeeaj");
        ParseFacebookUtils.initialize("254743831280356");
    }

	public void onClick(View login) {
		EditText userName = (EditText) findViewById(R.id.userName);
		EditText passWord = (EditText) findViewById(R.id.passWord);
		Button clicked = (Button) login;
		if((userName == null) || (passWord == null) || (clicked == null))
			return;
		String uName = userName.getText().toString();
		String pWord = passWord.getText().toString();
		switch(clicked.getId())
		{
		case R.id.login:
			login(uName, pWord);
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) 
			{
				Log.d("Parse", "Current user: " + currentUser.getUsername());
			} else 
			{
				Log.d("Parse", "No current user");
			}
			break;
		case R.id.createAccount:
			createUser(uName, pWord);
			break;
		}		
	}
	public void createUser(String uname, String pword)
	{
		ParseUser user = new ParseUser();
		user.setUsername(uname);
		user.setPassword(pword);
		user.signUpInBackground(new SignUpCallback() 
		{
		    public void done(ParseException e) 
		    {
		        if (e == null) 
		        {
		        	// Create user successful
		        } 
		        else 
		        {
		        	// Error
		        }
		    }
		});		
	}
	public void login(String uname, String pword)
	{
		ParseFacebookUtils.logIn(this, new LogInCallback() {
		    @Override
		    public void done(ParseUser user, ParseException err) {
		        if (user == null) {
		            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
		        } else if (user.isNew()) {
		            Log.d("MyApp", "User signed up and logged in through Facebook!");
		        } else {
		            Log.d("MyApp", "User logged in through Facebook!");
		        }
		    }
		});

/*		ParseUser.logInInBackground(uname, pword, new LogInCallback() 
		{
		    public void done(ParseUser user, ParseException e) 
		    {
		        if (e == null && user != null) 
		        {
		        } 
		        else if (user == null) 
		        {
		            // Sign up didn't succeed. The username or password was invalid.
		        } 
		        else 
		        {
		            // There was an error. Look at the ParseException to see what happened.
		        }
		    
		     }
		});
*/	
	}
    private void refresh(){
    	Log.d(LOGGING_TAG, "Refreshing UI with new data");
    	try
    	{
    		appInfo = db.getAppInfo();
			if (appInfo == null){
				appInfo = new ArrayList<AppInfo>(0);
//				Log.d(LOGGING_TAG, "No appInfo returned from service");
			} 
//			else 
//			{
//				Log.d(LOGGING_TAG, "Got "+ appInfo.size() +" appInfo from service");
//			}
			
		}
		catch(Exception e)
		{
			
		}
    	
//    	BaseAdapter adapter = (BaseAdapter) this.getListAdapter();
//    	adapter.notifyDataSetChanged();
    }
}
