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

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SimpleWebServerActivity extends Activity implements OnCheckedChangeListener {
	private SimpleWebServer sws;
	private ToggleButton tb;
	private TextView status;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sws = new SimpleWebServer(this,8889);
        
        tb = (ToggleButton)findViewById(R.id.toggleButton1);
        tb.setOnCheckedChangeListener(this);
        
        status = (TextView)findViewById(R.id.status);
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if ( buttonView == tb ) {
			if ( isChecked ){
				if ( sws.StartServer() ) {
					status.setText("SimpleWebServer is running at "+sws.GetServerAddress()+":"+sws.GetServerPort());
				} else {
					status.setText("SimpleWebServer can't be started. Please check if any other application is using the same port");
					tb.setChecked(false);
				}
			} else {
				sws.StopServer();
				status.setText("SimpleWebServer is not running");
			}
		}
	}
}