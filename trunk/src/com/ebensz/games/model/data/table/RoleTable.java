package com.ebensz.games.model.data.table;

import android.net.Uri;
import android.provider.BaseColumns;
import com.ebensz.games.model.data.UriDomain;

import java.util.HashMap;
import java.util.Map;

/**
 * User: tosmart Date: 11-5-24
 */
public class RoleTable {

    public static final String NAME = "roles";

    public static final Uri CONTENT_URI = Uri.parse(String.format("content://%s/roles", UriDomain.AUTHORITY));

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/role";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/role";

    public static interface Columns extends BaseColumns {
        String ROLE_ID = "role_id";
        String NAME = "name";
        String ICON = "icon";
        String AGE = "age";
        String WEALTH = "wealth";
        String EXP = "exp";
        String MALE = "male";
        String AREA = "area";
        String RANKING = "ranking";
    }

    public static Map<String, String> projectMap() {

        HashMap<String, String> map = new HashMap<String, String>();

        map.put(Columns._ID, Columns._ID);
        map.put(Columns.ROLE_ID, Columns.ROLE_ID);
        map.put(Columns.NAME, Columns.NAME);
        map.put(Columns.ICON, Columns.ICON);
        map.put(Columns.AGE, Columns.AGE);
        map.put(Columns.WEALTH, Columns.WEALTH);
        map.put(Columns.EXP, Columns.EXP);
        map.put(Columns.MALE, Columns.MALE);
        map.put(Columns.AREA, Columns.AREA);
        map.put(Columns.RANKING, Columns.RANKING);

        return map;
    }

    public static String createSql() {

        return
                "create table roles(" +
                        "_id     integer primary Key autoincrement," +
                        "role_id integer ," +
                        "name    text," +
                        "icon    integer," +
                        "age     integer," +
                        "wealth  integer," +
                        "exp     integer," +
                        "male    integer," +
                        "area    text," +
                        "ranking integer" +
                        ");";
    }

    public static String dropSQL() {
        return "drop table roles";
    }
}
