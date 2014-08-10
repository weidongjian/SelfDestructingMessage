package com.weidongjian.com.selfdestructingmessage.util;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.weidongjian.com.selfdestructingmessage.ParseConstant;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

public class DeleteMessageService extends IntentService {
	private DatabaseHandler databaseHandler;

	public DeleteMessageService() {
		super("deleteMessageService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		databaseHandler = new DatabaseHandler(getBaseContext());
		databaseHandler.open();
		ResultReceiver rec = intent.getParcelableExtra("receiver");
		long id = intent.getLongExtra("_id", -1);
		String objectID = intent.getStringExtra("objectId");
		databaseHandler.deleteMessage(id);
		databaseHandler.close();
		deleteParseMessage(objectID);
		rec.send(Activity.RESULT_OK, null);
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

}
