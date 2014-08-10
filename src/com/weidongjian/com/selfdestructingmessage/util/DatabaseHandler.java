package com.weidongjian.com.selfdestructingmessage.util;

import java.text.SimpleDateFormat;

import com.weidongjian.com.selfdestructingmessage.models.Message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

public class DatabaseHandler {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "DB_Messages";
	private static final String TABLE_NAME = "Messages";
	private SQLiteDatabase database;
	private DatabaseOpenHelper databaseOpenHelper;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private static final String KEY_ID = "_id";
	private static final String KEY_OBJECTID = "objectId";
	private static final String KEY_FILEURI = "fileUri";
	private static final String KEY_FILETYPE = "fileType";
	private static final String KEY_SENDERNAME = "senderName";
	private static final String KEY_CREATEDAT = "createdAt";

	public DatabaseHandler(Context context) {
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void open() {
		database = databaseOpenHelper.getWritableDatabase();
	}
	
	public void close() {
		if (database != null) {
			database.close();
		}
	}
	
	public void addMessage(Message message) {
		ContentValues newMessage = new ContentValues();
		newMessage.put(KEY_OBJECTID, message.getObjectId());
		newMessage.put(KEY_FILEURI, message.getFileUri().toString());
		newMessage.put(KEY_FILETYPE, message.getFileType());
		newMessage.put(KEY_SENDERNAME, message.getSenderName());
		String date = df.format(message.getCreatedAt());
		newMessage.put(KEY_CREATEDAT, date);
		open();
		database.insert(TABLE_NAME, null, newMessage);
		close();
	}
	
	public void updateMessage(Message message) {
		ContentValues editMessage = new ContentValues();
		long id = message.getId();
		editMessage.put(KEY_OBJECTID, message.getObjectId());
		editMessage.put(KEY_FILEURI, message.getFileUri().toString());
		editMessage.put(KEY_FILETYPE, message.getFileType());
		editMessage.put(KEY_SENDERNAME, message.getSenderName());
		String date = df.format(message.getCreatedAt());
		editMessage.put(KEY_CREATEDAT, date);
		open();
		database.update(TABLE_NAME, editMessage, "_id=" + id, null);
		close();
	}
	
	public Cursor getAllMessage() {
		return database.query(TABLE_NAME, null, null, null, null, null, KEY_CREATEDAT);
	}
	
	public Cursor getOneMessage(long id) {
		return database.query(TABLE_NAME, null, "_id=" + id, null, null, null, null);
	}
	
	public void deleteMessage(long id) {
		open();
		database.delete(TABLE_NAME, "_id=" + id, null);
	}

	class DatabaseOpenHelper extends SQLiteOpenHelper {

		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_OBJECTID + " TEXT," + KEY_FILEURI + " TEXT,"
					+ KEY_FILETYPE + " TEXT," + KEY_SENDERNAME + " TEXT,"
					+ KEY_CREATEDAT + " TEXT" + ")";
			db.execSQL(CREATE_MESSAGE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
		
	}
}
