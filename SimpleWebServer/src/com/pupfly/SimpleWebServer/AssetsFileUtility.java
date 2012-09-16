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
import java.io.InputStream;
import android.content.res.AssetManager;
import android.text.TextUtils.SimpleStringSplitter;

public class AssetsFileUtility {
	
	public static String RemoveLastPathSeperator(String uri){
		if (uri.endsWith("/")){
			return uri.substring(0, uri.length()-1);
		} else {
			return uri;
		}
	}
	
	public static  boolean IsFileExist(AssetManager am, String uri){
		String parentPath = GetParentPath(uri);
		String filename = GetFilename(uri);
		String[] files = List(am,parentPath);
		for(String s:files){
			if (s.equals(filename)){
				return true;
			}
		}
		return false;
	}
	
	public static String[] List(AssetManager am, String uri){
		uri = RemoveLastPathSeperator(uri);
		if ( IsFolder(am, uri) ){
			try {
				return am.list(uri);
			} catch (IOException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static boolean IsFolder(AssetManager am, String uri){
		uri = RemoveLastPathSeperator(uri);
		try {
	 		
			InputStream is = am.open(uri);
			if ( am.open(uri) == null ){
				return true;
			} else {
				is.close();
				is = null;
				return false;
			}
		} catch (IOException e) {
			return true;
		}
	}
	
	public static String GetFilename(String uri){
		if ( uri.equalsIgnoreCase("/") ){
			return "/";
		}
		if ( uri.endsWith("/") ){
			uri = uri.substring(0, uri.length()-1);
		}
		if ( uri.indexOf('/') < 0 ){
			return uri;
		}

		SimpleStringSplitter sss = new SimpleStringSplitter('/');
		sss.setString(uri);
		String tmp = "";
		String ret = "";
		while(sss.hasNext()){
			tmp = sss.next();
			if ( tmp.trim().length()>0 ){
				ret = tmp.trim();
			}
		}
		return ret;
	}
	
	public static String GetParentPath(String uri){
		if ( uri.equalsIgnoreCase("/") ){
			return "";
		}
		if ( uri.endsWith("/") ){
			uri = uri.substring(0, uri.length()-1);
		}
		if ( uri.indexOf('/') < 0 ){
			return "";
		}

		SimpleStringSplitter sss = new SimpleStringSplitter('/');
		sss.setString(uri);
		String tmp = "";
		String ret = "";
		while(sss.hasNext()){
			tmp = sss.next();
			if ( sss.hasNext() && tmp.trim().length()>0 ){
				ret += tmp.trim() + "/";
			}
		}
		if ( ret.endsWith("/") ){
			ret = ret.substring(0, ret.length()-1);
		}
		return ret;
	}
}
