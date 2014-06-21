package com.djcharon.ecommerce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class EcommerceDatabase {
	public final String DEBUG = "EcommerceDatabase";
	
	public static final String TABLE_PRODUCTS = "products";
	public static final String KEY_PID = "pid";
	public static final String KEY_PNAME = "pName";
	public static final String KEY_PTITLE = "pTitle";
	public static final String KEY_PPRICE = "pPrice";
	public static final String KEY_PDESCRIPTION = "pDescription";
	public static final String KEY_PCATEGORY = "pCategory";
	public static final String KEY_PICON = "pIcon";
	public static final String KEY_PDISCOUNT = "pDiscount";
	public static final String KEY_PPROMOTION = "pPromotion";
	public static final String KEY_PDATE = "pDate";
	
	public static final String TABLE_CART = "cart";
	public static final String KEY_CID = "cid";
	public static final String KEY_CPID = "cpid";
	public static final String KEY_CQTY = "cQuantity";
	
	private String[] allColumns = new String[]{KEY_PID, KEY_PNAME, KEY_PTITLE, KEY_PPRICE, KEY_PDESCRIPTION, KEY_PCATEGORY, KEY_PICON};
	
	private static final String DATABASE_NAME = "EcommerceDatabase";

	private static final int DATABASE_VERSION = 1;
	
	private static DbHelper helper;
	private final Context context;
	private static SQLiteDatabase database;
	
	public EcommerceDatabase(Context cont){
		context = cont;
		helper = new DbHelper(context);
	}
	
	private static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
					KEY_PID + " INTEGER PRIMARY KEY, " +
					KEY_PNAME + " TEXT NOT NULL, " +
					KEY_PTITLE + " TEXT NOT NULL, " + 
					KEY_PPRICE + " TEXT NOT NULL, " +
					KEY_PDESCRIPTION + " TEXT NOT NULL, " +
					KEY_PCATEGORY + " TEXT NOT NULL, " +
					KEY_PICON + " TEXT NOT NULL, " +
					KEY_PPROMOTION + " TEXT NOT NULL, " +
					KEY_PDISCOUNT + " INTEGER NOT NULL, " +
					KEY_PDATE + " TEXT NOT NULL);"
			);
			
			db.execSQL("CREATE TABLE " + TABLE_CART + " (" +
					KEY_CID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_CPID + " INTEGER NOT NULL, " +
					KEY_CQTY + " INTEGER NOT NULL);"
			);
		
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
			onCreate(db);
		}
		
	}
	
	public EcommerceDatabase open() throws SQLException {
		database = helper.getWritableDatabase();
		return this;
	}
	
	public boolean isDatabaseOpened(){
		return database.isOpen();
	}
	
	public void close(){
		if(helper!=null) helper.close();
		if(database.isOpen()) database.close();
	}
	
	/**
	 * This will insert new row on the products table
	 * @param id
	 * @param name
	 * @param title
	 * @param price
	 * @param description
	 * @param category
	 * @param icon
	 * @return the row ID of the newly inserted row, or -1 if an error occurred 
	 */
	public long createRowOnProducts(int id, String name, String title, String price, String description, String category, String icon, String promotion, int discount, String date){
		open();
		ContentValues values = new ContentValues();
		values.put(KEY_PID, id);
		values.put(KEY_PNAME, name);
		values.put(KEY_PTITLE, title);
		values.put(KEY_PPRICE, price);
		values.put(KEY_PDESCRIPTION, description);
		values.put(KEY_PCATEGORY, category);
		values.put(KEY_PICON, icon);
		values.put(KEY_PPROMOTION, promotion);
		values.put(KEY_PDISCOUNT, discount);
		values.put(KEY_PDATE, date);
		
		long i = database.insert(TABLE_PRODUCTS, null, values);
		Log.i("ActivitySetup", "Added Row");
		close();
		return i;
	}
	
	/**
	 * This will create a new row into cart table
	 * @param pid represent the id from products
	 * @param qty represent the quantity
	 * @return the row ID of the newly inserted row, or -1 if an error occurred 
	 */
	public long createRowOnCart(int pid, int qty){
		open();
		ContentValues values = new ContentValues();
		values.put(KEY_CPID, pid);
		values.put(KEY_CQTY, qty);
		
		long i = database.insert(TABLE_CART, null, values);
		Log.i("ActivitySetup", "Added Row");
		close();
		return i;
	}
	
	/**
	 * This method will delete a table
	 * @param table represent the name of the table
	 */
	public void dropTable(String table){
		open();
		database.execSQL("DROP TABLE IF EXISTS " + table);
		close();
	}
	
	/**
	 * This method it will return the name of a product for an id
	 * @param i represent the contact id
	 * @return the name for that id
	 */
	public String getName(int i) {
		open();
		String[] columns = new String[]{KEY_PID, KEY_PNAME};
		Cursor cursor = database.query(TABLE_PRODUCTS, columns, KEY_PID + "=" + i, null, null, null, null);
		if(cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex(KEY_PNAME));
			return name;
		}
		cursor.close();
		close();
		return null;
	}
	
	/**
	 * This method it will return the title of a product for an id
	 * @param pid represent the contact id
	 * @return the title for that id
	 */
	public String getTitle(int pid) {
		open();
		String[] columns = new String[]{KEY_PID, KEY_PTITLE};
		Cursor cursor = database.query(TABLE_PRODUCTS, columns, KEY_PID + "=" + pid, null, null, null, null);
		if(cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex(KEY_PTITLE));
			return name;
		}
		cursor.close();
		close();
		return null;
	}
	
	/**
	 * This method it will return the description for a product for an id
	 * @param pid represent the contact id
	 * @return the description for that id
	 */
	public String getDescription(int pid) {
		open();
		String[] columns = new String[]{KEY_PID, KEY_PDESCRIPTION};
		Cursor cursor = database.query(TABLE_PRODUCTS, columns, KEY_PID + "=" + pid, null, null, null, null);
		if(cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex(KEY_PDESCRIPTION));
			return name;
		}
		cursor.close();
		close();
		return null;
	}
	
	/**
	 * This method it will return the icon path for a product for an id
	 * @param pid represent the contact id
	 * @return the description for that id
	 */
	public String getIcon(int pid) {
		open();
		String[] columns = new String[]{KEY_PID, KEY_PICON};
		Cursor cursor = database.query(TABLE_PRODUCTS, columns, KEY_PID + "=" + pid, null, null, null, null);
		if(cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex(KEY_PICON));
			return name;
		}
		cursor.close();
		close();
		return null;
	}
	
	/**
	 * This method it will return the price for a product for an id
	 * @param pid represent the contact id
	 * @return the price for that id
	 */
	public String getPrice(int i) {
		open();
		String[] columns = new String[]{KEY_PID, KEY_PPRICE};
		Cursor cursor = database.query(TABLE_PRODUCTS, columns, KEY_PID + "=" + i, null, null, null, null);
		if(cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex(KEY_PPRICE));
			return name;
		}
		cursor.close();
		close();
		return null;
	}
	
	/**
	 * This method it will return the category for a product for an id
	 * @param pid represent the contact id
	 * @return the category for that id
	 */
	public String getCategory(int pid) {
		open();
		String[] columns = new String[]{KEY_PID, KEY_PCATEGORY};
		Cursor cursor = database.query(TABLE_PRODUCTS, columns, KEY_PID + "=" + pid, null, null, null, null);
		if(cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex(KEY_PCATEGORY));
			return name;
		}
		cursor.close();
		close();
		return null;
	}
	
	public Cursor getFirstRows(){
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM products", null);
		Log.i(DEBUG, String.valueOf(cursor.getCount()));
		close();
		return cursor;
	}
	
	/**
	 * This method will show how many rows has our table
	 * @return the rows
	 */
	public long size(String table){
		open();
		SQLiteStatement query = database.compileStatement("SELECT COUNT(*) FROM " + table);
		long rows;
		try{
			rows = query.simpleQueryForLong();
		}catch(SQLiteDoneException e){
			rows = 0;
		}
		close();
		return rows;
	}
	
	/**
	 * This method checks if there is already an product in our database
	 * @param pid represent the id from mysql db of the product, which is distinct
	 * @return the id for that contact, if the contact doesn't exists will return -1
	 */
	public boolean hasIDProducts(int pid){
		int id = 0;
		open();
		try{
			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + KEY_PID + "= '" + pid + "'", null);
			if(cursor != null && cursor.getCount()!=0){
				cursor.moveToFirst();
				id = cursor.getInt(cursor.getColumnIndex(KEY_PID));
			}
			cursor.close();
			close();
			if(id!=0){
				return true;
			}else{
				return false;
			}
		}catch(SQLiteException e){
			Log.e(DEBUG, e.toString());
		}
		return false;
	}
	
	/**
	 * This method checks if there is already an product in our database
	 * @param pid represent the id from mysql db of the product, which is distinct
	 * @return the id for that contact, if the contact doesn't exists will return -1
	 */
	public boolean hasIDCart(int pid){
		int id = 0;
		open();
		try{
			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + KEY_CPID + "= '" + pid + "'", null);
			if(cursor != null && cursor.getCount()!=0){
				cursor.moveToFirst();
				id = cursor.getInt(cursor.getColumnIndex(KEY_CPID));
			}
			cursor.close();
			close();
			if(id!=0){
				return true;
			}else{
				return false;
			}
		}catch(SQLiteException e){
			Log.e(DEBUG, e.toString());
		}
		return false;
	}
	
	/**
	 * This method will query between the 2 tables
	 * @return  a cursor that will contain the rows
	 */
	public Cursor getJoinedTables(){
		open();
		Cursor cursor = null;
		try{
			cursor = database.rawQuery("SELECT products.pid, products.pTitle, products.pName, products.pPrice, products.pIcon, cart.cpid, cart.cQuantity FROM products, cart WHERE " +
				"products.pid = cart.cpid;", null);
			Log.i(DEBUG, String.valueOf(cursor.getCount()));
		close();
		} catch(SQLiteException e){
			Log.e(DEBUG, e.toString());
		}
		return cursor;
	}
	
	/**
	 * This method it will return the qty for a product for an id
	 * @param pid represent the contact id
	 * @return the qty for that id
	 */
	public int getQty(int i) {
		open();
		String[] columns = new String[]{KEY_CPID, KEY_CQTY};
		Cursor cursor = database.query(TABLE_CART, columns, KEY_CPID + "=" + i, null, null, null, null);
		if(cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();
			int qty = cursor.getInt(cursor.getColumnIndex(KEY_CQTY));
			return qty;
		}
		cursor.close();
		close();
		return 0;
	}
	
	/**
	 * This method will take all the rows from a table
	 * @param table name
	 * @return a cursor that will contain the rows
	 */
	public Cursor getAllTheRows(String table){
		open();
		Cursor cursor = null;
		try{
			cursor = database.rawQuery("SELECT * FROM " + table + ";", null);
			Log.i(DEBUG, String.valueOf(cursor.getCount()));
		close();
		} catch(SQLiteException e){
			Log.e(DEBUG, e.toString());
		}
		return cursor;
	}
	
	/**
	 * This method it will take only the promotional products, when there isn't internet connection
	 * @param table the table from whom will take the rows
	 * @return a cursor with all the rows
	 */
	public Cursor getPromotionalRows(String table){
		open();
		Cursor cursor = null;
		try{
			cursor = database.rawQuery("SELECT * FROM " + table + " WHERE " + KEY_PPROMOTION + "='true';", null);
			Log.i(DEBUG, String.valueOf(cursor.getCount()));
		close();
		} catch(SQLiteException e){
			Log.e(DEBUG, e.toString());
		}
		return cursor;
	}
	
	/**
	 * This method it will take only the non-promotional products, when there isn't internet connection
	 * @param table the table from whom will take the rows
	 * @return a cursor with all the rows
	 */
	public Cursor getProductsRows(String table){
		open();
		Cursor cursor = null;
		try{
			cursor = database.rawQuery("SELECT * FROM " + table + " WHERE " + KEY_PPROMOTION + "='false';", null);
			Log.i(DEBUG, String.valueOf(cursor.getCount()));
		close();
		} catch(SQLiteException e){
			Log.e(DEBUG, e.toString());
		}
		return cursor;
	}
	
	
	
	/**
	 * This method deletes only one row
	 * @param cpid is the id of the row
	 */
	public void deleteRow(String table, long cpid){
		open();
		database.delete(table, KEY_CPID + "=" + cpid,  null);
		close();
	}
	
	/**
	 * This method will update the a row from table
	 * @param cpid the KEY_CPID
	 * @param qty
	 */
	public boolean updateRow(int cpid, int qty){
		open();
		boolean boo;
		String strFilter = KEY_CPID + "=" + cpid;
		ContentValues args = new ContentValues();
		args.put(KEY_CQTY, qty);
		boo = database.update(TABLE_CART, args, strFilter, null) > 0;	
		Log.i(DEBUG, "The update row is " + boo);
		close();
		return boo;
	}
	
	/**
	 * 
	 */
	public void deleteAll(String table){
		if(size(table)>0){
			open();
			database.delete(table, null,  null);
		}
		close();
	}

}
