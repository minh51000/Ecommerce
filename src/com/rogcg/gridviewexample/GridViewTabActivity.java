package com.rogcg.gridviewexample;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.djcharon.ecommerce.ActivityTab;
import com.djcharon.ecommerce.R;
import com.djcharon.ecommerce.TabTheoLoaiSpActivity;
import com.example.network.LoaiSanPham;
import com.example.network.ServiceHandler;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
//14 thang 6 10 gio 45

public class GridViewTabActivity extends Activity
{   
	
	private String URL_CATEGORIES = "http://www.sieustore.com/get_loai_san_pham.php";
	private ArrayList<LoaiSanPham> loaiSanPhamList;
	private static final String LINK_ANH_LOAI_SAN_PHAM="http://www.sieustore.com/hinh_loai_san_pham/";
	ProgressDialog pDialog;
	GridView gridView ;
	private int count=0;
	


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_layout);
        Intent i = getIntent();  
       
       
        loaiSanPhamList=new ArrayList<LoaiSanPham>();
       
        new GetCategories().execute();

     gridView = (GridView)findViewById(R.id.gridview);
     
    

        
    }
    class GetCategories extends AsyncTask<Void, Void, Void> {
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		pDialog = new ProgressDialog(GridViewTabActivity.this);
    		pDialog.setMessage("Fetching food categories..");
    		pDialog.setCancelable(false);
    		pDialog.show();
    	}
    	@Override
    	protected Void doInBackground(Void... arg0) {
    		ServiceHandler jsonParser = new ServiceHandler();// track1 copy file Servicer handler vao
    		String json = jsonParser.makeServiceCall(URL_CATEGORIES, ServiceHandler.GET);
    		Log.e("Response: ", "> " + json);
    		if (json != null) {
    			try {
    				JSONObject jsonObj = new JSONObject(json);// loi o day, json obj=null;;
    				if (jsonObj != null) {
    					JSONArray categories = jsonObj
    							.getJSONArray("categories");	
    					count=categories.length();
    					for (int i = 0; i < categories.length(); i++) {
    						JSONObject catObj = (JSONObject) categories.get(i);
    						LoaiSanPham cat = new LoaiSanPham(
    								catObj.getString("TenLoaiSp"),
    								catObj.getString("HinhLoaiSp")		
    								); 
    						loaiSanPhamList.add(cat);						
    					}
    				}

    			} catch (JSONException e) {
    				e.printStackTrace();
    				Log.e("test1","exction");
    			}

    		} else {
    			Log.e("JSON Data", "Didn't receive any data from server!");
    		}
    		return null;
    	}

    	@Override
    	protected void onPostExecute(Void result) {
    		super.onPostExecute(result);
    		if (pDialog.isShowing())
    			pDialog.dismiss();
    		gridView.setAdapter(new MyAdapter(getApplicationContext()));
                          //track3: set adapter du lieu o day
    	        //mGrid.setAdapter(new AppsAdapter());   
    		 gridView.setOnItemClickListener(new OnItemClickListener() {
    				public void onItemClick(AdapterView<?> parent, View v,
    					int position, long id) {
    					SharedPreferences sharedPref = getPreferences(Context.MODE_WORLD_WRITEABLE);
    					SharedPreferences.Editor editor = sharedPref.edit();
    					//editor.putString("loaiSp", loaiSanPhamList.get(position).getTenLoaiSp());
    					editor.putString("loaiSp", loaiSanPhamList.get(position).getTenLoaiSp());
    					editor.commit();
    					//activity.refreshContent();
    					switchTabInActivity(1,loaiSanPhamList.get(position).getTenLoaiSp());
    					// Intent i = new Intent(getBaseContext(), TabTheoLoaiSpActivity.class); 
    					// startActivity(i);
    					
    					
    				}
    			});
    	}

    }
    public void switchTabInActivity(int indexTabToSwitchTo,String searchName){
        ActivityTab parentActivity;
        parentActivity = (ActivityTab) this.getParent();
        parentActivity.switchTab(indexTabToSwitchTo,searchName);
      
}
    class MyAdapter extends BaseAdapter
    {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public MyAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);

            items.add(new Item("Image 1", R.drawable.nature1));
            items.add(new Item("Image 2", R.drawable.nature2));
            items.add(new Item("Image 3", R.drawable.tree1));
            items.add(new Item("Image 4", R.drawable.nature3));
            items.add(new Item("Image 5", R.drawable.tree2));
            
        }

        @Override
        public int getCount() {
        	
        	Log.e("positionTest2",String.valueOf(Log.e("positionTest",String.valueOf(loaiSanPhamList.size()))));
           
        
            return (loaiSanPhamList.size());//000 5 loi ma 6 ko loi-->van de o arralist ko co 6 phan tu
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;
            TextView name;

            if(v == null)
            {
               v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
               v.setTag(R.id.picture, v.findViewById(R.id.picture));
               v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.text);
             
           // Item item = (Item)getItem(i);
 
           // picture.setImageResource(item.drawableId);
           // getImagesName(i);
            //000
            UrlImageViewHelper.setUrlDrawable(picture, LINK_ANH_LOAI_SAN_PHAM+getImagesName(i));// invalid index, index qua lon xem o phan count
            name.setText(loaiSanPhamList.get(i).getTenLoaiSp());
    
            return v;
        }

        private String getImagesName(int position) {
			// TODO Auto-generated method stub	
        	//000 size is 5-->set loaisanphamlist size la 6
        	Log.e("positionTest",String.valueOf(position));
        	String c=loaiSanPhamList.get(position).getHinhAnhLoaiSp();
        	
        	return c;
		}

		private class Item
        {
            final String name;
            final int drawableId;

            Item(String name, int drawableId)
            {
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }

}