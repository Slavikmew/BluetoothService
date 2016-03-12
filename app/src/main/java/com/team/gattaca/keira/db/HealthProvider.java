/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.team.gattaca.keira.db;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class HealthProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private HealthDbHelper mOpenHelper;

    static final int BODY_TEMPERATURE = 101;
    static final int PULSE = 102;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
//        sWeatherByLocationSettingQueryBuilder.setTables(
//                HealthContract.BodyTemperatureEntry.TABLE_NAME + " INNER JOIN " +
//                        HealthContract.LocationEntry.TABLE_NAME +
//                        " ON " + HealthContract.BodyTemperatureEntry.TABLE_NAME +
//                        "." + HealthContract.BodyTemperatureEntry.COLUMN_LOC_KEY +
//                        " = " + HealthContract.LocationEntry.TABLE_NAME +
//                        "." + HealthContract.LocationEntry._ID);
    }

    //location.location_setting = ?
//    private static final String sLocationSettingSelection =
//            HealthContract.LocationEntry.TABLE_NAME +
//                    "." + HealthContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";
//
//    //location.location_setting = ? AND date >= ?
//    private static final String sLocationSettingWithStartDateSelection =
//            HealthContract.LocationEntry.TABLE_NAME +
//                    "." + HealthContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    HealthContract.BodyTemperatureEntry.COLUMN_DATE + " >= ? ";
//
//    //location.location_setting = ? AND date = ?
//    private static final String sLocationSettingAndDaySelection =
//            HealthContract.LocationEntry.TABLE_NAME +
//                    "." + HealthContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    HealthContract.BodyTemperatureEntry.COLUMN_DATE + " = ? ";

    private static final String PulsebyDateSelection =
            HealthContract.PulseEntry.TABLE_NAME + "." + HealthContract.PulseEntry.COLUMN_DATE + " = ?";





    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the BODY_TEMPERATURE, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and PULSE integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // HealthContract to help define the types to the UriMatcher.


        // 3) Return the new matcher!
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HealthContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, HealthContract.PATH_BODY_TEMPERATURE, BODY_TEMPERATURE);
        matcher.addURI(authority, HealthContract.PATH_PULSE, PULSE);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new HealthDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new HealthDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            case WEATHER_WITH_LOCATION:
            case BODY_TEMPERATURE:
                return HealthContract.BodyTemperatureEntry.CONTENT_TYPE;
            case PULSE:
                return HealthContract.PulseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"

            // "weather"
            case BODY_TEMPERATURE: {
                retCursor = mOpenHelper.getReadableDatabase().query(HealthContract.BodyTemperatureEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case PULSE: {
                retCursor = mOpenHelper.getReadableDatabase().query(HealthContract.PulseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case BODY_TEMPERATURE: {
                normalizeDate(values);
                long _id = db.insert(HealthContract.BodyTemperatureEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = HealthContract.BodyTemperatureEntry.buildBodyTempUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PULSE: {
                normalizeDate(values);
                long _id = db.insert(HealthContract.PulseEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = HealthContract.PulseEntry.buildPulseUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database

        // Student: Use the uriMatcher to match the BODY_TEMPERATURE and PULSE URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        // Student: return the actual rows deleted
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        final int rowsDeleted;

        if (null == selection)
        {
            selection = "1";
        }

        switch (match) {
            case BODY_TEMPERATURE: {
                rowsDeleted = db.delete(HealthContract.BodyTemperatureEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            case PULSE: {
                rowsDeleted = db.delete(HealthContract.PulseEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(HealthContract.BodyTemperatureEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(HealthContract.BodyTemperatureEntry.COLUMN_DATE);
            values.put(HealthContract.BodyTemperatureEntry.COLUMN_DATE, HealthContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case BODY_TEMPERATURE:
                normalizeDate(values);
                rowsUpdated = db.update(HealthContract.BodyTemperatureEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PULSE:
                rowsUpdated = db.update(HealthContract.PulseEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BODY_TEMPERATURE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(HealthContract.BodyTemperatureEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}