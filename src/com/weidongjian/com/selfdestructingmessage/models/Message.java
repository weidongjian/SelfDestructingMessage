package com.weidongjian.com.selfdestructingmessage.models;

import java.util.Date;

import android.net.Uri;

public class Message {
	private long _id;
	private String objectId;
	private Uri fileUri;
	private String fileType;
	private String senderName;
	private long createdAt;

	public Message() {
	}

	public Message(String objectId, Uri fileUri, String fileType,
			String senderName, long createdAt) {
		this.objectId = objectId;
		this.fileUri = fileUri;
		this.fileType = fileType;
		this.senderName = senderName;
		this.createdAt = createdAt;
	}
	
	public String getObjectId() {
		return objectId;
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the _id
	 */
	public long getId() {
		return _id;
	}

	/**
	 * @param _id
	 *            the _id to set
	 */
	public void setId(long _id) {
		this._id = _id;
	}

	/**
	 * @return the fileUri
	 */
	public Uri getFileUri() {
		return fileUri;
	}

	/**
	 * @param fileUri
	 *            the fileUri to set
	 */
	public void setFileUri(Uri fileUri) {
		this.fileUri = fileUri;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the senderName
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * @param senderName
	 *            the senderName to set
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * @return the createdAt
	 */
	public long getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt
	 *            the createdAt to set
	 */
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

}
