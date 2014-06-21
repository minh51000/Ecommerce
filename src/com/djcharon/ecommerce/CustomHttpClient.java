package com.djcharon.ecommerce;  

import java.io.BufferedInputStream;
import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;
import java.io.InputStreamReader;  
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;  
import java.net.URL;
import java.util.ArrayList;  
import org.apache.http.HttpResponse;  
import org.apache.http.NameValuePair;  
import org.apache.http.client.HttpClient;  
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;  
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;  
import org.apache.http.params.HttpParams;  

import android.util.Log;
      
public class CustomHttpClient {  
    /** The time it takes for our client to timeout */  
    public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds  
    /** Single instance of our HttpClient */  
    private static HttpClient mHttpClient;  
    private static String DEBUG = "CustomHttpClient";
    
    /** 
    * Get our single instance of our HttpClient object. 
    * 
    * @return an HttpClient object with connection parameters set 
    */  
    private static HttpClient getHttpClient() {  
    	if (mHttpClient == null) {  
    		mHttpClient = new DefaultHttpClient();  
    		final HttpParams params = mHttpClient.getParams();  
    		HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);  
    		HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);  
    		ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);  
    	}  
    	return mHttpClient;  
    }  
    
    public static DefaultHttpClient getThreadSafeClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
   
        return client;
    }
    /** 
    * Performs an HTTP Post request to the specified url with the 
    * specified parameters. 
    * 
    * @param url The web address to post the request to 
    * @param postParameters The parameters to send via the request 
    * @return The result of the request 
    * @throws Exception 
    */  
    public static String executeHttpPost(String url, ArrayList postParameters) throws Exception {  
    	BufferedReader in = null;  
    	try {  
    		HttpClient client = getThreadSafeClient();  
    		HttpPost request = new HttpPost(url);  
    		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);  
    		request.setEntity(formEntity);  
    		HttpResponse response = client.execute(request);  
    		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));  
    		StringBuffer sb = new StringBuffer("");  
    		String line = "";  
    		String NL = System.getProperty("line.separator");  
    		while ((line = in.readLine()) != null) {  
    			sb.append(line + NL);  
    		}  
    		in.close();  
    		String result = sb.toString();  
    		return result;  
    	} finally {  
    		if (in != null) {  
    			try {  
    				in.close();  
    			} catch (IOException e) {  
    				e.printStackTrace();  
    			}
    		}
    	}
    }
    
    public static String executeHttpGet(String url) throws Exception {  
    	BufferedReader in = null;  
    	try {  
    		HttpClient client = getThreadSafeClient();  
    		HttpGet request = new HttpGet();  
    		request.setURI(new URI(url));  
    		HttpResponse response = client.execute(request);  
    		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));  
    		StringBuffer sb = new StringBuffer("");  
    		String line = "";  
    		String NL = System.getProperty("line.separator");  
    		while ((line = in.readLine()) != null) {  
    			sb.append(line + NL);  
    		}  
    		in.close();  
    		String result = sb.toString();  
    		return result;  
    	} finally {  
    		if (in != null) {  
    			try {  
    				in.close();  
    			} catch (IOException e) {  
    				e.printStackTrace();  
    			}
    		}
    	}
    }
    
    /**
     * This method verifies if an url is OK by requesting the status code
     * @param link repr. the url
     * @return true is the url exists, false if it doesn't
     */
    public static boolean isAddressOk(String link){
    	boolean isOK = false;
        try {
            URL url = new URL(link);
            HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
            urlCon.connect();
            if(urlCon.getResponseCode() == 200) {
                isOK = true;
                Log.i(DEBUG, "TRUE");
            }else{
            	isOK = false;
            	Log.i(DEBUG, "FALSE");
            }
            urlCon.disconnect();
        } catch (MalformedURLException e1) {
            isOK = false;
            Log.e(DEBUG, "FALSE");
            e1.printStackTrace();
        } catch (IOException e) {
            isOK = false;
            Log.e(DEBUG, "FALSE");
            e.printStackTrace();
        }
        
        return isOK;
    }
}  
