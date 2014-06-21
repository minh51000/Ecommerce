package com.djcharon.ecommerce;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.djcharon.listeners.OnTaskCompletedListener;
import com.djcharon.listeners.RequestTask;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPayment;
import com.rogcg.gridviewexample.GridViewTabActivity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SlidingDrawer;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class TabTheoLoaiSpActivity extends ActivityGroup {
	// these lists are from products listview, with them will fill the lvProduct
	private ArrayList<Integer> pid = new ArrayList<Integer>();
	private ArrayList<String> pName = new ArrayList<String>();
	private ArrayList<String> pTitle = new ArrayList<String>();
	private ArrayList<String> pDescription = new ArrayList<String>();
	private ArrayList<String> pPrice = new ArrayList<String>();
	private ArrayList<String> pCategory = new ArrayList<String>();
	private ArrayList<String> pIcon = new ArrayList<String>();
	
	// these lists are from promotions listview, with them will fill the lvPromotions
	private ArrayList<Integer> ppid = new ArrayList<Integer>();
	private ArrayList<String> ppName = new ArrayList<String>();
	private ArrayList<String> ppTitle = new ArrayList<String>();
	private ArrayList<String> ppDescription = new ArrayList<String>();
	private ArrayList<String> ppPrice = new ArrayList<String>();
	private ArrayList<String> ppCategory = new ArrayList<String>();
	private ArrayList<String> ppIcon = new ArrayList<String>();
	private ArrayList<Integer> ppDiscount = new ArrayList<Integer>();
	
	// these lists are from cart listview, with them will fill the lvCart
	private ArrayList<Integer> cid = new ArrayList<Integer>();
	private ArrayList<Integer> cQty = new ArrayList<Integer>();
	private ArrayList<String> cName = new ArrayList<String>();
	private ArrayList<String> cTitle = new ArrayList<String>();
	private ArrayList<String> cPrice = new ArrayList<String>();
	private ArrayList<String> cIcon = new ArrayList<String>();
	
	// this flag will help us to figure it out if our activity is onPause
	private boolean IS_PAUSED = false; 
	
	// this flag will help us to determinate when we finished loading new products from mysql
	private boolean LOADING_PRODUCTS_ENDED = false;
	
	// this flag will be false when we stopped downloading the images and the data from json for productlist, true when the task is running
	private boolean IS_PRODUCTS_TASK = false;
	
	// this flag will be false when we stopped downloading the images and the data from json for promotionslist, true when the task is running
	private boolean IS_PROMOTIONS_TASK = false;
	
	// this flag will let us know if we run this activity for the first time
	private boolean IS_FIRST_TIME = true;
	
	// these variables will be used to search something from mysql db
	private String titleSearch, nameSearch, catSearch;
	
	// these vars are for order
	private String sortOrder, sortType;
	
	private int TABLE_SIZE;
	
	private PayPal ppObject;
	
	
	private TabHost tabHost;
	private ListView lvProducts, lvCart, lvPromotions;
	private RelativeLayout rlPay, rlLoading;
	private Utils utils;
	private ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(); 
	private final String DEBUG = "ActivityTab"; 
	private ImageView imgLoading;
	
	private JSONArray jarray;
	
	private CheckoutButton btnPaypal;
	
	private SmsManager sms;
	
	private Dialog dialog;
	
	
	private ProductsAdapter pAdapter;

	private EcommerceDatabase db;
	
	private EditText etEmail;
	private Button btnSend, btnHandle, btnLogin, btnRegister, btnSearch, btnList, btnReload;
	
	private TextView tvValueTotalQty, tvValueTotalUSD;
	
	//SlidingDrawer hides content out of the screen and allows the user to drag a handle to bring the content on screen. 
	//SlidingDrawer can be used vertically or horizontally.
	private SlidingDrawer sliding;
	
	private PopupWindow popup;
	
	private Typeface roboBold;
	
	// we need this varibles to calculate the total qty and total amount of money for purchase
	private int TOTAL_QTY, TOTAL_USD;
	
	private RequestTask task;
	
	// some animations
	private Animation scaleIn;
	private static Animation translateInUp;
	private static Animation translateInDown;
	 Intent intent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab);
		
		
        
        fillProductList();
       
	}
	
	protected void onStart() {
		super.onStart();
		
		Log.i(DEBUG, "onStart");
		
		// we don't need to reload those info, everytime we start this activity, so when we open specs IS_PAUSED = true, because this activity triggers the onPause method
		if(!IS_PAUSED){
			
			isLogged();
			
			slidingDrawerHandle(sliding, btnHandle);
		}
		
		//Register a callback to be invoked when the selected state of any of the items in this list changes
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			public void onTabChanged(String arg0) {
				setTabBackground(tabHost);
				if(tabHost.getCurrentTab()==3){
					// when we press tab 3, we'll fill the cart from db
					fillCartList(false);
				
				}
			}
		});
		
		// we set the event for the moment we press long click on an item from products list, 
		lvProducts.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
				LayoutInflater inflater = (LayoutInflater)TabTheoLoaiSpActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View viewInflated = inflater.inflate(R.layout.popupfile, null, false);
				popup = new PopupWindow(viewInflated, Resizable.getDrawableWidth(getResources(), R.drawable.popup_menu), Resizable.getDrawableHeight(getResources(), R.drawable.popup_menu), true);
				
				popup.setAnimationStyle(R.style.animation_popup);
	      
        		//arg1.getTop() return the top of this view, in pixels. 
        		popup.showAtLocation(lvProducts, Gravity.NO_GRAVITY, view.getWidth()/2-Resizable.getDrawableWidth(getResources(), R.drawable.popup_menu)/2, view.getTop()+view.getHeight()/2);
        		
        		setPopupListeners(viewInflated, position, pid);
        		
				return false;
			}
		});
		
		// we set the event for the moment we press long click on an item from promotions list, a popupWindow will appear where you can add to cart 
		lvPromotions.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
				LayoutInflater inflater = (LayoutInflater)TabTheoLoaiSpActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View viewInflated = inflater.inflate(R.layout.popupfile, null, false);
				popup = new PopupWindow(viewInflated, Resizable.getDrawableWidth(getResources(), R.drawable.popup_menu), Resizable.getDrawableHeight(getResources(), R.drawable.popup_menu), true);
				
				popup.setAnimationStyle(R.style.animation_popup);
	      
        		//arg1.getTop() return the top of this view, in pixels. 
        		popup.showAtLocation(lvPromotions, Gravity.NO_GRAVITY, view.getWidth()/2-Resizable.getDrawableWidth(getResources(), R.drawable.popup_menu)/2, view.getTop()+view.getHeight()/2);
        		
        		setPopupListeners(viewInflated, position, ppid);
        		
				return false;
			}
		});
		
		// when we pres on an item from lvProducts, will open a new activity which will pull the data from products after pid
		lvProducts.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Intent intent = new Intent("com.specs");
				intent.putExtra("pid", pid.get(position));
				startActivity(intent);
			}
		});
		
		// when we pres on an item from lvPromotions, will open a new activity which will pull the data from products after pid
		lvPromotions.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Intent intent = new Intent("com.specs");
				intent.putExtra("pid", ppid.get(position));
				startActivity(intent);
			}
		});
		
		// when we pres on an item from lvCart, will open a new activity which will pull the data from products after pid
		lvCart.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Intent intent = new Intent("com.specs");
				intent.putExtra("pid", cid.get(position));
				startActivity(intent);
			}
		});
		
		lvCart.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
				LayoutInflater inflater = (LayoutInflater)TabTheoLoaiSpActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View viewInflated = inflater.inflate(R.layout.popup_cart, null, false);
				popup = new PopupWindow(viewInflated, Resizable.getDrawableWidth(getResources(), R.drawable.popup_menu), Resizable.getDrawableHeight(getResources(), R.drawable.popup_menu), true);
				
				popup.setAnimationStyle(R.style.animation_popup);
	      
        		//arg1.getTop() return the top of this view, in pixels. 
				popup.showAtLocation(lvCart, Gravity.NO_GRAVITY, view.getWidth()/2-Resizable.getDrawableWidth(getResources(), R.drawable.popup_menu)/2, view.getTop()+view.getHeight()/2);
        		
        		TextView tvQty = (TextView)view.findViewById(R.id.tvNr);
        		
        		Button btnPlus = (Button)viewInflated.findViewById(R.id.btnPlus);
        		Button btnMinus = (Button)viewInflated.findViewById(R.id.btnMinus);
        		Button btnOk = (Button)viewInflated.findViewById(R.id.btnOk);
        		Button btnClose = (Button)viewInflated.findViewById(R.id.btnClose);
        		final EditText etQty = (EditText)viewInflated.findViewById(R.id.etQty);
        		
        		etQty.setText(tvQty.getText());
        		
        		// will add 1 from qty 
        		btnPlus.setOnClickListener(new OnClickListener(){
        			public void onClick(View view){
        				int qty = Integer.valueOf(etQty.getText().toString());
        				++qty;
        				if(qty>=0) etQty.setText("" + qty);
        			}
        		});
        		
        		// will substract 1 from qty 
        		btnMinus.setOnClickListener(new OnClickListener(){
        			public void onClick(View view){
        				int qty = Integer.valueOf(etQty.getText().toString());
        				--qty;
        				// we don't want the nr to be lower then 0
        				if(qty>=0) etQty.setText("" + qty);
        			}
        		});
        		
        		btnOk.setOnClickListener(new OnClickListener(){
        			public void onClick(View view){
        				int qty = Integer.valueOf(etQty.getText().toString());
        				int id = cid.get(position);
        				if(qty!=0){
        					// now will add a new row into cart table
        					if(db.hasIDCart(id)) db.updateRow(id, qty);
        				}else{
        					db.deleteRow(EcommerceDatabase.TABLE_CART, id);
        				}
        				fillCartList(false);
        			
        				popup.dismiss();
        			}
        		});
        		
        		btnClose.setOnClickListener(new OnClickListener(){
        			public void onClick(View view){
        				popup.dismiss();
        			}
        		});
        		
				return false;
			}
		});
		
		// this will load more data from mysql at the moment we reach at the bottom of the list
		lvProducts.setOnScrollListener(new OnScrollListener() {
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		        //Check if the last view is visible
		        if (firstVisibleItem + visibleItemCount >= totalItemCount) {
		        	if(!Utils.IS_ENDED_PRODUCT_LIST && !IS_PRODUCTS_TASK){
		        		fillProductList();
		        	}
		        }   
		    }

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
		});
		

		
		btnSend.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				regexText();
			}
		});
		
		btnRegister.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				dialog = new MyAlertDialog(TabTheoLoaiSpActivity.this);
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
						if(!email.isEmpty() && !email.equals("Email")){
							etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
							if(!user.isEmpty() && !user.equals("Username")){
								etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
								if(!retype.isEmpty() && !retype.equals("Passdasd")){
									if(!utils.PASSWORDS_MATCH) etRetype.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
									if(!address.isEmpty() && !address.equals("Address")){
										etAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
										if(!city.isEmpty() && !city.equals("City")){
											etCity.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
											if(!country.isEmpty() && !country.equals("Country")){
												etCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
												if(!number.isEmpty() && !number.equals("Phone")){
													etNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
													if(utils.isInternet()){
														// if there is internet then we parse the information to our server through php
														postParameters.add(new BasicNameValuePair("email", email)); 
														postParameters.add(new BasicNameValuePair("address", address));  
														postParameters.add(new BasicNameValuePair("city", city));  
														postParameters.add(new BasicNameValuePair("country", country));  
														postParameters.add(new BasicNameValuePair("username", user));
														postParameters.add(new BasicNameValuePair("phone", number));  
														// we convert the password into sha256
												        postParameters.add(new BasicNameValuePair("password", utils.sha256(retype)));
												        task = new RequestTask("register", postParameters);
												        task.setOnTaskCompleted(new OnTaskCompletedListener(){

															public void onTaskStarted() {

															}

															public void onTaskCompleted(String result) {
																try{
														        	result = result.replaceAll("\\s+", "");
														        	int res = Integer.valueOf(result);
														        	switch(res){
														        		case 0: 
														        			utils.showToast("Please try again.");
														        			break;
														        		case 1:
														        			utils.showToast("Registration successfully.");
														        			if(dialog.isShowing()) dialog.dismiss();
														        			// if it is ok then we store permanently the username and password into preferences, and store as it is logged
														        			utils.prefsEditor.putString("username", user);
														        			utils.prefsEditor.putString("password", retype);
														        			utils.prefsEditor.putBoolean("isLogged", true);
														        			utils.prefsEditor.commit();
														        			isLogged();
														        			break;
														        		case 2: 
														        			utils.showToast("This user already exists.");
														        			break;
														        	}
														        	utils.prefsEditor.commit();
														        }catch(Exception e){
														        	Log.e(DEBUG, e.toString());
														        }
															}
												        	
												        });
												        task.execute();
													}
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
		
		btnLogin.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				dialog = new MyAlertDialog(TabTheoLoaiSpActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				dialog.setContentView(R.layout.dialog_login);
				
				final EditText etUser = (EditText)dialog.findViewById(R.id.etUser);
				final EditText etPass = (EditText)dialog.findViewById(R.id.etPass);
				Button btnOk = (Button)dialog.findViewById(R.id.btnOK);

				btnOk.setOnClickListener(new OnClickListener(){
					public void onClick(View view){
						if(utils.isInternet()){
							// we set the variables for login.php
							postParameters.add(new BasicNameValuePair("username", etUser.getText().toString()));  
					        postParameters.add(new BasicNameValuePair("password", utils.sha256(etPass.getText().toString())));
					        task = new RequestTask("login", postParameters);
					        task.setOnTaskCompleted(new OnTaskCompletedListener(){

								public void onTaskStarted() {}

								public void onTaskCompleted(String result) {
									 try {  
							        	// Enter the remote php link
										 result = result.replaceAll("\\s+","");  
							        	//error.setText(res);  
							        	if(result.equals("1")) {
							        		utils.showToast("Correct Username or Password"); 
							        		// if it is ok then we store permanently the username and password into preferences, and store as it is logged
							        		utils.prefsEditor.putString("username", etUser.getText().toString());
						        			utils.prefsEditor.putString("password", etPass.getText().toString());
						        			utils.prefsEditor.putBoolean("isLogged", true);
						        			utils.prefsEditor.commit();
						        			isLogged();
						        			btnPaypal.setVisibility(View.VISIBLE);
							        		if(dialog.isShowing()) dialog.dismiss();
							        	} else  {
							        		utils.showToast("Sorry!! Incorrect Username or Password"); 
							        		etUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_error, 0);
							        		etPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_error, 0);
							        	}
							        	
							        	postParameters.removeAll(postParameters);
							        	
						        	} catch (Exception e) {  
						        		Log.e(DEBUG, e.toString());  
							        }
								}
					        	
					        });
					       task.execute();
						}
					}
				});
				
				if(dialog!=null) dialog.show();
			}
		});
		
		btnSearch.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View view, MotionEvent evt) {
				switch(evt.getAction()){
					case MotionEvent.ACTION_DOWN:
						btnSearch.setBackgroundResource(R.drawable.search_pressed);
						dialog = new MyAlertDialog(TabTheoLoaiSpActivity.this);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setCancelable(true);
						dialog.setContentView(R.layout.dialog_search);
						
						final EditText etTitle = (EditText)dialog.findViewById(R.id.etTitle);
						final EditText etName = (EditText)dialog.findViewById(R.id.etName);
						final EditText etCategory = (EditText)dialog.findViewById(R.id.etCategory);
						Button btnOk = (Button)dialog.findViewById(R.id.btnOK);
						
						btnOk.setOnClickListener(new OnClickListener(){
							public void onClick(View view) {
								titleSearch = etTitle.getText().toString();
								nameSearch = etName.getText().toString();
								catSearch = etCategory.getText().toString();
								Utils.IS_ENDED_PRODUCT_LIST = false;
								deleteProductLists();
								fillProductList();
								pAdapter.notifyDataSetChanged();
								if(dialog.isShowing()) dialog.dismiss();
							}
						});
						if(dialog!=null) dialog.show();
						break;
					case MotionEvent.ACTION_UP:
						btnSearch.setBackgroundResource(R.drawable.search);
						break;
				}
				return false;
			}
			
		});
		
		btnReload.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View view, MotionEvent evt) {
				switch(evt.getAction()){
					case MotionEvent.ACTION_DOWN:
						btnReload.setBackgroundResource(R.drawable.reload_pressed);
						Utils.IS_ENDED_PRODUCT_LIST = false;
						deleteProductLists();
						nameSearch = ""; titleSearch = ""; catSearch = "";
						sortOrder = null; sortType = null;
						fillProductList();
						pAdapter.notifyDataSetChanged();
						break;
					case MotionEvent.ACTION_UP:
						btnReload.setBackgroundResource(R.drawable.reload);
						break;
				}
				return false;
			}
			
		});
		
		btnList.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View view, MotionEvent evt) {
				switch(evt.getAction()){
					case MotionEvent.ACTION_DOWN:
						btnList.setBackgroundResource(R.drawable.list_pressed);
						dialog = new MyAlertDialog(TabTheoLoaiSpActivity.this);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setCancelable(true);
						dialog.setContentView(R.layout.dialog_sort);
						
						final CheckBox check = (CheckBox)dialog.findViewById(R.id.checkBox1);
						final RadioGroup radio = (RadioGroup)dialog.findViewById(R.id.radioGroup1);
						Button btnSort = (Button)dialog.findViewById(R.id.btnSort);
						
						btnSort.setOnClickListener(new OnClickListener(){
							public void onClick(View view) {
								int checkedRadioButton = radio.getCheckedRadioButtonId();
								
								switch (checkedRadioButton) {
									case R.id.radio0 : sortOrder = EcommerceDatabase.KEY_PPRICE;
										break;
									case R.id.radio1 : sortOrder = EcommerceDatabase.KEY_PTITLE;
										break;
									case R.id.radio2 : sortOrder = EcommerceDatabase.KEY_PCATEGORY;
										break;
								}
								
								if(check.isChecked()){
									sortType = "ASC";
								}else{
									sortType = "DESC";
								}
								
								if(dialog.isShowing()) dialog.dismiss();
								
								Utils.IS_ENDED_PRODUCT_LIST = false;
								deleteProductLists();
								fillProductList();
								pAdapter.notifyDataSetChanged();
								
							}
						});
						if(dialog!=null) dialog.show();
						break;
					case MotionEvent.ACTION_UP:
						btnList.setBackgroundResource(R.drawable.list);
						break;
				}
				return false;
			}
			
		});
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.i(DEBUG, "onDestroy.");
		Utils.IS_ENDED_PRODUCT_LIST = false;
	}

	protected void onPause() {
		super.onPause();
		IS_PAUSED = true;
		Log.i(DEBUG, "onPause.");
	}

	protected void onRestart() {
		super.onRestart();
		Log.i(DEBUG, "onRestart.");
	}

	protected void onResume() {
		super.onResume();
		Log.i(DEBUG, "onResume.");
	}

	protected void onStop() {
		super.onStop();
		Log.i(DEBUG, "onStop.");
	}
	
	/**
	 * Called when the current Window of the activity gains or loses focus. This is the best indicator of whether this activity is visible to the user.
	 * The default implementation clears the key tracking state, so should always be called.
	 */
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		AnimationDrawable frameAnimation = (AnimationDrawable) imgLoading.getBackground();
	    frameAnimation.start();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode) {
			case Activity.RESULT_OK:
				//The payment succeeded
				String payKey = data.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
				//Tell the user their payment succeeded
				utils.showToast("The payment succeeded.");
				fillCartList(true);
				db.deleteAll(EcommerceDatabase.TABLE_CART);
				break;
			case Activity.RESULT_CANCELED:
				//The payment was canceled
				//Tell the user their payment was canceled
				utils.showToast("The payment was canceled.");
				break;
			case PayPalActivity.RESULT_FAILURE:
				//The payment failed -- we get the error from the EXTRA_ERROR_ID and EXTRA_ERROR_MESSAGE
				String errorID = data.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
				String errorMessage = data.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
				//Tell the user their payment was failed.
				utils.showToast("Error: " + errorID + " " + errorMessage);
		}
		btnPaypal.updateButton();
	}
	
	/**
	 * Called by the system when the device configuration changes while your activity is running. Note that this will only be called if you have selected
	 *  configurations you would like to handle with the configChanges attribute in your manifest. If any configuration change occurs that is not selected 
	 *  to be reported by that attribute, then instead of reporting it the system will stop and restart the activity (to have it launched with the new configuration). 
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
    }
	
	/**
	 * This method set the events for the popupWindow which appears when we press long on an item from products list or promotions list, 
	 * @param viewInflated the view inflated for the popupWindows
	 * @param position the position of the item on the listview
	 * @param idList the list with pid, ppid
	 */
	public void setPopupListeners(View viewInflated, final int position, final ArrayList<Integer> idList){
  		Button btnPlus = (Button)viewInflated.findViewById(R.id.btnPlus);
		Button btnMinus = (Button)viewInflated.findViewById(R.id.btnMinus);
		Button btnOk = (Button)viewInflated.findViewById(R.id.btnOk);
		Button btnClose = (Button)viewInflated.findViewById(R.id.btnClose);
		final EditText etQty = (EditText)viewInflated.findViewById(R.id.etQty);
		final int id = idList.get(position);
		final int qty = db.getQty(id);
		etQty.setText("" + qty);
		
		// will add 1 from qty 
		btnPlus.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				int qty = Integer.valueOf(etQty.getText().toString());
				++qty;
				if(qty>0) etQty.setText("" + qty);
			}
		});
		
		// will substract 1 from qty 
		btnMinus.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				int qty = Integer.valueOf(etQty.getText().toString());
				--qty;
				// we don't want the nr to be lower then 0
				if(qty>0) etQty.setText("" + qty);
			}
		});
		
		btnOk.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				int qty = Integer.valueOf(etQty.getText().toString());
				if(qty!=0){
					// now will add a new row into cart table
					if(!db.hasIDCart(id)){
						db.createRowOnCart(id, qty);
					}else{
						db.updateRow(id, qty);
					}
					if(popup.isShowing()) popup.dismiss();
				}
			}
		});
		
		btnClose.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				if(popup.isShowing()) popup.dismiss();
			}
		});
	}
	
	/**
	 * This method helps us to dismiss the popup after an amount of time
	 * @param popup the popup that we want dismissed 
	 * @param msec the amount of time after we want to dismiss it
	 */
	public static void popupDismissTimer(final PopupWindow popup, int msec){
		Handler hand = new Handler();
    	Runnable timer = new Runnable(){
    		public void run(){
    			popup.dismiss();
    		}
    	};
    	hand.postDelayed(timer, msec);	
    }
	
	/**
	 * This method allows us to send to our friends the link of this app, if you insert numbers, the message will be sent through sms, if you insert an email will
	 * be sent via email. All this is possible through regular expressions. 
	 */
	private void regexText(){
		String sent = etEmail.getText().toString();
		// you have to insert something if you want to send, otherwise it will toast a message
		if(sent.length()!=0){
			// if the text it matches with the email pattern then will send this as an email
			if(sent.matches("\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b")){
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setType("message/rfc822");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ etEmail.getText().toString()});
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try this");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "<a href=\"" + Utils.LINK_MARKET + "\">" + Utils.LINK_MARKET + "</a>");
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				// if the text it matches with the phone nr pattern then will send this as a sms
			}else if(sent.matches("^[0-9+]{10,16}")){
				sms.sendTextMessage(sent, null, Utils.LINK_MARKET, null, null);
				utils.showToast("Message Sent.");
			}else{
				utils.showToast("Type a valid e-mail or a phone number.");
			}
		}else {
			utils.showToast("Text field is empty please try again.");
		}
	}
	
	/**
	 * This method checks if we already logged, if we are logged will show the paypal button and hide btnLogin, btnRegister
	 */
	public void isLogged(){
		if(utils.prefs.getBoolean("isLogged", false)){
			btnLogin.setVisibility(View.INVISIBLE);
			btnRegister.setVisibility(View.INVISIBLE);
		}else{
			btnLogin.setVisibility(View.VISIBLE);
			btnRegister.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * This method will change the background tabs when a tab is selected
	 * @param tabhost
	 */
	public static void setTabBackground(TabHost tabhost) {
	    for(int i=0;i<tabhost.getTabWidget().getChildCount();i++){
	        tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_normal); //unselected
	        setTabIndicator(tabhost, i, false);
	    }
	    tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundResource(R.drawable.back_tab_pressed); // selected
	    setTabIndicator(tabhost, tabhost.getCurrentTab(), true);
	}
	
	/**
	 * This method will set the indicator color and will change the drawable for the current tab
	 * @param tabhost the tabhost
	 * @param color in what color we want to change the text
	 * @param res represent the image resource for the tab
	 */
	public static void setCurrentTabIndicator(TabHost tabhost, String color, int res) {
	    ViewGroup tabIndicator = (ViewGroup) tabhost.getTabWidget().getChildTabViewAt(tabhost.getCurrentTab());
	    ImageView img = (ImageView)tabIndicator.getChildAt(0);
	    img.setImageResource(res);
	    TextView tv = (TextView)tabIndicator.getChildAt(1); 
	    tv.setTextColor(Color.parseColor(color));
	}
	
	/**
	 * This will set the tab indicator whether is selected or not
	 * @param tabhost the tabhost
	 * @param tab the tab to be modified
	 * @param bobo will make the difference either is selected or not
	 */
	public static void setTabIndicator(TabHost tabhost, int tab, boolean bobo) {
	    ViewGroup tabIndicator = (ViewGroup) tabhost.getTabWidget().getChildTabViewAt(tab);
	    ImageView img = (ImageView)tabIndicator.getChildAt(0);
	    TextView tv = (TextView)tabIndicator.getChildAt(1); 
	    translateInUp.cancel();
	    switch(tab){
	    	case 0: if(!bobo){
	    				img.setImageResource(R.drawable.shop);
	    			}else{
	    				img.setImageResource(R.drawable.shop_pressed);
	    				img.startAnimation(translateInUp);
	    			}
	    		break;
	    	case 1: if(!bobo){
						img.setImageResource(R.drawable.promotion);
					}else{
						img.setImageResource(R.drawable.promotion_pressed);
						img.startAnimation(translateInUp);
					}
	    		break;
	    	case 2: if(!bobo){
						img.setImageResource(R.drawable.email);
					}else{
						img.setImageResource(R.drawable.email_pressed);
						img.startAnimation(translateInUp);
					}
	    		break;
	    	case 3: if(!bobo){
						img.setImageResource(R.drawable.pay);
					}else{
						img.setImageResource(R.drawable.pay_pressed);
						img.startAnimation(translateInUp);
					}
	    		break;
	    }
	    if(!bobo) {
	    	tv.setTextColor(Color.parseColor("#FFFFFF"));
	    }else{
	    	tv.setTextColor(Color.parseColor("#BCC316"));
	    	tv.startAnimation(translateInDown);
	    }
	}
	
	/**
	 * This will return the date
	 */
	public String getCurrentDate(){
		return DateFormat.getDateTimeInstance().format(new Date());
	}
	
	public Animation translateUpIn(){
		AnimationSet set = new AnimationSet(true);
		
		TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
		translate.setDuration(500);
		set.addAnimation(translate);
		
		AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
		alpha.setDuration(200);
		set.addAnimation(alpha);
		
		return translate;
	}
	
	/**
	 * This table will get the size of mysql table, which will help us to compare with the size from our table stored on the phone
	 * @param table the table that we want to get the size
	 * @return the nr of total rows
	 */
	public int getMysqlTableSize(final String table){
		task = new RequestTask("tableSize", postParameters);
		task.setOnTaskCompleted(new OnTaskCompletedListener(){
			public void onTaskStarted() {}

			public void onTaskCompleted(String result) {
				try {
					JSONObject obj = new JSONObject(result);
					TABLE_SIZE = Integer.valueOf(obj.getString(table));
				} catch (JSONException e) {
					Log.e(DEBUG, "Error on getMysqlTableSize: " + e.toString());
				} catch (Exception e) {
					Log.e(DEBUG, "Error on getMysqlTableSize: " + e.toString());
				}
			}
		});
	
		return TABLE_SIZE;
	}
	
	/**
	 * This method will fill the cart list with the fields for the products that we want to buy
	 * @param boo it will be false when we want to fill the cartlist with all the items we want to buy, and true after we bought some items and we want
	 * to add them to mysql server on sales table
	 */
	private void fillCartList(boolean boo){
		// first we need to delete the content from the cart lists
		this.deleteCartLists();
		TOTAL_QTY = 0; TOTAL_USD = 0;
		int price, qty;
		// if is not 0, first will parse all the rows to arraylists from sqlite stored on the phone
		Cursor cursor = db.getJoinedTables();
		if(cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				if(boo == false){
					price = Integer.valueOf(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PPRICE)).replace("$", ""));
					qty = cursor.getInt(cursor.getColumnIndex(EcommerceDatabase.KEY_CQTY));
					cid.add(cursor.getInt(cursor.getColumnIndex(EcommerceDatabase.KEY_PID)));
	        		cName.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PNAME)));
	        		cTitle.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PTITLE)));
	        		cPrice.add(price + "$");
	        		cIcon.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PICON)));
	        		cQty.add(qty);
	        		TOTAL_QTY += qty;
	        		TOTAL_USD += qty * price;
				}else{
					// we insert a new row on sales on mysql db
					postParameters.removeAll(postParameters);
					price = Integer.valueOf(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PPRICE)).replace("$", ""));
					qty = cursor.getInt(cursor.getColumnIndex(EcommerceDatabase.KEY_CQTY));
					// if there is internet then we parse the information to our server through php
					postParameters.add(new BasicNameValuePair("pid", String.valueOf(cursor.getInt(cursor.getColumnIndex(EcommerceDatabase.KEY_PID))))); 
					Log.i(DEBUG, utils.prefs.getString("username", ""));
					postParameters.add(new BasicNameValuePair("user", utils.prefs.getString("username", "")));  
					postParameters.add(new BasicNameValuePair("sQty", String.valueOf(qty)));  
					postParameters.add(new BasicNameValuePair("sDate", String.valueOf(getCurrentDate())));  
					postParameters.add(new BasicNameValuePair("sTotal", String.valueOf(qty * price)));
			        
