/**********************************************************************
 * Title: Solar Compass
 * Description: Android app for finding directions using the sun
 * Author: Agnibho Mondal
 * Website: http://code.agnibho.com/solarcompass
 **********************************************************************
 Copyright (c) 2016 Agnibho Mondal
 All rights reserved
 **********************************************************************
 This file is part of Solar Compass.

 Solar Compass is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Solar Compass is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Solar Compass.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************/

package com.agnibho.android.solarcompass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends AppCompatActivity {
    LocationData location= LocationData.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Latitude
         */
        final TextView latText=(TextView)findViewById(R.id.editText);
        if(location.isAvailable()) {
            latText.setText(Double.toString(location.getLatitude()));
        }
        /**
         * Longitude
         */
        final TextView lonText=(TextView)findViewById(R.id.editText2);
        if(location.isAvailable()) {
            lonText.setText(Double.toString(location.getLongitude()));
        }
        /**
         * Button
         */
        Button setBtn=(Button)findViewById(R.id.button);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lt = Double.parseDouble(latText.getText().toString());
                double ln = Double.parseDouble(lonText.getText().toString());
                 if (lt >= -90 && lt <= 90){
                     if (ln >= -180 && ln <= 180){
                         location.setCoordinate(lt, ln);
                         finish();
                     }
                     else{
                         Toast.makeText(getApplicationContext(), "Invalid longitude", Toast.LENGTH_LONG).show();
                         lonText.setText("");
                         lonText.requestFocus();
                     }
                } else {
                     Toast.makeText(getApplicationContext(), "Invalid longitude", Toast.LENGTH_LONG).show();
                     latText.setText("");
                     latText.requestFocus();
                }
            }
        });
    }
}
