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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class SimpleRequestHandler implements HttpRequestHandler, ContentProducer{
	private static final String copyright = "<p>SimpleWebServer Version "+WebServerUtility.Version+" Copyright&copy;2012 <a href=\"http://sws.pupfly.com\">http://sws.pupfly.com</a></p>";
	private static final String versionpage = "<html><head><title>Version</title></head><body>"+copyright+"</body></html>";
	private static final String notfoundpage = "<html><head><title>SimpleWebServer</title></head><body><br><p><b>Error:</b> Requested resource is not found</p><hr />"+copyright+"</body></html>";
	private static final String htmlcontent = "text/html";
	private static final String TAG = "SimpleRequestHandler";
	private static final int BUFFER_SIZE = 20 * 1024;
	private AssetManager mAssetManager;
	private byte[] mBuffer;
	private InputStream mIs;
	ByteArrayOutputStream mAos;


	
	public SimpleRequestHandler(Context ctx) {
		super();

		mAssetManager = ctx.getAssets();
		mBuffer = new byte[BUFFER_SIZE];
		mIs = null;
		mAos = new ByteArrayOutputStream(BUFFER_SIZE);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		// TODO Auto-generated method stub
		String uri = request.getRequestLine().getUri();
		//Log.i(TAG,"Handle request:"+request.getRequestLine());
		BuildResponse(uri,response);
	}
	
	private void BuildResponse(String uri,HttpResponse response){

		String ctype = null;
		mAos.reset();

		/* remove GET params if any */
		int tt = uri.indexOf('?');
		if ( tt > 0 ){
			uri = uri.substring(0, tt);
		}
		
		try {
			if ( uri.equalsIgnoreCase("/server.version.api") ){
				mAos.write(versionpage.getBytes());
				ctype = htmlcontent;
				mIs = null;
			} else {	
				String origuri = uri;
				uri="wwwroot"+uri;
				Log.i(TAG,"uri is "+ uri);
				if (uri.endsWith("/")){
					uri = AssetsFileUtility.RemoveLastPathSeperator(uri);
				}
	
				ctype = WebServerUtility.GetContentType(uri);
		
				if ( AssetsFileUtility.IsFileExist(mAssetManager, uri)){
					if (! AssetsFileUtility.IsFolder(mAssetManager, uri) ){
						mIs = mAssetManager.open(uri);
					} else {
						ctype = htmlcontent;
						if ( AssetsFileUtility.IsFileExist(mAssetManager, uri + "/index.html" ) ){
							mIs = mAssetManager.open(uri+"/index.html");
						} else if ( AssetsFileUtility.IsFileExist(mAssetManager, uri + "/index.htm" ) ) {
							mIs = mAssetManager.open(uri+"/index.htm");
						} else {
							/* list folder */
							mAos.write("<br><b>Directory of ".getBytes());
							mAos.write(origuri.getBytes());
							mAos.write("</b><br><hr /><table>".getBytes());
						
							String[] files = AssetsFileUtility.List(mAssetManager, uri);
						
							if (!origuri.endsWith("/")){
								origuri+="/";
							}
							if (! origuri.equalsIgnoreCase("/")){
								mAos.write("<tr><td>[</td><td>Folder</td><td>]</td><td><a href=\"../\">..</a></td></tr>".getBytes());
							}

							for ( String fn : files ){
								mAos.write("<tr><td>[</td><td>".getBytes());
								if ( AssetsFileUtility.IsFolder(mAssetManager, uri+"/"+fn) ){
									mAos.write("Folder".getBytes());
								} else {
									mAos.write("File".getBytes());
								}
								mAos.write("</td><td>]</td><td><a href=\"".getBytes());
								mAos.write(origuri.getBytes());
								mAos.write(fn.getBytes());
								mAos.write("\">".getBytes());
								mAos.write(fn.getBytes());
								mAos.write("</a></td></tr>".getBytes());
							}
							mAos.write("</table><hr />".getBytes());
							mAos.write(copyright.getBytes());
							mAos.flush();
							ctype = htmlcontent;
							mIs = null;
						}
					}
					
				} else {
					mAos.write(notfoundpage.getBytes());
					ctype = htmlcontent;
					mIs = null;				
				}
			}
		} catch (IOException e2){
			e2.printStackTrace();
		}
		if ( ctype != null ){
			response.setHeader("Content-Type", ctype);
		}
		HttpEntity entity = new EntityTemplate(this);
		response.setEntity(entity);
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		// TODO Auto-generated method stub
		if ( mIs != null ){
			int len=0;
			while( (len = mIs.read(mBuffer)) >= 0 ) {
				outstream.write(mBuffer, 0, len);
			}
			mIs.close();
			mIs=null;
		} else {
			outstream.write(mAos.toByteArray());
		}
		outstream.flush();
	}
}