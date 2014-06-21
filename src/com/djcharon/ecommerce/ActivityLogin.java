package com.djcharon.ecommerce;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.djcharon.listeners.OnTaskCompletedListener;
import com.djcharon.listeners.RequestTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityLogin extends Activity {
	private final String DEBUG = "ActivityEcommerce";
	private Button btnLogin, btnRegister, btnSkip, btnLogout;
	private ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(); 
	private MyAlertDialog dialog = null;
	private Intent intent;
	private Typeface roboBold;
	private Utils utils;
	private TextView tvHello;
	private RequestTask task;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        utils = new Utils(ActivityLogin.this);
        
        roboBold = Typeface.createFromAsset(getAssets(), "Roboto_Bold.ttf");

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setTypeface(roboBold);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setTypeface(roboBold);
        btnSkip = (Button)findViewById(R.id.btnSkip);
        btnSkip.setTypeface(roboBold);
        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setTypeface(roboBold);
        tvHello = (TextView)findViewById(R.id.tvHello);
        tvHello.setTypeface(roboBold);
        
        Log.i(DEBUG, utils.sha256("djcharon"));
    }

	@Override
	protected void onStart() {
		super.onStart();
		
		// we check if we already logged in
		if(utils.prefs.getBoolean("isLogged", false)){
			btnLogin.setVisibility(View.INVISIBLE);
			btnRegister.setVisibility(View.INVISIBLE);
			btnLogout.setVisibility(View.VISIBLE);
			tvHello.setVisibility(View.VISIBLE);
			tvHello.setText("Hello " + utils.prefs.getString("username", "") + "");
		}else{
			btnLogin.setVisibility(View.VISIBLE);
			btnRegister.setVisibility(View.VISIBLE);
			btnLogout.setVisibility(View.INVISIBLE);
			tvHello.setVisibility(View.INVISIBLE);
		}
		
		// we set the action for the time we press btnLogout
		btnLogout.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				utils.prefsEditor.putBoolean("isLogged", false);
				utils.prefsEditor.putString("username", "");
    			utils.prefsEditor.putString("password", "");
    			utils.prefsEditor.commit();
    			btnLogin.setVisibility(View.VISIBLE);
    			btnRegister.setVisibility(View.VISIBLE);
    			btnLogout.setVisibility(View.INVISIBLE);
    			tvHello.setVisibility(View.INVISIBLE);
			}
		});
		
		// we set the action for the time we press btnRegister
		// when we press this button will popup a dialog form that will send to our mysql db all the data
		btnRegister.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				dialog = new MyAlertDialog(ActivityLogin.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				dialog.setContentView(R.layout.dialog_register);
				
				final EditText etEmail = (EditText)dialog.findViewById(R.id.etEmail);
				final EditText etUsername = (EditText)dialog.findViewById(R.id.etUsername);
				final EditText etPassword = (EditText)dialog.findViewById(R.id.etPassword);
				final EditText etRetype = (EditText)dialog.findViewById(R.id.etRetype);
				final EditText etAddress = (EditText)dialog.findViewById(R.id.etAddress);
				final EditText etCity = (EditText)dialog.findViewById(R.id.etCity);
				final EditText etCountry = (EditText)dialog.findViewById(R.id.etCountry);
				final EditText etNumber = (EditText)dialog.findViewById(R.id.etNumber);
				final Button btnRegister = (Button)dialog.findViewById(R.id.btnRegister);
				
				etRetype.addTextChangedListener(new TextWatcher() {
					public void afterTextChanged(Editable arg0) {	
					}
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if(etPassword.getText().toString().equals(etRetype.getText().toString())){
							etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
							etRetype.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
							utils.PASSWORDS_MATCH = true;
						}else{
							etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
							etRetype.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
							utils.PASSWORDS_MATCH = false;
						}
					}
				});
				
				btnRegister.setOnClickListener(new OnClickListener(){
					public void onClick(View view){
						String email = etEmail.getText().toString();
						final String user = etUsername.getText().toString();
						final String retype = etRetype.getText().toString();
						String address = etAddress.getText().toString();
						String city = etCity.getText().toString();
						String country = etCountry.getText().toString();
						String number = etNumber.getText().toString();
						// check if is not empty and is not equals with the string that is already there
						if(!email.isEmpty()  && !email.equals("Email")){
							etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
							if(!user.isEmpty() && !user.equals("Username")){
								etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
								if(!retype.isEmpty() && !retype.equals("Passdasd")){
									if(!utils.PASSWORDS_MATCH) etRetype.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
									if(!address.isEmpty() && !address.equals("Address")){
										etAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
										if(!city.isEmpty() && !city.equals("City")){
											etCity.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
											if(!country.isEmpty() && !country.equals("Country")){
												etCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
												if(!number.isEmpty() && !number.equals("Phone")){
													etNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//													if(utils.isInternet()){
														// if there is internet then we parse the information to our server through php
														postParameters.add(new BasicNameValuePair("email", email)); 
														postParameters.add(new BasicNameValuePair("address", address));  
														postParameters.add(new BasicNameValuePair("city", city));  
														postParameters.add(new BasicNameValuePair("country", country));  
														postParameters.add(new BasicNameValuePair("username", user));
														postParameters.add(new BasicNameValuePair("phone", number));  
														// we convert the password into sha256
												        postParameters.add(new BasicNameValuePair("password", utils.sha256(retype)));
												        
												        try{
												        	task = new RequestTask("register", postParameters);
												        	task.setOnTaskCompleted(new OnTaskCompletedListener(){
																public void onTaskCompleted(String result) {
																	// TODO Auto-generated method stub
																	result = result.replaceAll("\\s+", "");
														        	int res = Integer.valueOf(result);
														        	switch(res){
														        		case 0: 
														        			utils.showToast("Please try again.");
														        			break;
														        		case 1:
														        			utils.showToast("Registration successfully.");
														        			if(dialog.isShowing()) dialog.dismiss();
														        			utils.prefsEditor.putString("username", user);
														        			utils.prefsEditor.putString("password", retype);
														        			utils.prefsEditor.putBoolean("isLogged", true);
														        			utils.prefsEditor.commit();
														        			intent = new Intent("com.tabs");
														        			startActivity(intent);
														        			break;
														        		case 2: 
														        			utils.showToast("This user already exists.");
														        			break;
														        	}
																}

																public void onTaskStarted() {
																	// TODO Auto-generated method stub
																	
																}
												        	});
												        	task.execute();
												        	
												        }catch(Exception e){
												        	Log.e(DEBUG, e.toString());
												        }
//													}
												}else{
													etNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
												}
											}else{
												etCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
											}
										}else{
											etCity.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
										}
									}else{
										etAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
									}
								}else{
									etRetype.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
								}
							}else{
								etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
							}
						}else{
							etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.x, 0);
						}
					}
				});
				
				if(dialog!=null) dialog.show();
			}
		});
		
		// we set the action for the time we press btnLogin
		// when we press this button will popup a dialog form that will check with mysql is we are in db,
		// if we are in mysql will give us a response code that equals with 1
		btnLogin.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				dialog = new MyAlertDialog(ActivityLogin.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				dialog.setContentView(R.layout.dialog_login);
				
				final EditText etUser = (EditText)dialog.findViewById(R.id.etUser);
				final EditText etPass = (EditText)dialog.findViewById(R.id.etPass);
				Button btnOk = (Button)dialog.findViewById(R.id.btnOK);

				btnOk.setOnClickListener(new OnClickListener(){
					public void onClick(View view){
//						if(utils.isInternet()){
							postParameters.add(new BasicNameValuePair("username", etUser.getText().toString()));  
					        postParameters.add(new BasicNameValuePair("password", utils.sha256(etPass.getText().toString())));
					        try {  
					        	// Enter the remote php link
					        	task = new RequestTask("login", postParameters);
					        	task.setOnTaskCompleted(new OnTaskCompletedListener(){
									public void onTaskCompleted(String result) {
										result = result.replaceAll("\\s+","");  
							        	//error.setText(res);  
							        	if(result.equals("1")) {
							        		utils.showToast("Login successfully.");  
							        		intent = new Intent("com.tabs");
							        		utils.prefsEditor.putString("username", etUser.getText().toString());
						        			utils.prefsEditor.putString("password", etPass.getText().toString());
						        			utils.prefsEditor.putBoolean("isLogged", true);
						        			utils.prefsEditor.commit();
							        		startActivity(intent);
							        		if(dialog.isShowing()) dialog.dismiss();
							        	} else  {
							        		utils.showToast("Sorry!! Incorrect Username or Password"); 
							        		etUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_error, 0);
							        		etPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_error, 0);
							        	}
							        	postParameters.removeAll(postParameters);
									}

									public void onTaskStarted() {}
					        	});
					        	task.execute();
				        	} catch (Exception e) {  
				        		Log.e(DEBUG, e.toString());  
					        }
						}
//					}
				});
				
				if(dialog!=null) dialog.show();
			}
		});
		
		// we set the action for the time we press btnSkip
		// this will open the activitytab
		btnSkip.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				intent = new Intent("com.tabs");
        		startActivity(intent);
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
//	private String createNewDeviceId() {
//       String s = getTelephonyManager().getDeviceId();
//       if(s.isEmpty() || "000039485642710".equals(s))
//    	   s = getWifiManager().getConnectionInfo().getMacAddress();
//       if(s.isEmpty() && android.os.Build.VERSION.SDK_INT >= 9)
//    	   s = Build.SERIAL;
//       if(s.isEmpty()) {
//    	   s = android.provider.Settings.Secure.getString(getContentResolver(), "android_id");
//    	   if(s.isEmpty() || "9774d56d682e549c".equals(s))
//    		   s = "";
//       	   }
//       Object aobj[] = new Object[2];
//       aobj[0] = s;
//       aobj[1] =String.valueOf(UUID.randomUUID());
//       return Base64.encodeBase64URLSafeString(String.format("%1$s_%2$s", aobj).getBytes());
//	}
	
    public WifiManager getWifiManager() {
    	WifiManager mWifiManager = null;
        if(mWifiManager == null)
            mWifiManager = (WifiManager)getSystemService("wifi");
        return mWifiManager;
    }
	  
    public TelephonyManager getTelephonyManager() {
    	TelephonyManager mTelephonyManager = null;
        if(mTelephonyManager == null)
            mTelephonyManager = (TelephonyManager)getSystemService("phone");
        return mTelephonyManager;
    }

	/**
	 * This class it will help us to create our own dialog without the black bg
	 * @author etien
	 */
	public class MyAlertDialog extends Dialog{
		public MyAlertDialog(Context context){ 
	        super(context);
	        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		}
	}


}