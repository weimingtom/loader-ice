package com.ebensz.games.model.data;

import android.content.UriMatcher;
import android.net.Uri;

/**
 * User: tosmart
 * Date: 11-5-24
 */
public class UriDomain {

    public static final String AUTHORITY = "com.ebensz.games";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final int ALL_ROLES = 1;
    public static final int SPECIFIC_ROLE = 2;

    public static final int ALL_CAREERS = 3;
    public static final int SPECIFIC_CAREER = 4;

    public static final int ALL_MATCH_PROGRESS = 5;
    public static final int SPECIFIC_MATCH_PROGRESS = 6;

    public static final int ALL_MATCH_SCHEDULER = 7;
    public static final int SPECIFIC_MATCH_SCHEDULERS = 8;

    public static final int ALL_ROUND_RESULT = 9;
    public static final int SPECIFIC_ROUND_RESULT = 10;

    public static final int ALL_LOGIN = 11;
    public static final int SPECIFIC_LOGIN = 12;

    public static final int ALL_ROUND_SCORE = 13;
    public static final int SPECIFIC_ROUND_SCORE = 14;


    public static int match(Uri uri) {
        return URI_MATCHER.match(uri);
    }

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        UriMatcher matcher = URI_MATCHER;
        matcher.addURI(AUTHORITY, "roles", ALL_ROLES);
        matcher.addURI(AUTHORITY, "roles/#", SPECIFIC_ROLE);

        matcher.addURI(AUTHORITY, "careers", ALL_CAREERS);
        matcher.addURI(AUTHORITY, "careers/#", SPECIFIC_CAREER);

        matcher.addURI(AUTHORITY, "match_progress", ALL_MATCH_PROGRESS);
        matcher.addURI(AUTHORITY, "match_progress/#", SPECIFIC_MATCH_PROGRESS);

        matcher.addURI(AUTHORITY, "match_scheduler", ALL_MATCH_SCHEDULER);
        matcher.addURI(AUTHORITY, "match_scheduler/#", SPECIFIC_MATCH_SCHEDULERS);

        matcher.addURI(AUTHORITY, "round_result", ALL_ROUND_RESULT);
        matcher.addURI(AUTHORITY, "round_result/#", SPECIFIC_ROUND_RESULT);

        matcher.addURI(AUTHORITY, "login", ALL_LOGIN);
        matcher.addURI(AUTHORITY, "login/#", SPECIFIC_LOGIN);

        matcher.addURI(AUTHORITY, "round_score", ALL_ROUND_SCORE);
        matcher.addURI(AUTHORITY, "round_score/#", SPECIFIC_ROUND_SCORE);
    }
}