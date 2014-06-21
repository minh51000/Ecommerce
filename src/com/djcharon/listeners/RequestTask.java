package com.djcharon.listeners;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import com.djcharon.ecommerce.CustomHttpClient;
import com.djcharon.ecommerce.Utils;

import android.os.AsyncTask;
import android.util.Log;

public class RequestTask extends AsyncTask<Void, Void, String>{
	public final String DEBUG = "SendPooTask";
	public OnTaskCompletedListener listener;
	private String request; 
	private ArrayList<NameValuePair> postParameters;
	
	public RequestTask(String request, ArrayList<NameValuePair> postParameters){
		this.request = request; 
		this.postParameters = postParameters;
	}
	
	public void setOnTaskCompleted(OnTaskCompletedListener listener){
		this.listener = listener;
	}	
	
	protected void onPreExecute() {
		super.onPreExecute();
		listener.onTaskStarted();
	}

	protected String doInBackground(Void... params) {
		String result = null;
		try {
			if(request.equals("login")) result = CustomHttpClient.executeHttpPost(Utils.LINK_LOGIN, postParameters).toString();
			if(request.equals("register")) result = CustomHttpClient.executeHttpPost(Utils.LINK_REGISTER, postParameters).toString(); 
			if(request.equals("promotions")) result = CustomHttpClient.executeHttpPost(Utils.LINK_PROMOTIONS, postParameters).toString(); 
			if(request.equals("products")) result = CustomHttpClient.executeHttpPost(Utils.LINK_PRODUCTS, postParameters).toString(); 
			if(request.equals("tableSize")) result = CustomHttpClient.executeHttpPost(Utils.LINK_TABLE_SIZE, postParameters).toString(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
	
	protected void onPostExecute(String result){
		listener.onTaskCompleted(result);	
	}

}
