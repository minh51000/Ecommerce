package com.djcharon.ecommerce;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * This class will allow us to change the size of view, layouts according to the screen resolution
 */
public class Resizable {
	private Context context;
	private Activity activity;
	public static int MOCKUP_WIDTH, MOCKUP_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT, DENSITY = 160;
	
	/**
	 * @param cont represent the context from where this class is initialized
	 * @param act represent the activity from where this class is initialized
	 * @param width represent the width of the fireworks, photoshop mockup, so all the views will be changed after this width
	 * @param height width represent the width of the fireworks, photoshop mockup, so all the views will be changed after this width
	 */
	public Resizable(Context cont, Activity act, int width, int height){
		context = cont;
		MOCKUP_WIDTH = width;
		MOCKUP_HEIGHT = height;
		activity = act;
		getScreenSize();
	}
	
	/**
	 * @param cont represent the context from where this class is initialized
	 * @param width represent the width of the fireworks, photoshop mockup, so all the views will be changed after this width
	 * @param height width represent the width of the fireworks, photoshop mockup, so all the views will be changed after this width
	 * @param density represent the folder where are the drawables
	 */
	public Resizable(Context cont, Activity act, int width, int height, int density){
		DENSITY = density;
		context = cont;
		MOCKUP_WIDTH = width;
		MOCKUP_HEIGHT = height;
		activity = act;
		getScreenSize();
	}
	
	private void getScreenSize(){
 		//A structure describing general information about a display, such as its size, density, and font scaling.
     	DisplayMetrics metrics = new DisplayMetrics(); 
     	activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);  
 		SCREEN_WIDTH = metrics.widthPixels; 
 		SCREEN_HEIGHT = metrics.heightPixels;
	}
	
	/**
 	 * This method will resize the view after the width
 	 * @param viewId represent the view id that will be resized
 	 * @param drawableId represent the drawable resource from where the view will be resized
 	 */
 	public void resizeView(int viewId, int drawableId) {
 		double ratio;
 		int imgWidth, imgHeight, newWidth, newHeight;
 		
 		View view = (View)activity.findViewById(viewId);
 		
 		// Be careful if you use multiple density drawable folders under res, and make sure you specify 
 		// inTargetDensity on your BitmapFactory.Options to get the drawable of the density you want.
 		BitmapFactory.Options options = new BitmapFactory.Options();
 		options.inTargetDensity = DENSITY;
 		// This will decode a drawable from resource into a bitmap
 		Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), drawableId, options);
 		imgWidth = bmp.getWidth();
 		imgHeight = bmp.getHeight();

 		// we take the orientation
 		int orientation = activity.getResources().getConfiguration().orientation;

 		// if orieantation is 1 it means that is in portrait mode
 		if(orientation == 1){
 			// when is in portrait mode we resize after width, so we need to know how many times the image width will fits in the screen
 			ratio = (double)MOCKUP_WIDTH/(double)SCREEN_WIDTH;
 		}else{
 			// when is in landscape mode we resize after height, so we need to know how many times the image height will fits in the screen
 			ratio = (double)MOCKUP_HEIGHT/(double)SCREEN_HEIGHT;
 		}
 		
 		newWidth = (int) (imgWidth / ratio);
 		newHeight = (int) (imgHeight / ratio);
 		
 		if(view!=null){
 			view.getLayoutParams().height = newHeight;
			view.getLayoutParams().width = newWidth;
 		}else{
 			Log.i("Resizable", "View Is Null."); 
 		}
 	}
 	
 	/**
 	 * This method will resize the view after the width
 	 * @param view represent the view that will be resized
 	 * @param drawableId represent the drawable resource from where the view will be resized
 	 */
 	public void resizeView(View view, int drawableId) {
 		double ratio;
 		int imgWidth, imgHeight, newWidth, newHeight;
 		
 		// Be careful if you use multiple density drawable folders under res, and make sure you specify 
 		// inTargetDensity on your BitmapFactory.Options to get the drawable of the density you want.
 		BitmapFactory.Options options = new BitmapFactory.Options();
 		options.inTargetDensity = DENSITY;
 		// This will decode a drawable from resource into a bitmap
 		Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), drawableId, options);
 		imgWidth = bmp.getWidth();
 		imgHeight = bmp.getHeight();

 		// we take the orientation
 		int orientation = activity.getResources().getConfiguration().orientation;

 		// if orieantation is 1 it means that is in portrait mode
 		if(orientation == 1){
 			// when is in portrait mode we resize after width, so we need to know how many times the image width will fits in the screen
 			ratio = (double)MOCKUP_WIDTH/(double)SCREEN_WIDTH;
 		}else{
 			// when is in landscape mode we resize after height, so we need to know how many times the image height will fits in the screen
 			ratio = (double)MOCKUP_HEIGHT/(double)SCREEN_HEIGHT;
 		}
 		
 		newWidth = (int) (imgWidth / ratio);
 		newHeight = (int) (imgHeight / ratio);
 		
 		if(view!=null){
 			view.getLayoutParams().height = newHeight;
			view.getLayoutParams().width = newWidth;
			
			Log.i("Resizable", "SCREEN WIDTH " + SCREEN_WIDTH);
			Log.i("Resizable", "Ratio " + ratio);
			Log.i("Resizable", "imgWidth " + imgWidth + " imgHeight " + imgHeight);
			Log.i("Resizable", "newWidth " + newWidth + " new Height " + newHeight);  
 		}else{
 			Log.i("Resizable", "View Is Null."); 
 		}
 	}
 	
 	public static int getDrawableHeight(Resources res, int resID){
 		BitmapFactory.Options options = new BitmapFactory.Options();
 		options.inTargetDensity = 240;
 		Bitmap bmp = BitmapFactory.decodeResource(res, resID, options);
 		return bmp.getHeight();
 	}
 	
	public static int getDrawableWidth(Resources res, int resID){
 		BitmapFactory.Options options = new BitmapFactory.Options();
 		options.inTargetDensity = 240;
 		Bitmap bmp = BitmapFactory.decodeResource(res, resID, options);
 		return bmp.getWidth();
 	}
}
