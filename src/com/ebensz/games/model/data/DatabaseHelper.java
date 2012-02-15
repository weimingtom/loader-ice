package com.ebensz.games.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.utils.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import static com.ebensz.games.model.data.table.RoleTable.*;

/**
 * User: tosmart Date: 11-5-24
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "loader.db";
    public static final int DATABASE_VERSION = 1;

    private static final String COMMA = ",";
    private static final int TOKEN_COUNT = 7;

    private static final String ASSERTS_ROLES_FILE = "roles.txt";
    private static final String ASSERTS_CAREERS_FILE = "careers.xml";
    private static final String NAME_DICT_FILE = "name_dict.txt";

    private static final String DEFAULT_PLAYER_NAME = "玩家";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        insertInitData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(createSql());
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL(dropSQL());
    }

    private void insertInitData(SQLiteDatabase db) {
        initRoles(db);
    }


    private void initRoles(SQLiteDatabase db) {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            context.getAssets().open(ASSERTS_ROLES_FILE)
                    )
            );
            doLoopInsertRoles(db, reader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) try {
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doLoopInsertRoles(SQLiteDatabase db, BufferedReader reader)
            throws IOException {

        String line = reader.readLine();
        boolean atFirstLine = true;

        while (line != null) {

            String[] tokens = line.split(COMMA);

            if (tokens.length == TOKEN_COUNT) {

                int index = 0;

                ContentValues values = new ContentValues();

                String id = tokens[index++];
                values.put(Columns._ID, id);
                values.put(Columns.ROLE_ID, id);

                if (atFirstLine) {
                    values.put(Columns.NAME, nextName());
                    index++;
                    atFirstLine = false;
                }
                else {
                    values.put(Columns.NAME, tokens[index++]);
                }

                values.put(Columns.ICON, tokens[index++]);
                values.put(Columns.AGE, tokens[index++]);
                values.put(Columns.WEALTH, tokens[index++]);
                values.put(Columns.EXP, tokens[index++]);
                values.put(Columns.MALE, tokens[index]);

                values.put(Columns.RANKING, Role.RANKING_UNKNOWN);

                db.insert(NAME, Columns._ID, values);
            }

            line = reader.readLine();
        }
    }

    private String nextName() {

        if (nameDict == null) {
            nameDict = loadNameDict();
        }
        if (nameDict == null) return DEFAULT_PLAYER_NAME;

        Random random = new Random();

        return nameDict.get(random.nextInt(nameDict.size()));
    }

    private List<String> loadNameDict() {
        return FileUtil.readLines(context, NAME_DICT_FILE);
    }

    private Context context;
    private List<String> nameDict;
}
