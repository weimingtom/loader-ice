package com.ebensz.games.model.Role;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.ebensz.games.model.data.table.RoleTable;

import java.util.ArrayList;
import java.util.List;

import static com.ebensz.games.model.data.table.RoleTable.Columns.*;

/**
 * 此类用来管理所有的玩家角色，包括人类玩家。 此类以一个List为基础，人类玩家对应的角色放在第0个的位置上
 * <p/>
 * User: tosmart Date: 11-5-23 Time: 上午9:29
 */
public class RoleDatabase {

    public RoleDatabase(Context context) {
        this.context = context;
    }

    public List<Role> load() {

        List<Role> roles = new ArrayList<Role>();

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    RoleTable.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            while (cursor.moveToNext()) {
                roles.add(bindRole(cursor));
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }

        return roles;
    }

    public void add(Role role) {

        ContentValues values = new ContentValues();

        values.put(ROLE_ID, role.getId());
        values.put(NAME, role.getName());
        values.put(ICON, role.getIconIndex());
        values.put(AGE, role.getAge());
        values.put(WEALTH, role.getWealth());
        values.put(EXP, role.getExp());
        values.put(MALE, role.isMale() ? 1 : 0);
        values.put(RANKING, role.getRanking());
        values.put(AREA, role.getArea());

        context.getContentResolver().insert(RoleTable.CONTENT_URI, values);

    }

    public boolean update(Role role) {
        ContentValues values = new ContentValues();

        values.put(ROLE_ID, role.getId());
        values.put(NAME, role.getName());
        values.put(ICON, role.getIconIndex());
        values.put(AGE, role.getAge());
        values.put(WEALTH, role.getWealth());
        values.put(EXP, role.getExp());
        values.put(MALE, role.isMale() ? 1 : 0);
        values.put(RANKING, role.getRanking());
        values.put(AREA, role.getArea());

        Uri uri = Uri.withAppendedPath(
                RoleTable.CONTENT_URI,
                Integer.toString(role.getId())
        );

        int result = context.getContentResolver().update(uri, values, null, null);

        return result == 1;
    }

    private Role bindRole(Cursor cursor) {

        int roleId = cursor.getInt(cursor.getColumnIndex(ROLE_ID));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        int icon = cursor.getInt(cursor.getColumnIndex(ICON));
        int age = cursor.getInt(cursor.getColumnIndex(AGE));
        int wealth = cursor.getInt(cursor.getColumnIndex(WEALTH));
        int exp = cursor.getInt(cursor.getColumnIndex(EXP));
        int male = cursor.getInt(cursor.getColumnIndex(MALE));
        int ranking = cursor.getInt(cursor.getColumnIndex(RANKING));
        String area = cursor.getString(cursor.getColumnIndex(AREA));

        Role role = new Role();
        role.setId(roleId);
        role.setName(name);
        role.setIconIndex(icon);
        role.setAge(age);
        role.setWealth(wealth);
        role.setExp(exp);
        role.setMale(male);
        role.setRanking(ranking);
        role.setArea(area);

        return role;
    }

    private Context context;
}
