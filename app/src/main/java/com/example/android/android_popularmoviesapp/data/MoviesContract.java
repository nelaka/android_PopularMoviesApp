package com.example.android.android_popularmoviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the favMovies database. This class is not necessary, but keeps
 * the code organized.
 */

public class MoviesContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.android_popularmoviesapp";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
     * can handle. For instance,
     *
     *     content://com.example.android.sunshine/weather/
     *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     *
     * is a valid path for looking at weather data.
     *
     *      content://com.example.android.sunshine/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_FAV = "fav_movies";


    /* Inner class that defines the table contents of the favMovies table */
    public static final class MoviesEntry implements BaseColumns {
        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAV)
                .build();

        /* Used internally as the name of favMovies table. */
        public static final String TABLE_NAME = "fav_movies";
        /* Movie ID as returned by API, used to identify the trailer to be used */
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_TITLE = "title";

        /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular weather row represents. For example, if you live in the Eastern
         * Standard Time (EST) time zone and you load weather data at 9:00 PM on September 23, 2016,
         * the UTC time stamp for that particular time would be 1474678800000 in milliseconds.
         * However, due to time zone offsets, it would already be September 24th, 2016 in the GMT
         * time zone when it is 9:00 PM on the 23rd in the EST time zone. In this example, the date
         * column would hold the date representing September 23rd at midnight in GMT time.
         * (1474588800000)
         *
         * The reason we store GMT time and not local time is because it is best practice to have a
         * "normalized", or standard when storing the date and adjust as necessary when
         * displaying the date. Normalizing the date also allows us an easy way to convert to
         * local time at midnight, as all we have to do is add a particular time zone's GMT
         * offset to this date to get local time at midnight on the appropriate date.
         */
        public static final String COLUMN_DESC = "synopsis";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_DATE = "date";
    }




}