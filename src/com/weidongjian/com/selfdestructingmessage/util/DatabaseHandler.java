package com.weidongjian.com.selfdestructingmessage.util;

import java.text.SimpleDateFormat;

import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.models.Message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

public class DatabaseHandler {
	private static int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "DB_Messages";
	private static final String TABLE_NAME = "Messages";
	private String USERNAME = ParseUser.getCurrentUser().getUsername();
	private SQLiteDatabase database;
	private DatabaseOpenHelper databaseOpenHelper;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private static final String KEY_ID = "_id";
	private static final String KEY_USERNAME = "userName";
	private static final String KEY_OBJECTID = "objectId";
	private static final String KEY_FILEURI = "fileUri";
	private static final String KEY_FILETYPE = "fileType";
	private static final String KEY_SENDERNAME = "senderName";
	private static final String KEY_CREATEDAT = "createdAt";

	public DatabaseHandler(Context context) {
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME,
				null, DATABASE_VERSION);
	}

	public void open() {
		database = databaseOpenHelper.getWritableDatabase();
	}

	// private boolean isTableExists(String tableName, boolean openDb) {
	// if (openDb) {
	// if (database == null || !database.isOpen()) {
	// database = databaseOpenHelper.getReadableDatabase();
	// }
	//
	// if (!database.isReadOnly()) {
	// database.close();
	// database = databaseOpenHelper.getReadableDatabase();
	// }
	// }
	//
	// Cursor cursor = database.rawQuery(
	// "select DISTINCT tbl_name from sqlite_master where tbl_name = '"
	// + tableName + "'", null);
	// if (cursor != null) {
	// if (cursor.getCount() > 0) {
	// cursor.close();
	// return true;
	// }
	// cursor.close();
	// }
	// return false;
	// }

	public void close() {
		if (database != null) {
			database.close();
		}
	}

	public void addMessage(Message message) {
//		System.out.println("start addMessageToDatabase" + message);
		ContentValues newMessage = new ContentValues();
		newMessage.put(KEY_USERNAME, USERNAME);
		newMessage.put(KEY_OBJECTID, message.getObjectId());
		newMessage.put(KEY_FILEURI, message.getFileUri().toString());
		newMessage.put(KEY_FILETYPE, message.getFileType());
		newMessage.put(KEY_SENDERNAME, message.getSenderName());
		long date = message.getCreatedAt();
		newMessage.put(KEY_CREATEDAT, date);
		long resulte = database.insert(TABLE_NAME, null, newMessage);
		if (resulte == -1)
			System.out.println("Error insert new message");
		else {
			System.out.println("insert new message in row:" + resulte);
		}
	}

	public void updateMessage(Message message) {
		ContentValues editMessage = new ContentValues();
		long id = message.getId();
		editMessage.put(KEY_USERNAME, USERNAME);
		editMessage.put(KEY_OBJECTID, message.getObjectId());
		editMessage.put(KEY_FILEURI, message.getFileUri().toString());
		editMessage.put(KEY_FILETYPE, message.getFileType());
		editMessage.put(KEY_SENDERNAME, message.getSenderName());
		long date = message.getCreatedAt();
		editMessage.put(KEY_CREATEDAT, date);
		database.update(TABLE_NAME, editMessage, "_id=" + id, null);
	}

	public Cursor getAllMessage() {
		System.out.println("current userName is:" + USERNAME);
		return database.query(TABLE_NAME, null, KEY_USERNAME + "=?",
				new String[] { USERNAME }, null, null, KEY_CREATEDAT + " DESC");
	}

	public Cursor getOneMessage(long id) {
		return database.query(TABLE_NAME, null, "_id=" + id, null, null, null,
				null);
	}

	public void deleteMessage(long id) {
		database.delete(TABLE_NAME, "_id=" + id, null);
	}

	public void deleteAllMessae() {
		database.delete(TABLE_NAME, KEY_SENDERNAME + "=?",
				new String[] { USERNAME });
	}

	class DatabaseOpenHelper extends SQLiteOpenHelper {

		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			System.out.println("SQLiteDatabase onCreate");
			String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS "
					+ TABLE_NAME + "(" + KEY_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_USERNAME
					+ " TEXT," + KEY_OBJECTID + " TEXT," + KEY_FILEURI
					+ " TEXT," + KEY_FILETYPE + " TEXT," + KEY_SENDERNAME
					+ " TEXT," + KEY_CREATEDAT + " INTEGER" + ")";

			db.execSQL(CREATE_MESSAGE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}

	}
}
