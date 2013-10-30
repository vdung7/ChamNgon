package model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLDataException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "database";

	private static final String DATABASE_PATH = "data/data/cyber.app.chamngon/databases/";
	private static final String DATABASE_NAME = "chamngon.sqlite";
	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase myDataBase;
	private final Context myContext;

	// bang cham ngon
	private static final String SAYING_TABLE_NAME = "ChamNgon";
	private static final String SID = "_chid";
	private static final String VIETNAMESE = "ndungviet";
	private static final String ENGLISH = "ndunganh";
	private static final String AUTHOR = "tacgia";
	private static final String CONTENT_ID = "cid";
	private static final String FAVOURITE = "yeuthich";

	// bang content
	private static final String CONTENT_TABLE_NAME = "Content";
	private static final String CID = "_cid";
	private static final String NAME = "ten";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void openDatabase() throws SQLDataException {
		myDataBase = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME,
				null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		Log.i("database", "==== Open database success ====");
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	// kiem tra xem da co database chua
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;

		try {
			String myPath = DATABASE_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database chua ton tai
		}

		if (checkDB != null)
			checkDB.close();
		System.out.println("check database thanh cong");
		return checkDB != null ? true : false;

	}

	// coppy data tu assets sang data
	private void copyDataBase() throws IOException {

		InputStream inputStream = myContext.getAssets().open(DATABASE_NAME);

		String outFileName = DATABASE_PATH + DATABASE_NAME;

		OutputStream outputStream = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}

		outputStream.flush();
		outputStream.close();
		inputStream.close();
		System.out.println("copy databse thanh cong");

	}
	
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase(); // kiem tra db

		if (dbExist) {
			// khong lam gi ca, database da co roi
		} else {
			this.getReadableDatabase();
			try {
				copyDataBase(); // chep du lieu
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	public ArrayList<Saying> getAllSaying() {
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		String sqlQuery = "SELECT * FROM " + SAYING_TABLE_NAME;
		Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
		ArrayList<Saying> listSayings = new ArrayList<Saying>();
		while (cursor.moveToNext()) {
			listSayings.add(new Saying(cursor.getInt(cursor
					.getColumnIndex(SID)), cursor.getString(cursor
					.getColumnIndex(VIETNAMESE)), cursor.getString(cursor
					.getColumnIndex(ENGLISH)), cursor.getString(cursor
					.getColumnIndex(AUTHOR)), cursor.getInt(cursor
					.getColumnIndex(CONTENT_ID)), cursor.getInt(cursor
					.getColumnIndex(FAVOURITE))));
		}
		return listSayings;
	}

	public Saying getSaying(int id) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		Cursor cursor = sqliteDatabase.rawQuery("select * from "
				+ SAYING_TABLE_NAME + " where " + SID + " = " + id, null);
		cursor.moveToNext();

		Saying result = new Saying();
		result.setId(cursor.getInt(cursor.getColumnIndex(CONTENT_ID)));
		result.setCid(cursor.getInt(cursor.getColumnIndex(CONTENT_ID)));
		result.setEnglish(cursor.getString(cursor.getColumnIndex(ENGLISH)));
		result.setVietnamese(cursor.getString(cursor.getColumnIndex(VIETNAMESE)));
		result.setAuthor(cursor.getString(cursor.getColumnIndex(AUTHOR)));
		result.setFavourite(cursor.getInt(cursor.getColumnIndex(FAVOURITE)));
		return result;
	}

	public ArrayList<Saying> getSaying(int fromId, int toId) {
		SQLiteDatabase sqLiteDatabase = getReadableDatabase();
		String sqlQuery = "SELECT * FROM " + SAYING_TABLE_NAME + " WHERE "
				+ SID + " BETWEEN " + fromId + " AND " + toId;
		Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
		ArrayList<Saying> listSayings = new ArrayList<Saying>();
		while (cursor.moveToNext()) {
			listSayings.add(new Saying(cursor.getInt(cursor
					.getColumnIndex(SID)), cursor.getString(cursor
					.getColumnIndex(VIETNAMESE)), cursor.getString(cursor
					.getColumnIndex(ENGLISH)), cursor.getString(cursor
					.getColumnIndex(AUTHOR)), cursor.getInt(cursor
					.getColumnIndex(CONTENT_ID)), cursor.getInt(cursor
					.getColumnIndex(FAVOURITE))));
		}
		return listSayings;
	}

	public ArrayList<Saying> getSayingByLength(int length) {
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		String sqlQuery = "SELECT * FROM " + SAYING_TABLE_NAME + " WHERE "
				+ " LENGTH(" + VIETNAMESE + ") <= " + length;
		Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
		ArrayList<Saying> listSayings = new ArrayList<Saying>();
		while (cursor.moveToNext()) {
			listSayings.add(new Saying(cursor.getInt(cursor
					.getColumnIndex(SID)), cursor.getString(cursor
					.getColumnIndex(VIETNAMESE)), cursor.getString(cursor
					.getColumnIndex(ENGLISH)), cursor.getString(cursor
					.getColumnIndex(AUTHOR)), cursor.getInt(cursor
					.getColumnIndex(CONTENT_ID)), cursor.getInt(cursor
					.getColumnIndex(FAVOURITE))));
		}
		return listSayings;
	}

	public ArrayList<Saying> getSayingByContent(int contentID) {
		SQLiteDatabase sd = getWritableDatabase();
		ArrayList<Saying> list = new ArrayList<Saying>();
		Cursor c = sd.rawQuery("select * from " + SAYING_TABLE_NAME + " where "
				+ CONTENT_ID + " = " + contentID, null);

		c.moveToLast();
		c.moveToNext();

		while (c.moveToPrevious()) {
			Saying result = new Saying();
			result.setId(c.getInt(c.getColumnIndex(SID)));
			result.setCid(c.getInt(c.getColumnIndex(CONTENT_ID)));
			result.setEnglish(c.getString(c.getColumnIndex(ENGLISH)));
			result.setVietnamese(c.getString(c.getColumnIndex(VIETNAMESE)));
			result.setAuthor(c.getString(c.getColumnIndex(AUTHOR)));
			result.setFavourite(c.getInt(c.getColumnIndex(FAVOURITE)));
			list.add(result);
		}
		System.out.println("danh dach cham ngon cua cid= " + contentID + " la "
				+ list.toString());
		return list;
	}

	public int getNumberChamNgonByContent(int cid) {
		SQLiteDatabase sd = getWritableDatabase();
		ArrayList<Saying> list = new ArrayList<Saying>();
		Cursor c = sd.rawQuery("select * from " + SAYING_TABLE_NAME + " where "
				+ CONTENT_ID + " = " + cid, null);

		c.moveToLast();
		c.moveToNext();

		while (c.moveToPrevious()) {
			Saying result = new Saying();
			result.setId(c.getInt(c.getColumnIndex(SID)));
			list.add(result);
		}
		return list.size();
	}

	public ArrayList<Saying> getFavoriteSayings() {
		SQLiteDatabase sd = getWritableDatabase();
		ArrayList<Saying> list = new ArrayList<Saying>();
		Cursor c = sd.rawQuery("select * from " + SAYING_TABLE_NAME + " where "
				+ FAVOURITE + " = " + 1, null);

		c.moveToLast();
		c.moveToNext();

		while (c.moveToPrevious()) {
			Saying result = new Saying();
			result.setId(c.getInt(c.getColumnIndex(SID)));
			result.setCid(c.getInt(c.getColumnIndex(CONTENT_ID)));
			result.setEnglish(c.getString(c.getColumnIndex(ENGLISH)));
			result.setVietnamese(c.getString(c.getColumnIndex(VIETNAMESE)));
			result.setAuthor(c.getString(c.getColumnIndex(AUTHOR)));
			result.setFavourite(c.getInt(c.getColumnIndex(FAVOURITE)));
			list.add(result);
		}
		System.out.println("danh dach cham ngon yeu thich" + " la "
				+ list.toString());
		return list;
	}

	public int getNumOfFavouriteSaying() {
		SQLiteDatabase sd = getWritableDatabase();
		ArrayList<Saying> list = new ArrayList<Saying>();
		Cursor c = sd.rawQuery("select * from " + SAYING_TABLE_NAME + " where "
				+ FAVOURITE + " = " + 1, null);

		c.moveToLast();
		c.moveToNext();

		while (c.moveToPrevious()) {
			Saying result = new Saying();
			result.setId(c.getInt(c.getColumnIndex(SID)));
			list.add(result);
		}
		return list.size();
	}

	public void updateSaying(Saying saying) {
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put(FAVOURITE, saying.getFavourite());
		sqLiteDatabase.update(SAYING_TABLE_NAME, contentValues, SID
				+ " = " + saying.getId(), null);
	}

	public void replaceSaying(Saying saying) {
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		String sqlQuery = "REPLACE INTO " + SAYING_TABLE_NAME + "(" + SID
				+ ", " + VIETNAMESE + ", " + ENGLISH + ", " + AUTHOR + ", "
				+ CONTENT_ID + ", " + FAVOURITE + ") VALUES (" + saying.getId()
				+ ", \"" + saying.getVietnamese() + "\", \"" + saying.getEnglish()
				+ "\", \"" + saying.getAuthor() + "\", \"" + saying.getCid()
				+ "\", \"" + saying.getFavourite() + "\")";
		sqLiteDatabase.execSQL(sqlQuery);
	}

	public Content getContent(int cid) {
		Log.i(TAG, "---- get content by id " + cid + " ----");
		SQLiteDatabase sd = getWritableDatabase();
		Cursor c = sd.rawQuery("select * from " + CONTENT_TABLE_NAME
				+ " where " + CID + " = " + cid, null);
		c.moveToNext();

		Content result = new Content();
		result.setId(c.getInt(c.getColumnIndex(CID)));
		result.setName(c.getString(c.getColumnIndex(NAME)));
		System.out.println("get content thanh cong " + result.toString());
		return result;
	}

	public ArrayList<Content> getAllContent() {
		Log.i(TAG, "---- get all content ----");
		SQLiteDatabase sd = getWritableDatabase();
		ArrayList<Content> list = new ArrayList<Content>();
		System.out.println("get all contennt");
		Cursor c = sd.rawQuery("select * from " + CONTENT_TABLE_NAME, null);
		System.out.println("thuc hien truy van thanh cong");
		while (c.moveToNext()) {
			Content result = new Content();
			result.setId(c.getInt(c.getColumnIndex(CID)));
			result.setName(c.getString(c.getColumnIndex(NAME)));

			list.add(result);
		}
		return list;
	}

	public int countSaying() {
		Log.i(TAG, "---- count saying ----");
		SQLiteDatabase sqLiteDatabase = getReadableDatabase();
		String sqlQuery = "SELECT COUNT(" + SID + ")" + " FROM "
				+ SAYING_TABLE_NAME;
		Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
		cursor.moveToNext();
		return cursor.getInt(0);
	}

	public ArrayList<String> getAllAuthor() {
		Log.i(TAG, "---- gat all author ----");
		SQLiteDatabase sqLiteDatabase = getReadableDatabase();
		String sqlQuery = "SELECT DISTINCT " + AUTHOR + " FROM "
				+ SAYING_TABLE_NAME + " ORDER BY " + AUTHOR;
		Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
		ArrayList<String> list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			list.add(cursor.getString(cursor.getColumnIndex(AUTHOR)));
		}
		return list;
	}

	public ArrayList<Saying> getSayingByAuthor(String author) {
		Log.i(TAG, "---- geting all saying by author " + author + " ----");
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		String sqlQuery = "SELECT * FROM " + SAYING_TABLE_NAME + " WHERE "
				+ AUTHOR + " LIKE " + author;
		Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
		ArrayList<Saying> listSayings = new ArrayList<Saying>();
		while (cursor.moveToNext()) {
			listSayings.add(new Saying(cursor.getInt(cursor
					.getColumnIndex(SID)), cursor.getString(cursor
					.getColumnIndex(VIETNAMESE)), cursor.getString(cursor
					.getColumnIndex(ENGLISH)), cursor.getString(cursor
					.getColumnIndex(AUTHOR)), cursor.getInt(cursor
					.getColumnIndex(CONTENT_ID)), cursor.getInt(cursor
					.getColumnIndex(FAVOURITE))));
		}
		return listSayings;
	}
}
