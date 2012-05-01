package com.example.regauth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Registration extends Activity implements OnClickListener {
	
	EditText edtLogin, edtPassword;
	CheckBox chbSelectAdmin;
	Button btnAdd, btnCancel;
	DatabaseHelper dbHelper;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        
        edtLogin = (EditText) findViewById(R.id.edtLogin);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        chbSelectAdmin = (CheckBox) findViewById(R.id.chbSelectAdmin); 
        
        Button btnAdd = (Button) findViewById(R.id.btnAdd); 
        btnAdd.setOnClickListener(this);
        
        Button btnCancel = (Button) findViewById(R.id.btnCancel);  
        btnCancel.setOnClickListener(this);
      }
	
	@Override
	protected void onStart() {
		try
		{
			super.onStart();
			dbHelper=new DatabaseHelper(this);
	
		}
		catch(Exception ex)
		{
			
			CatchError(ex.toString());
		}
	}
	
	void CatchError(String Exception)
	{
		Dialog diag=new Dialog(this);
		diag.setTitle("Add new User");
		TextView txt=new TextView(this);
		txt.setText(Exception);
		diag.setContentView(txt);
		diag.show();
	}


	public void onClick(View v) {
	    Intent intent = new Intent();
	   
	    
	    switch (v.getId()) {
	    case R.id.btnAdd:
	    	boolean ok=true;
	    	int CountUser = 0;
			try
			{
				String login = edtLogin.getText().toString();
				String password = edtPassword.getText().toString();
				Boolean adminBool = chbSelectAdmin.isChecked();
				int admin = 0;
				if (adminBool) {
					admin = 1;
				} 
				
				CountUser = dbHelper.getUserLoginCount(login);	
				if (CountUser>0){
					edtLogin.setText("");
					edtPassword.setText("");
			        Toast.makeText(this, "Такой Логин уже зарегистрирован!", Toast.LENGTH_SHORT).show();
				} else {
					User user = new User(login,password,admin);
					dbHelper.AddUser(user);	
					
				}			
			}
			catch(Exception ex)
			{
				ok=false;
				CatchError(ex.toString());
			}
			
		  if (CountUser == 0){	
			  intent.putExtra("AddUser", ok);
			  closeActivity(intent);
		  }
	      break;
	    case R.id.btnCancel:
	      intent.putExtra("AddUser", false);
	      closeActivity(intent);
	      break;
	    }
		
	}
	
	void closeActivity(Intent intent){
	    setResult(RESULT_OK, intent);
	    finish();

	}

}
