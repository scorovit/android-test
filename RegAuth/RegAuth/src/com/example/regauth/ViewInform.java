package com.example.regauth;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ViewInform extends Activity implements OnClickListener {
	
	int IDUser, isAdmin;
	String UserLogin;
	Button btnCleanUser;
	ListView mUserList;
	ArrayAdapter<String> adapter; 
    protected ArrayList<User> ListUser = new ArrayList<User>();
    private ArrayList<String> ListNews = new ArrayList<String>(); 
    DatabaseHelper dbHelper;
    protected static final int CONTEXTMENU_DELETEITEM = 0;
 	
    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewinf);
        dbHelper=new DatabaseHelper(this);
        
		final Bundle extras = getIntent().getExtras();
		IDUser = extras.getInt("UserID");
		UserLogin = extras.getString("UserLogin");
		isAdmin = extras.getInt("isAdmin");
        
        TabHost tabHost=(TabHost)findViewById(R.id.tabhost);
        tabHost.setup();

        TabSpec list=tabHost.newTabSpec("Tab 1");
        list.setContent(R.id.tabList);
        
        mUserList = (ListView) this.findViewById(R.id.listViewInform);
        if (isAdmin==0){
        	list.setIndicator("Лента новостей");
        	CheckIfServiceIsRunning();
            startService(new Intent(this, ServiceUpdateRSS.class));
            doBindService();
        } else {
        	list.setIndicator("Список пользователей");
        	AddUserList();
        	initListView();
        }

        TabSpec CleanUser=tabHost.newTabSpec("Tab 2");
        CleanUser.setIndicator("Пользователь");
        CleanUser.setContent(R.id.tabUserInf);
        
        tabHost.addTab(list);
        tabHost.addTab(CleanUser);
        
        btnCleanUser = (Button) findViewById(R.id.btnCleanUser);
        btnCleanUser.setOnClickListener(this);
        
        }
	
    private void CheckIfServiceIsRunning() {
        //If the service is running when the activity starts, we want to automatically bind to it.
        if (ServiceUpdateRSS.isRunning()) {
            doBindService();
        }
    }

	   class IncomingHandler extends Handler {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case ServiceUpdateRSS.MSG_SET_STRING_VALUE:
	            	String readNews = msg.getData().getString("str1");
	                try {
	                JSONArray jsonArray = new JSONArray(readNews);
	                ListNews.clear();
	    			for (int i = 0; i < jsonArray.length(); i++) {
	    				JSONObject jsonObject = jsonArray.getJSONObject(i);
	    				ListNews.add(jsonObject.getString("text"));
	    				Log.i("mytag", jsonObject.getString("text"));
	    			}
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}
	                refreshNewsListItems(ListNews);
	                break;
	            default:
	                super.handleMessage(msg);
	            }
	        }
	    }

	
	public void onClick(View v) {
		 switch (v.getId()) {
		    case R.id.btnCleanUser:
		    	int upd = dbHelper.UpdateUser(new User(IDUser, UserLogin, "", 0));   
		    	finish();
		    break;
		 }
		
	}

	private void AddUserList(){
	
		String Login, Password;
		int ID, isAdmin;
		
		Cursor AllUsers = dbHelper.getAllUsers();
		if (AllUsers!= null ) {
		    if  (AllUsers.moveToFirst()) {
		        do {
		        	ID = AllUsers.getInt(AllUsers.getColumnIndex("UserID"));
		        	Login = AllUsers.getString(AllUsers.getColumnIndex("UserLogin"));
		        	Password = AllUsers.getString(AllUsers.getColumnIndex("UserPassword"));
		        	isAdmin = AllUsers.getInt(AllUsers.getColumnIndex("Admin"));
		        	ListUser.add(new User(ID, Login, Password, isAdmin));
		        }while (AllUsers.moveToNext());
		    }
		}
		AllUsers.close();

	}
	
	 private void refreshUserListItems() {
		 mUserList.setAdapter(new ArrayAdapter<User>(this, R.layout.listitem, ListUser));
	 }

	 private void refreshNewsListItems(ArrayList<String> ListNews) {
		 adapter = new ArrayAdapter<String>(this, R.layout.listitem, ListNews);
		 mUserList.setAdapter(adapter);
		 adapter.notifyDataSetChanged(); 
	 }
	 
	 private void initListView() {
         /* Loads the items to the ListView. */
		 refreshUserListItems();

		 mUserList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {          
			    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
			        menu.setHeaderTitle("Операция");
			        menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Удалить пользователя");
			    }});
	 }
	 
	 @Override
     public boolean onContextItemSelected(MenuItem item) {
             
		 AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();		 /* Switch on the ID of the item, to get what the user selected. */
             switch (item.getItemId()) {
                     case CONTEXTMENU_DELETEITEM:
                             /* Get the selected item out of the Adapter by its position. */
                             User userContexted = (User) mUserList.getAdapter().getItem(menuInfo.position);
                             /* Remove it from the list.*/
                             int Id = userContexted.getID();
                             Log.d("mytag", "id = " +Id);
                             dbHelper.DeleteUser(userContexted);
                             ListUser.remove(userContexted);

                             refreshUserListItems();
                             return true; 
             }
             return false;
     }
	 
	    private ServiceConnection mConnection = new ServiceConnection() {
	        public void onServiceConnected(ComponentName className, IBinder service) {
	            mService = new Messenger(service);
	            try {
	                Message msg = Message.obtain(null, ServiceUpdateRSS.MSG_REGISTER_CLIENT);
	                msg.replyTo = mMessenger;
	                mService.send(msg);
	            } catch (RemoteException e) {
	                // In this case the service has crashed before we could even do anything with it
	            }
	        }

	        public void onServiceDisconnected(ComponentName className) {
	            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
	            mService = null;
	        }
	    };
	 
	    void doBindService() {
	        bindService(new Intent(this, ServiceUpdateRSS.class), mConnection, Context.BIND_AUTO_CREATE);
	        mIsBound = true;
	    }
	    void doUnbindService() {
	        if (mIsBound) {
	            // If we have received the service, and hence registered with it, then now is the time to unregister.
	            if (mService != null) {
	                try {
	                    Message msg = Message.obtain(null, ServiceUpdateRSS.MSG_UNREGISTER_CLIENT);
	                    msg.replyTo = mMessenger;
	                    mService.send(msg);
	                } catch (RemoteException e) {
	                    // There is nothing special we need to do if the service has crashed.
	                }
	            }
	            // Detach our existing connection.
	            unbindService(mConnection);
	            mIsBound = false;
	        }
	    }
	    
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        try {
	            doUnbindService();
	            stopService(new Intent(ViewInform.this, ServiceUpdateRSS.class));
	 	        } catch (Throwable t) {
	            Log.e("mytag", "Failed to unbind from the service", t);
	        }
	    }



}
