package com.ebensz.games.model.Role;

import java.io.Serializable;

/**
 * 角色.
 *
 * @author jason
 */
public class Role implements Serializable, Comparable<Role>, Cloneable {
    public static final int HUMAN_ID = 0;

    public static final int RANKING_UNKNOWN = -1;

    public static final int RANKING_TOO_BAD = Integer.MAX_VALUE;

    private static final long serialVersionUID = 1L;
    /**
     * 输光后为其重新加上分
     */
    public static final int ROLE_LOSE_WEALTH = 10000;

    public Role() {
        ranking = RANKING_UNKNOWN;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public int compareTo(Role another) {
        return id - another.id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconIndex() {
        return iconIndex;
    }

    public void setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWealth() {
        return wealth;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public boolean isMale() {
        return male == 1;
    }

    public void setMale(int male) {
        this.male = male;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Medal getMedal() {
        return medal;
    }

    public void setMedal(Medal medal) {
        this.medal = medal;
    }

    public int getWinRate() {
        return winRate;
    }

    public void setWinRate(int winRate) {
        this.winRate = winRate;
    }

    public static class Medal {
        private int goldenMedal; // 金牌
        private int silverMedal; // 银牌
        private int cupreousMedal; // 铜牌

        public int getGoldenMedal() {
            return goldenMedal;
        }

        public void setGoldenMedal(int goldenMedal) {
            this.goldenMedal = goldenMedal;
        }

        public int getSilverMedal() {
            return silverMedal;
        }

        public void setSilverMedal(int silverMedal) {
            this.silverMedal = silverMedal;
        }

        public int getCupreousMedal() {
            return cupreousMedal;
        }

        public void setCupreousMedal(int cupreousMedal) {
            this.cupreousMedal = cupreousMedal;
        }
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    @Override
    public Role clone() throws CloneNotSupportedException {
        return (Role) super.clone();
    }

    protected int id;
    protected String name;
    protected int iconIndex;
    protected int age;
    protected int wealth;
    protected int exp;
    protected int male; // 1 男 0 女

    protected String area;
    protected Medal medal;
    protected int winRate;
    protected int ranking;

}
