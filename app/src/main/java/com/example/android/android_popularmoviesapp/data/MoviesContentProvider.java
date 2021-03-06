package com.example.android.android_popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        * Creates the UriMatcher that will match each URI to the CODE_MOVIES and
        * CODE_MOVIE_WITH_ID constants defined above.
        * @return A UriMatcher that correctly matches the constants for  CODE_MOVIES and CODE_MOVIE_WITH_ID
         */
       private static UriMatcher buildUriMatcher() {

            /*
             * All paths added to the UriMatcher have a corresponding code to return when a match is
             * found. The code passed into the constructor of UriMatcher here represents the code to
             * return for the root URI. It's common to use NO_MATCH as the code for this case.
             */
            final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            final String authority = MoviesContract.CONTENT_AUTHORITY;

            matcher.addURI(authority, MoviesContract.PATH_FAV, CODE_MOVIES);

            /*
             * The "/#" signifies to the UriMatcher that if CODE_MOVIES is followed by ANY number,
             * that it should return the CODE_MOVIE_WITH_ID code
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
     * Handles requests to insert a set of new rows.          *
         * @param uri    The content:// URI of the insertion request.
         * @return The number of values that were inserted.
         */

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {

           case CODE_MOVIE_WITH_ID: {

               String[] selectionArguments = new String[]{};

                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        MoviesContract.MoviesEntry.COLUMN_DATE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
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

    @Nullable
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType.");
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

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

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