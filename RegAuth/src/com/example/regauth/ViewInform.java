package com.example.regauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
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
	protected ListView mUserList;
    protected ArrayList<User> ListUser = new ArrayList<User>();
    private ArrayList<String> ListNews = new ArrayList<String>(); 
    DatabaseHelper dbHelper;
    protected static final int CONTEXTMENU_DELETEITEM = 0;
    String URL = "http://twitter.com/statuses/user_timeline/vogella.json"; 
	
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
        	String readNews = readNews();
    		try {
    			JSONArray jsonArray = new JSONArray(readNews);

    			for (int i = 0; i < jsonArray.length(); i++) {
    				JSONObject jsonObject = jsonArray.getJSONObject(i);
    				ListNews.add(jsonObject.getString("text"));
    				Log.i("mytag", jsonObject.getString("text"));
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		mUserList.setAdapter(new ArrayAdapter<String>(this, R.layout.listitem, ListNews));
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
	 
	 public String readNews() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return builder.toString();
		}
	 
}
