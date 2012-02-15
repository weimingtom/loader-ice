package com.ebensz.games.model.Role;

import android.content.Context;

import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-11-1
 * Time: 下午4:14
 */
public class RoleCenter {

    private static RoleCenter instance;

    public static RoleCenter getInstance(Context context) {
        if (instance == null) {
            instance = new RoleCenter(context);
            instance.load();
        }
        return instance;
    }

    private RoleCenter(Context context) {
        roleDatabase = new RoleDatabase(context);
    }


    private boolean load() {
        roles = roleDatabase.load();

        return roles != null;
    }

    public List<Role> getAllNpc() {
        return roles.subList(1, roles.size() - 1);
    }

    public Role getHuman() {
        return roles.get(0);
    }


    public boolean update(Role role) {
        return roleDatabase.update(role);
    }

    public Role findById(int roleId) {
        for (Role role : roles) {
            if (role.getId() == roleId)
                return role;
        }

        return null;
    }

    private List<Role> roles;
    private RoleDatabase roleDatabase;
}
