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

import java.util.HashMap;
import java.util.Map;

public class WebServerUtility {
	public static final String Version = "1.0";
	
	public static String GetContentType(String uri){
		Map<String,String> typemap=new HashMap<String,String>();   
		typemap.put("bmp","image/bmp");
		typemap.put("jpg","image/jpeg");
		typemap.put("jpeg","image/jpeg");
		typemap.put("gif","image/gif");
		typemap.put("png","image/png");

		typemap.put("mp3","audio/mpeg");
		typemap.put("mp2","audio/mpeg");
		typemap.put("mid","audio/midi");
		typemap.put("midi","audio/midi");
		typemap.put("m3u","audio/x-mpegurl");
		typemap.put("wav","audio/x-wav");
		typemap.put("rm","audio/x-pn-realaudio");

		typemap.put("mpeg","video/mpeg");
		typemap.put("mpg","video/mpeg");
		typemap.put("mpe","video/mpeg");
		typemap.put("qt","video/quicktime");
		typemap.put("mov","video/quicktime");
		typemap.put("avi","video/x-msvideo");

		typemap.put("txt","text/plain");
		typemap.put("html","text/html");
		typemap.put("htm","text/html");
		typemap.put("xml","text/xml");
		typemap.put("xsl","text/xml");
		typemap.put("css","text/css");
		
		typemap.put("js","application/x-javascript");
		typemap.put("swf","application/x-shockwave-flash");
		typemap.put("form","application/x-www-form-urlencoded");
		typemap.put("pdf","application/pdf");
		typemap.put("doc","application/msword");
		typemap.put("xls","application/vnd.ms-excel");
		typemap.put("ppt", "application/vnd.ms-powerpoint");
		typemap.put("docx","application/msword");
		typemap.put("xlsx","application/vnd.ms-excel");
		typemap.put("pptx", "application/vnd.ms-powerpoint");

		if (uri.contains(".")){
			String[] sp = uri.split("//.");
			String ext = sp[sp.length-1];
			if ( typemap.containsKey(ext) ){
				return typemap.get(ext);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
