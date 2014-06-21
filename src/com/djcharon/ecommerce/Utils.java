package com.djcharon.ecommerce;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class Utils {
	private Context context;
	private Activity activity;
	public SharedPreferences prefs;
	public SharedPreferences.Editor prefsEditor;
	private final String DEBUG = "EcommerceUtils";
	private JSONArray jarray;
	private JSONObject json;
	public boolean PASSWORDS_MATCH = false;
	
	// This flag will be true when the we parsed all the data from the php script, this will target the products script
	public static boolean IS_ENDED_PRODUCT_LIST = false;
	
	// This flag will be true when the we parsed all the data from the php script, this will target the promotions cript
	public static boolean IS_ENDED_PROMO_LIST = false;
	
	// This is the path where the files will be downloaded
	public static final String PATH = Environment.getExternalStorageDirectory() + "/Ecommerce/";
	
	// this represent the link to our site
	public static final String LINK_ROOT = "http://www.sieustore.com/";
	
	// this represent the link to our login API
	public static final String LINK_LOGIN = LINK_ROOT + "android/login.php";
	
	// this represent the link to our register API
	public static final String LINK_REGISTER = LINK_ROOT + "android/register.php";
	
	// this represent the link to our products API, which will parse data from mysql db
	public static final String LINK_PRODUCTS = LINK_ROOT + "android/products.php";
	
	// this represent the link to our images from where will download all the images
	public static final String LINK_IMAGES = LINK_ROOT + "images/";
	
	// this represent the link to the script that shows how many rows has tables
	public static final String LINK_TABLE_SIZE = LINK_ROOT + "android/TableSize.php";
	
	// this represent the link to market from where you can download this app
	public static final String LINK_MARKET = "https://play.google.com/store";
	
	// this represent the link to the script that insert a new sale int sales table
	public static final String LINK_SALES = LINK_ROOT + "android/sales.php";
	
	// this represent the link to the script that insert a new sale int sales table
	public static final String LINK_PROMOTIONS = LINK_ROOT + "android/promotions.php";
	
	public static final String LINK_REQUEST_AS_TYPE = LINK_ROOT + "android/requestAsType.php";
	
	public static final String PAYPAL_ACCOUNT = "etien_1335682646_per@yahoo.com";
	
	public Utils(Activity act){
		activity = act;
		prefs = activity.getSharedPreferences("ecommerce", Context.MODE_WORLD_READABLE);
		prefsEditor = prefs.edit();
	}
	
	 /**
     * This method verify if the phone is connected to internet through wi-fi or 3G
     * @return true if there is a conection, false if there is not
     */
    public boolean isInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!networkInfo.isAvailable()) {
            	networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }
        }
        if(networkInfo == null) {
        	showToast("You are not connected to internet.");
        	return false;
        }else{
         	if(networkInfo.isConnected()){
         		return true;
         	}else{
         		showToast("You are not connected to internet.");
         		return false;
         	}
        }
    }
    
    /**
     * This method will show a message
     * @param message represent the message
     */
    public void showToast(final String message){
    	Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    
    /**
     * This method transform the http request to json object
     * @param url the url from where will gather the json 
     * @return will return the json object if there is one 
     */
    public String getJsonFromUrl(String url){
    	
    	// initialize the objects
    	InputStream is = null;
    	String result = "";
    	
    	//http post
    	try {
    		HttpClient httpClient = new DefaultHttpClient();
    		HttpPost httpPost = new HttpPost(url);
    		HttpResponse response = httpClient.execute(httpPost);
    		HttpEntity entity = response.getEntity();
    		is = entity.getContent();
    	}catch(Exception e){
    		Log.e(DEBUG, "Error getJsonFromUrl: " + e.toString());
    	}
    	
    	//we need to convert the response to string
    	try{
    		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"), 8);
    		StringBuilder sb = new StringBuilder();
    		String line = null;
    		while((line = reader.readLine()) != null){
    			sb.append(line + "\n");
    		}
    		is.close();
    		result=sb.toString();
    	}catch(Exception e){
    		Log.e(DEBUG, "Error converting the response to string getJsonFromUrl: " + e.toString());
    	}

    	return result;
    }
    
    /**
     * This method transform the string resulted from a link into json object
     * @param result the string that need to be converted
     * @return will return the json object if there is one 
     */
    public JSONArray getJarrayFromString(String result){
    	//we need to parse the string to json object
    	try{
    		jarray = new JSONArray(result);
    	}catch(JSONException e){
    		Log.e(DEBUG, "Error parsing to json on getJarrayFromString(); " + e.toString());
    	}
    	
    	return jarray;
    }
    
    /**
     * This method transform the string resulted from a link into json object
     * @param result the string that need to be converted
     * @return will return the json object if there is one 
     */
    public JSONObject getJsonFromString(String result){
    	//we need to parse the string to json object
    	try{
    		json = new JSONObject(result);
    	}catch(JSONException e){
    		Log.e(DEBUG, "Error parsing to json on getJsonFromString(): " + e.toString());
    	}
    	return json;
    }
    
    /**
     * This method will download an image from an url
     * @param imgUrl the url for the image
     * @param filename the filename that will be stored on card
     */
    public void downloadImage(String imgUrl, String filename){
    	try {
    		Log.v(DEBUG, "imgUrl: " + imgUrl);
            URL url = new URL(imgUrl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            File file = new File(PATH);
            if(!file.exists()){
            	file.mkdirs();
            }
            File outputFile = new File(file, filename);
            // we check if the file exists which means that has been already downloaded
            
            if(!outputFile.exists()){
	            FileOutputStream fos = new FileOutputStream(outputFile);
	
	            InputStream is = c.getInputStream();
	
	            byte[] buffer = new byte[1024];
	            int len1 = 0;
	            while ((len1 = is.read(buffer)) != -1) {
	                fos.write(buffer, 0, len1);
	                if(len1==-1){
	                	Log.i(DEBUG, "Download finished");
	                }
	            }
	            fos.close();
	            is.close();
            }else{
            	Log.i(DEBUG, "File already exists.");
            }
        } catch (IOException e) {
            Log.e(DEBUG, "Error on downloadImage(): " + e.toString());
        }
    } 
    
    /**
     * This method will encrypt a password into a sha256
     * @param password the pass to encrypt
     * @return the password encrypted
     */
    public String sha256(String password){ 
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
			byte byteData[] = md.digest();
			  
        	//convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        
	        return sb.toString();
	        
		} catch (NoSuchAlgorithmException e) {
			Log.e(DEBUG, "Error on sha256(): " + e.toString());
		}

    	return "";
    }
}

