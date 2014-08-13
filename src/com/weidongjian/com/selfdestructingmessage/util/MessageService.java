package com.weidongjian.com.selfdestructingmessage.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;
import com.weidongjian.com.selfdestructingmessage.models.Message;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ResultReceiver;

public class MessageService extends IntentService {
	private DatabaseHandler databaseHandler;
	private ResultReceiver rec;

	public MessageService() {
		super("MessageService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
//		System.out.println("service had started");
		databaseHandler = new DatabaseHandler(getBaseContext());
		databaseHandler.open();
		String type = intent.getStringExtra("type");
		if (type.equals("delete")) {
			// System.out.println("delete service");
			rec = intent.getParcelableExtra("receiver");
			long id = intent.getLongExtra("_id", -1);
			String objectID = intent.getStringExtra("objectId");
			databaseHandler.deleteMessage(id);
			deleteParseMessage(objectID);
			rec.send(Activity.RESULT_OK, null);
		} else if (type.equals("retrieveMessage")) {
//			System.out.println("get the ResultReceiver");
			rec = intent.getParcelableExtra("receiver");
			retrieveMessage();
			// System.out.println("send retrieveMessage result");
		}
	}

	private void deleteParseMessage(String id) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstant.CLASS_MESSAGE);
		query.whereEqualTo(ParseConstant.KEY_OBJECT_ID, id);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> arg0, ParseException e) {
				if (e == null) {
					if (!arg0.isEmpty()) {
						ParseObject delMessage = arg0.get(0);
						deleteMessage(delMessage);
					}
				}
			}
		});
	}

	private void deleteMessage(ParseObject message) {

		List<String> ids = message.getList(ParseConstant.KEY_RECEIVE_ID);
		if (ids.size() == 1) {
			message.deleteInBackground();
		} else {
			ArrayList<String> idToRemove = new ArrayList<String>();
			idToRemove.add(ParseUser.getCurrentUser().getObjectId());
			message.removeAll(ParseConstant.KEY_RECEIVE_ID, idToRemove);
			message.saveInBackground();
		}
	}

	private void retrieveMessage() {
		long refreshDate = getLastUpdatedDate();
		System.out.println("last requery  time: " + new Date(refreshDate));
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstant.CLASS_MESSAGE);
		query.whereEqualTo(ParseConstant.KEY_RECEIVE_ID, ParseUser
				.getCurrentUser().getObjectId());
		query.whereGreaterThan("createdAt", new Date(refreshDate));
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				if (arg1 == null) {
//					System.out.println("parseQuery done with no error");
					if (!arg0.isEmpty()) {
						System.out.println("parseQuery done with result:"
								+ arg0.size());
						addMessageToDatabase(arg0);
					}else {
						rec.send(Activity.RESULT_OK, null);
					}
				} else {
					rec.send(Activity.RESULT_OK, null);
					System.out.println("parseQuery done with error"
							+ arg1.getMessage());
				}
			}
		});
	}

	private long getLastUpdatedDate() {
		Cursor cursor = databaseHandler.getAllMessage();
		if (!cursor.moveToFirst()) {
			return 0;
		} else {
			cursor.moveToFirst();
			int index = cursor.getColumnIndex("createdAt");
			long createdAt = cursor.getLong(index);
			cursor.close();
			return createdAt;
		}
	}

	private void addMessageToDatabase(List<ParseObject> mMessage) {
		// System.out.println("parseQuery done with result" + mMessage.size());
		for (ParseObject message : mMessage) {
			String objectId = message.getObjectId();
			ParseFile file = message.getParseFile(ParseConstant.KEY_FILE);
			Uri uri = Uri.parse(file.getUrl());
			String fileType = message.getString(ParseConstant.KEY_FILE_TYPE);
			String senderName = message
					.getString(ParseConstant.KEY_SENDER_NAME);
			long createdAt = message.getCreatedAt().getTime();
			Message newMessage = new Message(objectId, uri, fileType,
					senderName, createdAt);
//			databaseHandler.open();
			databaseHandler.addMessage(newMessage);
			rec.send(Activity.RESULT_OK, null);
//			databaseHandler.close();
		}
	}

}
