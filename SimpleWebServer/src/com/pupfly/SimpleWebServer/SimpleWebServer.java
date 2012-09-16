 /*
  * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
  *
  * Copyright 2012, Chun Xu, zuojisi@gmail.com
  *
  * This file is part of SimpleWebServer.
  * 
  * SimpleWebServer is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Lesser General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * SimpleWebServer is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with SimpleWebServer.  If not, see <http://www.gnu.org/licenses/>.
  *
  * - Author: Chun Xu
  * - Contact: zuojisi@gmail.com
  * - License: GNU Lesser General Public License (LGPL)
  * - Blog and source code availability: http://sws.pupfly.com/
  */

package com.pupfly.SimpleWebServer;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class SimpleWebServer {
	private String TAG;
	
	private boolean mRunning;
	private int mServerPort;
	private BasicHttpProcessor mHttpProc;
	private BasicHttpContext mHttpContext;
	private HttpService mHttpService;
	private ServerSocket mHostSocket;
	private ServerTask mSt;
	private Context mCtx;
	private WifiLock mWifiLock;

	public SimpleWebServer(Context ctx) {
		mCtx = ctx;
		mServerPort = -1;
		CommonCreate();
	}
	
	public SimpleWebServer(Context ctx,int port) {

		mServerPort = port;
		mCtx = ctx;
		CommonCreate();
	}
	
	private void CommonCreate(){
		TAG = this.getClass().getSimpleName();
        WifiManager wm = (WifiManager) mCtx.getSystemService(Context.WIFI_SERVICE);
        mWifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, getClass().getPackage().getName());
		
        mHttpProc = new BasicHttpProcessor();
		mHttpContext = new BasicHttpContext();
		mHttpProc.addInterceptor(new ResponseDate());
		mHttpProc.addInterceptor(new ResponseServer());
		mHttpProc.addInterceptor(new ResponseContent());
		mHttpProc.addInterceptor(new ResponseConnControl());
		mHttpService = new HttpService(mHttpProc,new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
		HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
		registry.register("*", new SimpleRequestHandler(mCtx));
		mHttpService.setHandlerResolver(registry);
	}

	private class ServerTask extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (mRunning) {
				try {
					Socket sock = mHostSocket.accept();
					if ( ! mHostSocket.isClosed() ){
						DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();
						serverConnection.bind(sock, new BasicHttpParams());
						mHttpService.handleRequest(serverConnection, mHttpContext);
						serverConnection.shutdown();
					} else {
						Log.i(TAG,"package received after socket is closed. ignore");
					}
				} catch (IOException e) {
					if(mRunning == true){
						e.printStackTrace();
					}
				} catch (HttpException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int GetServerPort(){
		return mServerPort;
	}
	
	public String GetServerAddress(){
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
			    NetworkInterface intf = en.nextElement(); 
			    String name = intf.getDisplayName();
			    if (name.contains("eth") || name.contains("wlan") ){
			    	return intf.getInetAddresses().nextElement().getHostAddress();
			    }
			}
			return "";
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	public synchronized boolean StartServer() {
		if ( mRunning == true ){
			Log.e(TAG,"Server is already running, do nothing");
			return true;
		} else {
			try {
				if ( mServerPort > 0 ){
					mHostSocket = new ServerSocket(mServerPort);
				} else {
					mHostSocket = new ServerSocket(0);
					mServerPort = mHostSocket.getLocalPort();
				}
				mHostSocket.setReuseAddress(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,"port:"+mServerPort+" is not openned or set correctly. Can't start server");
				return false;
			}
			Log.i(TAG,"port:"+mServerPort+" openned successfully");
			Log.i(TAG,"Server started");
			mRunning = true;
			mWifiLock.acquire();
			mSt = new ServerTask();
			mSt.start();
			return true;
		}
	}

	public synchronized void StopServer() {
		if ( mRunning == false ){
			Log.w(TAG,"Server is not running, no need to stop, do nothing");
		} else {
			mRunning = false;
			mWifiLock.release();
			if (mHostSocket != null) {
				try {
					mHostSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.i(TAG,"Server is stopped");
		}
	}
}
