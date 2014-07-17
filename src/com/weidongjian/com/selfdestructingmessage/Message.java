package com.weidongjian.com.selfdestructingmessage;

public class Message {
	int _id;
	String _sender;
	String _date;
	String _fileType;
	String _uri;
	
	//Empty constructor
	public Message() {
	
	}
	
	//constructor
	public Message(int id, String sender, String date, String fileType, String uri){
		_id = id;
		_sender = sender;
		_date = date;
		_fileType = fileType;
		_uri = uri;
	}
	
	//constructor
	public Message(String sender, String date, String fileType, String uri){
		_sender = sender;
		_date = date;
		_fileType = fileType;
		_uri = uri;
	}

	public int getID() {
		return _id;
	}

	public void setID(int id) {
		this._id = id;
	}

	public String getSender() {
		return _sender;
	}

	public void setSender(String sender) {
		this._sender = sender;
	}

	public String getDate() {
		return _date;
	}

	public void setDate(String date) {
		this._date = _date;
	}

	public String getFileType() {
		return _fileType;
	}

	public void setFileType(String fileType) {
		this._fileType = fileType;
	}

	public String getUri() {
		return _uri;
	}

	public void setUri(String uri) {
		this._uri = uri;
	}
	
	
}