//			        String response = null;
//			        
//			        try{
//			        	response = CustomHttpClient.executeHttpPost(Utils.LINK_SALES, postParameters).toString(); 
////			        	response = response.replaceAll("\\s+", "");
//			        	//int res = Integer.valueOf(response);
//			        	Log.i(DEBUG, response);
//			        } catch (Exception e) {  
//		        		Log.e(DEBUG, "Error at fillCartList(): " + e.toString());  
//			        }
				}
				cursor.moveToNext();
			}
		}
		tvValueTotalQty.setText("" + TOTAL_QTY);
        tvValueTotalUSD.setText(TOTAL_USD + "$");
	}
	
	/**
	 * This method will fill the products listview, first we check if we already downloaded something by taking the size of the sqlite table, if it is 0
	 * we start to parse data from the mysql db startig from 0 and taking 10 rows(see more on mysql LIMIT). If the size of the table is different than 0,
	 * then first we fill the list with the date stored on the phone on sqlite, and then we parse from the table size from mysql, and together will make our list.
	 * On every json array will add a new row on our table.
	 * This method has a flow, because not all the time will coincide the sqlite db with mysql db.
	 * If there is no connection then will get all the data from sqlite db.
	 */

	private void fillProductList(){
		// we check if there is an internet connection and if the address it is ok
		if(utils.isInternet() && CustomHttpClient.isAddressOk(Utils.LINK_PRODUCTS)){
			// this represent the starting point from MYSQL LIMIT, so if it 10 will return from the 10th row
			// we need to know how many rows we downloaded before
			final int from = pid.size();
			// this represent how many rows will return from the starting point from MYSQL LIMIT
			final int nr = 5;
//			long sqliteSize = db.size(EcommerceDatabase.TABLE_PRODUCTS);
        	
			// if this is 0 then we will take the first 10 rows from mysql
				if(from == 0){
					// we add the post variables and values for the request
					postParameters.add(new BasicNameValuePair("from", String.valueOf(0).toString())); 
					postParameters.add(new BasicNameValuePair("nr", String.valueOf(nr).toString()));  
				}else{
					postParameters.add(new BasicNameValuePair("from", String.valueOf(from).toString())); 
					postParameters.add(new BasicNameValuePair("nr", String.valueOf(nr).toString()));  
				}
				postParameters.add(new BasicNameValuePair("title", titleSearch)); 
				postParameters.add(new BasicNameValuePair("name", nameSearch));  
				postParameters.add(new BasicNameValuePair("category", catSearch));  
				if(sortOrder!=null) postParameters.add(new BasicNameValuePair("order", sortOrder));
				if(sortType!=null) postParameters.add(new BasicNameValuePair("by", sortType));
				task = new RequestTask("products", postParameters);
	        	task.setOnTaskCompleted(new OnTaskCompletedListener(){
	        		public void onTaskStarted() {
	        			if(!rlLoading.isShown()){
		        			rlLoading.startAnimation(fadeIn());
		        			rlLoading.setVisibility(View.VISIBLE);
	        			}
	        			IS_PRODUCTS_TASK = true;
					}
					public void onTaskCompleted(String result) {
				        try {  
				        	if(result!=""){
					        	// Enter the remote php link 
					        	// we convert the response into json array
					        	jarray = utils.getJarrayFromString(result);
					        	
					        	// we get how many rows are in total for a query
					        	int mysqlSize = (jarray.getJSONObject(0).getInt("numRows"));
					        	
					        	Log.i(DEBUG, "From " + from + " to " + mysqlSize);
					        	
					        	// we check to see if we got all the rows from the mysql
					        	if(from <= mysqlSize){
					        		int rows;
						        	// we check to see if there is 0
						        	if(jarray.length()>0){
						        		// this will help us to get the exact amount of rows, otherwise because when we gather data from mysql we take from 5 to 5 rows, the total nr.
						        		// of rows from our algorithm will not be always a nr. that divides with 5, so when it reaches to the nearest number that divides with 5 
						        		Log.i(DEBUG, "From " + from + " to " + Math.floor(mysqlSize/nr)*nr);
						        		if(from+5<=Math.floor(mysqlSize/nr)*nr){
						        			rows = jarray.length();
						        		}else{
						        			rows = mysqlSize%nr+1;
						        			Utils.IS_ENDED_PRODUCT_LIST = true;
						        		}
						        		ArrayList<String> list = new ArrayList<String>();
							        	for(int i=1; i<rows; i++){
							        		JSONObject row = jarray.getJSONObject(i);
							        		pid.add(row.getInt("pid"));
							        		pName.add(row.getString("pName"));
							        		pTitle.add(row.getString("pTitle"));
							        		pPrice.add(row.getString("pPrice") + "$");
							        		pDescription.add(row.getString("pDescription"));
							        		pCategory.add(row.getString("pCategory"));
							        		pIcon.add(Utils.PATH + row.getString("pIcon"));
							        		list.add(row.getString("pIcon"));
							        		// we check if this id already exists in the db, if it doesn't exists we create new one
							        		if(!db.hasIDProducts(row.getInt("pid"))) db.createRowOnProducts(row.getInt("pid"), row.getString("pName"), row.getString("pTitle"), row.getString("pPrice"), row.getString("pDescription"), row.getString("pCategory"), Utils.PATH + row.getString("pIcon"), row.getString("pPromotion"), row.getInt("pDiscount"), row.getString("pDate"));
							        		Log.i(DEBUG, row.getString("pDescription"));
							        	}
							        	new DownloadImages(list, pAdapter).execute();
						        	}
					        	}
					        	postParameters.removeAll(postParameters);
				        	}else{
				        		Utils.IS_ENDED_PRODUCT_LIST = true;
				        		if(rlLoading.isShown()){
				    				rlLoading.startAnimation(fadeOut());
				    				rlLoading.setVisibility(View.INVISIBLE);
				    			}
				        	}
			        	} catch (Exception e) {  
			        		Log.e(DEBUG, "Error at fillProductList(): " + e.toString());  
				        }
					}
	        	});
	        	task.execute();

		}else{
			// if we are not connected on internet or somehow the link would not work, then we will take the rows stored in sqlite db
			if(db.size(EcommerceDatabase.TABLE_PRODUCTS) > 0){
				Cursor cursor = db.getProductsRows(EcommerceDatabase.TABLE_PRODUCTS);
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
					pid.add(cursor.getInt(cursor.getColumnIndex(EcommerceDatabase.KEY_PID)));
	        		pName.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PNAME)));
	        		pTitle.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PTITLE)));
	        		pPrice.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PPRICE))+ "$");
	        		pDescription.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PDESCRIPTION)));
	        		pCategory.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PCATEGORY)));
	        		pIcon.add(cursor.getString(cursor.getColumnIndex(EcommerceDatabase.KEY_PICON)));
	        		cursor.moveToNext();
				}
				pAdapter.notifyDataSetChanged();
				Utils.IS_ENDED_PRODUCT_LIST = true;
			}
		}
	}
	
	/**
	 * This method will fill the lvPromotions, first will check to see if you are connected to the internet and if the address it is working, if it true 
	 * than will start to parse from mysql only the products that are on a promotion(WHERE pPromotion=true) database through json, an each row parsed from 
	 * mysql, it will create a new row on sqlite db, if it is false then will try to parse from sqlite db. Here we use promotions.php to access data.
	 */
	
	/**
	 * This method will change the sliding button background according to slidingdrawer visibility
	 * @param sd the slidingdrawer
	 * @param btn the sliding button
	 */
	private void slidingDrawerHandle(SlidingDrawer sd, final Button btn){
		sd.setOnDrawerOpenListener(new OnDrawerOpenListener(){
			public void onDrawerOpened() {
				btn.setBackgroundResource(R.drawable.br_down);
			}
		});

		sd.setOnDrawerCloseListener(new OnDrawerCloseListener(){
			public void onDrawerClosed() {
				btn.setBackgroundResource(R.drawable.br_up);
			}
		});
	}
	
	/**
	 * This will the delete the content for the cart lists
	 */
	private void deleteCartLists(){
		cid.removeAll(cid);
		cName.removeAll(cName);
		cTitle.removeAll(cTitle);
		cPrice.removeAll(cPrice);
		cIcon.removeAll(cIcon);
		cQty.removeAll(cQty);
	}
	
	/**
	 * This will the delete the content for the products lists
	 */
	private void deleteProductLists(){
		pid.removeAll(pid);
		pName.removeAll(pName);
		pTitle.removeAll(pTitle);
		pPrice.removeAll(pPrice);
		pIcon.removeAll(pIcon);
		pCategory.removeAll(pCategory);
		pDescription.remove(pDescription);
	}

	private static class PromotionsHolder{
    	ImageView imgIcon;
    	TextView tvName, tvPrice, tvTitle;
    }
    
   
    
    private static class ProductsHolder{
    	ImageView imgIcon;
    	TextView tvName, tvPrice, tvTitle;
    }
    
    private class ProductsAdapter extends BaseAdapter{
    	private LayoutInflater inflater;
    	private ArrayList<String> list;
    	
    	public ProductsAdapter(ArrayList<String> ll){
    		inflater = (LayoutInflater)TabTheoLoaiSpActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		list = ll;
    	}
    	
    	//How many items are in the data set represented by this Adapter.
		public int getCount() {
			return list.size();
		}
    			
		//Get the data item associated with the specified position in the data set.
		public Object getItem(int position) {
			return list.get(position);
		}

		//Get the row id associated with the specified position in the list.
		public long getItemId(int position) {
			return position;
		}
		
		//Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) 
		//will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a root view and to prevent attachment to the root.
		public View getView(int position, View convertView, ViewGroup parent) {
			ProductsHolder holder; 
			if(convertView == null){
				convertView = inflater.inflate(R.layout.item_product, null);
				holder = new ProductsHolder();
				holder.imgIcon = (ImageView)convertView.findViewById(R.id.imgIcon);
				holder.imgIcon.getLayoutParams().height = 80;
				holder.imgIcon.getLayoutParams().width = 80;
				holder.tvName = (TextView)convertView.findViewById(R.id.tvName);
				holder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
				holder.tvPrice = (TextView)convertView.findViewById(R.id.tvPrice);
				convertView.setTag(holder);
			}else{
				holder = (ProductsHolder)convertView.getTag();
			}
			Bitmap bmp = BitmapFactory.decodeFile(pIcon.get(position));
			holder.imgIcon.setImageBitmap(bmp);
			holder.tvName.setText(pName.get(position));
			holder.tvTitle.setText(pTitle.get(position));
			holder.tvPrice.setText(pPrice.get(position));
		
			return convertView;
		}
    }
    
    private static class CartHolder{
    	ImageView imgIcon;
    	TextView tvPrice, tvTitle, tvQty, tvTotals;
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
	
	class DownloadImages extends AsyncTask<Void, Void, Void> {
		private ArrayList<String> images = new ArrayList<String>();
		private BaseAdapter adapter;
	   
		public DownloadImages(ArrayList<String> images, BaseAdapter adapter){
			this.images = images;
			this.adapter = adapter;
		}

		protected Void doInBackground(Void... arg0) {
			for(int i=0; i<images.size(); i++){
				utils.downloadImage(Utils.LINK_IMAGES + images.get(i), images.get(i));
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
			if(rlLoading.isShown()){
				rlLoading.startAnimation(fadeOut());
				rlLoading.setVisibility(View.INVISIBLE);
			}
			if(IS_FIRST_TIME) {
				lvProducts.startAnimation(scaleIn);
				lvPromotions.startAnimation(scaleIn);
				IS_FIRST_TIME = false;
			}
			lvProducts.setVisibility(View.VISIBLE);
			lvPromotions.setVisibility(View.VISIBLE);
			IS_PROMOTIONS_TASK = false;
			IS_PRODUCTS_TASK = false;
		}
    }
	
	
	public Animation fadeIn(){
		AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
		alpha.setDuration(500);
		return alpha;
	}
	
	public Animation fadeOut(){
		AlphaAnimation alpha = new AlphaAnimation(1f, 0f);
		alpha.setDuration(500);
		return alpha;
	}
}
