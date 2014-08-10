package com.weidongjian.com.selfdestructingmessage.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ServiceReceiver extends ResultReceiver {
	private Receiver receiver;

	public ServiceReceiver(Handler handler) {
		super(handler);
	}
	
	public void setReceiver(Receiver receiver) {
	      this.receiver = receiver;
	  }

	  // Defines our event interface for communication
	  public interface Receiver {
	      public void onReceiveResult(int resultCode, Bundle resultData);
	  }

	  // Delegate method which passes the result to the receiver if the receiver has been assigned
	  @Override
	  protected void onReceiveResult(int resultCode, Bundle resultData) {
	      if (receiver != null) {
	        receiver.onReceiveResult(resultCode, resultData);
	      }
	  }

}
