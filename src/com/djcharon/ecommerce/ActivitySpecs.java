package com.djcharon.ecommerce;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivitySpecs extends Activity{
	private EcommerceDatabase db;
	
	private ImageView imgSpecIcon;
	private TextView tvSpecName, tvSpecTitle, tvSpecPrice, tvSpecDesc;
	
	private final String DEBUG = "ActivitySpecs";
	
	private int PID;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specs);
		
		db = new EcommerceDatabase(ActivitySpecs.this);
		
		tvSpecName = (TextView)findViewById(R.id.tvSpecName);
		tvSpecTitle = (TextView)findViewById(R.id.tvSpecTitle);
		tvSpecPrice = (TextView)findViewById(R.id.tvSpecPrice);
		tvSpecDesc = (TextView)findViewById(R.id.tvDescription);
		
		imgSpecIcon = (ImageView)findViewById(R.id.imgSpecIcon);
		
		getExtraPID();
		
		Log.i(DEBUG, "PID " + PID);
	}

	protected void onStart() {
		super.onStart();
		Bitmap bmp = BitmapFactory.decodeFile(db.getIcon(PID));
		tvSpecName.setText(db.getName(PID));
		tvSpecTitle.setText(db.getTitle(PID));
		tvSpecPrice.setText(db.getPrice(PID) + "$");
		tvSpecDesc.setText(db.getDescription(PID));
		Log.i(DEBUG, db.getDescription(PID));
		imgSpecIcon.setImageBitmap(bmp);
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onStop() {
		super.onStop();
	}

	public void getExtraPID(){
		Intent intent = getIntent();
	    if(intent != null) {
	        PID = intent.getIntExtra("pid", 0);
	    }
	}
}
