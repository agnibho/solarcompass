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

import java.util.Calendar;
import java.util.TimeZone;

public class CompassLogic {

    /*
    Find Compass Angle
    Unit - Degrees
    */
    public static double getAngle(){
        return Math.toDegrees(getAzimuthAngle());
    }

    /*
    Declination Angle
    Unit - Degrees
    */
    private static double getDeclinationAngle(){
        int dayNum= Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return 23.45*Math.sin(Math.toRadians(360.0/365.0*(284.0+dayNum)));
    }

    /*
    Hour Angle
    Unit - Degrees
     */
    private static double getHourAngle(){
        Calendar cal=Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        double utc=(double)cal.get(Calendar.HOUR_OF_DAY)+(double)cal.get(Calendar.MINUTE)/60;
        double timeDiff=LocationData.getInstance().getLongitude()/15;
        double hour=(utc+timeDiff)%24.0;
        return (12-hour)*15;
    }

    /*
    Altitude Angle
    Unit - Radians
     */
    private static double getAltitudeAngle(){
        double latitude=Math.toRadians(LocationData.getInstance().getLatitude());
        double declination=Math.toRadians(getDeclinationAngle());
        double hourAngle=Math.toRadians(getHourAngle());
        double sinAlt=Math.cos(latitude)*Math.cos(declination)*Math.cos(hourAngle)+Math.sin(latitude)*Math.sin(declination);
        return Math.asin(sinAlt);
    }

    /*
    Azimuth Angle
    Unit - Radians
     */
    private static double getAzimuthAngle(){
        double latitude=Math.toRadians(LocationData.getInstance().getLatitude());
        double declination=Math.toRadians(getDeclinationAngle());
        double altitude=getAltitudeAngle();
        double cosAz=(Math.sin(altitude)*Math.sin(latitude)-Math.sin(declination))/(Math.cos(altitude)*Math.cos(latitude));
        double azimuth=Math.acos(cosAz);
        if(getHourAngle()<0){
            if(azimuth>0){
                azimuth=-azimuth;
            }
        }
        else{
            if(azimuth<0){
                azimuth=-azimuth;
            }
        }
        return azimuth;
    }
}
