package com.example.android.android_popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.android.android_popularmoviesapp.data.MoviesContract.MoviesEntry.TABLE_NAME;

/**
     * This class serves as the ContentProvider for all of Sunshine's data. This class allows us to
     * bulkInsert data, query data, and delete data.
     * <p>
     * Although ContentProvider implementation requires the implementation of additional methods to
     * perform single inserts, updates, and the ability to get the type of the data from a URI.
     * However, here, they are not implemented for the sake of brevity and simplicity. If you would
     * like, you may implement them on your own. However, we are not going to be teaching how to do
     * so in this course.
     */
    public class  MoviesContentProvider extends ContentProvider {

        /*
         * These constant will be used to match URIs with the data they are looking for. We will take
         * advantage of the UriMatcher class to make that matching MUCH easier than doing something
         * ourselves, such as using regular expressions.
         */
        private static final int CODE_MOVIES = 100;
    private static final int CODE_MOVIE_WITH_ID = 101;

        /*
         * The URI Matcher used by this content provider. The leading "s" in this variable name
         * signifies that this UriMatcher is a static member variable of WeatherProvider and is a
         * common convention in Android programming.
         */
        private static final UriMatcher sUriMatcher = buildUriMatcher();


        private MoviesDbHelper mOpenHelper;

       /**
         * Creates the UriMatcher that will match each URI to the CODE_WEATHER and
         * CODE_WEATHER_WITH_DATE constants defined above.
         * <p>
         * It's possible you might be thinking, "Why create a UriMatcher when you can use regular
         * expressions instead? After all, we really just need to match some patterns, and we can
         * use regular expressions to do that right?" Because you're not crazy, that's why.
         * <p>
         * UriMatcher does all the hard work for you. You just have to tell it which code to match
         * with which URI, and it does the rest automagically. Remember, the best programmers try
         * to never reinvent the wheel. If there is a solution for a problem that exists and has
         * been tested and proven, you should almost always use it unless there is a compelling
         * reason not to.
         *
         * @return A UriMatcher that correctly matches the constants for CODE_WEATHER and CODE_WEATHER_WITH_DATE
         */
       private static UriMatcher buildUriMatcher() {

            /*
             * All paths added to the UriMatcher have a corresponding code to return when a match is
             * found. The code passed into the constructor of UriMatcher here represents the code to
             * return for the root URI. It's common to use NO_MATCH as the code for this case.
             */
            final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            final String authority = MoviesContract.CONTENT_AUTHORITY;

            /*
             * For each type of URI you want to add, create a corresponding code. Preferably, these are
             * constant fields in your class so that you can use them throughout the class and you no
             * they aren't going to change. In Sunshine, we use CODE_WEATHER or CODE_WEATHER_WITH_DATE.
             */

            /* This URI is content://com.example.android.sunshine/weather/ */
            matcher.addURI(authority, MoviesContract.PATH_FAV, CODE_MOVIES);

            /*
             * This URI would look something like content://com.example.android.sunshine/weather/1472214172
             * The "/#" signifies to the UriMatcher that if PATH_WEATHER is followed by ANY number,
             * that it should return the CODE_WEATHER_WITH_DATE code
             */
            matcher.addURI(authority, MoviesContract.PATH_FAV + "/#", CODE_MOVIE_WITH_ID);

            return matcher;
        }


        /**
         * In onCreate, we initialize our content provider on startup. This method is called for all
         * registered content providers on the application main thread at application launch time.
         * It must not perform lengthy operations, or application startup will be delayed.
         *
         * Nontrivial initialization (such as opening, upgrading, and scanning
         * databases) should be deferred until the content provider is used (via {@link #query},
         * {@link #bulkInsert(Uri, ContentValues[])}, etc).
         *
         * Deferred initialization keeps application startup fast, avoids unnecessary work if the
         * provider turns out not to be needed, and stops database errors (such as a full disk) from
         * halting application launch.
         *
         * @return true if the provider was successfully loaded, false otherwise
         */
        @Override
        public boolean onCreate() {
            /*
             * As noted in the comment above, onCreate is run on the main thread, so performing any
             * lengthy operations will cause lag in your app. Since WeatherDbHelper's constructor is
             * very lightweight, we are safe to perform that initialization here.
             */
            mOpenHelper = new MoviesDbHelper(getContext());

            return true;
        }


    /**
         * Handles requests to insert a set of new rows. In Sunshine, we are only going to be
         * inserting multiple rows of data at a time from a weather forecast. There is no use case
         * for inserting a single row of data into our ContentProvider, and so we are only going to
         * implement bulkInsert. In a normal ContentProvider's implementation, you will probably want
         * to provide proper functionality for the insert method as well.
         *
         * @param uri    The content:// URI of the insertion request.
         *
         * @return The number of values that were inserted.
         */

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

//      COMPLETED (10) Handle queries on both the weather and weather with date URI
        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {

            /*
             * When sUriMatcher's match method is called with a URI that looks something like this
             *
             *      content://com.example.android.sunshine/weather/1472214172
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return the weather for a particular date. The date in this code is encoded in
             * milliseconds and is at the very end of the URI (1472214172) and can be accessed
             * programmatically using Uri's getLastPathSegment method.
             *
             * In this case, we want to return a cursor that contains one row of weather data for
             * a particular date.
             */
           case CODE_MOVIE_WITH_ID: {

                /*
                 * In order to determine the date associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 1472214172 and
                 * represents the number of seconds since the epoch, or UTC time.
                 */
       /*         String normalizedUtcDateString = uri.getLastPathSegment();

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                String[] selectionArguments = new String[] {}; //{normalizedUtcDateString};

                cursor = mOpenHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        TABLE_NAME,
                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        /*
                         * The URI that matches CODE_WEATHER_WITH_DATE contains a date at the end
                         * of it. We extract that date and use it with these next two lines to
                         * specify the row of weather we want returned in the cursor. We use a
                         * question mark here and then designate selectionArguments as the next
                         * argument for performance reasons. Whatever Strings are contained
                         * within the selectionArguments array will be inserted into the
                         * selection statement by SQLite under the hood.
                         */
                        MoviesContract.MoviesEntry.COLUMN_DATE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.example.android.sunshine/weather/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the weather in our weather table.
             *
             * In this case, we want to return a cursor that contains every row of weather data
             * in our weather table.
             */
            case CODE_MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";


        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIES:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            case CODE_MOVIE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        "id=?", new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }



        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

//  Return the number of rows deleted
        return numRowsDeleted;

    }


    /**
     * In Sunshine, we aren't going to do anything with this method. However, we are required to
     * override it as WeatherProvider extends ContentProvider and getType is an abstract method in
     * ContentProvider. Normally, this method handles requests for the MIME type of the data at the
     * given URI. For example, if your app provided images at a particular URI, then you would
     * return an image URI from this method.
     *
     * @param uri the URI to query.
     * @return nothing in Sunshine, but normally a MIME type string, or null if there is no type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Sunshine.");
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // COMPLETED (2) Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case CODE_MOVIES:
                // Inserting values into tasks table
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("No need to implement update in Popular Movies");
    }

}
