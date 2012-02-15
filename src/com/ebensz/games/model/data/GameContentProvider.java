package com.ebensz.games.model.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.ebensz.games.model.data.table.RoleTable;

/**
 * User: tosmart Date: 11-5-24
 */
public class GameContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        switch (UriDomain.match(uri)) {

            case UriDomain.ALL_ROLES:
                return RoleTable.CONTENT_TYPE;

            case UriDomain.SPECIFIC_ROLE:
                return RoleTable.CONTENT_ITEM_TYPE;
        }

        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (UriDomain.match(uri)) {

            case UriDomain.ALL_ROLES:
                queryBuilder.setTables(RoleTable.NAME);
                if (projection == null) {
                    queryBuilder.setProjectionMap(RoleTable.projectMap());
                }
                break;

            case UriDomain.SPECIFIC_ROLE:
                queryBuilder.setTables(RoleTable.NAME);
                if (projection == null) {
                    queryBuilder.setProjectionMap(RoleTable.projectMap());
                }
                queryBuilder.appendWhere(String.format(
                        "%s=%d",
                        RoleTable.Columns._ID,
                        ContentUris.parseId(uri)));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return queryBuilder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;

        switch (UriDomain.match(uri)) {

            case UriDomain.ALL_ROLES:
                rowId = db.insert(RoleTable.NAME, null, values);
                break;


            default:
                throw new UnsupportedOperationException("INSERT for URI: " + uri);
        }

        return rowId == -1 ? null : ContentUris.withAppendedId(uri, rowId);
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (UriDomain.match(uri)) {

            case UriDomain.ALL_ROLES:
                return db.delete(RoleTable.NAME, selection, selectionArgs);

            case UriDomain.SPECIFIC_ROLE:
                return db.delete(
                        RoleTable.NAME,
                        String.format(
                                "%s=%d",
                                RoleTable.Columns._ID,
                                ContentUris.parseId(uri)),
                        null);

        }

        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (UriDomain.match(uri)) {

            case UriDomain.ALL_ROLES:
                return db.update(RoleTable.NAME, values, selection, selectionArgs);

            case UriDomain.SPECIFIC_ROLE:
                return db.update(
                        RoleTable.NAME,
                        values,
                        String.format(
                                "%s=%d",
                                RoleTable.Columns._ID,
                                ContentUris.parseId(uri)),
                        null);


        }

        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    private DatabaseHelper dbHelper;
}
