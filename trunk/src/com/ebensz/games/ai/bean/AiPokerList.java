package com.ebensz.games.ai.bean;

import java.util.*;

/**
 * User: tosmart
 * Date: 2010-6-28
 * Time: 8:40:50
 */
public class AiPokerList implements Cloneable {

    public AiPokerList() {
        idArray = new ArrayList<Character>();
        counter = new HashMap<Character, Integer>();
    }

    public AiPokerList(char[] PokerIds) {

        idArray = new ArrayList<Character>();
        counter = new HashMap<Character, Integer>();

        add(PokerIds);
    }

    @Override
    public AiPokerList clone() throws CloneNotSupportedException {

        AiPokerList deepCopy = (AiPokerList) super.clone();
        deepCopy.idArray = new ArrayList<Character>();
        deepCopy.counter = new HashMap<Character, Integer>();

        for (Character anIdArray : idArray) {
            deepCopy.add(anIdArray);
        }

        deepCopy.sortAndCount();

        return deepCopy;
    }

    public AiPokerList getClone() {
        try {
            return clone();
        }
        catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    public void add(char[] PokerIds) {

        for (char PokerId : PokerIds) {
            idArray.add(PokerId);
        }

        sortAndCount();
    }

    public void add(char PokerId) {
        add(new char[]{PokerId});
    }

    public void add(AiPokerList pokers) {
        idArray.addAll(pokers.idArray);
        sortAndCount();
    }

    public void clear() {
        idArray.clear();
        sortAndCount();
    }

    public int size() {
        return idArray.size();
    }

    public boolean isEmpty() {
        return idArray.size() == 0;
    }

    public char get(int index) {
        return idArray.get(index);
    }

    public AiPokerList remove(AiPokerList pokers) {

        for (char id : pokers.idArray) {

            Iterator<Character> iterator = idArray.iterator();

            while (iterator.hasNext()) {

                if (id == iterator.next()) {
                    iterator.remove();
                    break;
                }
            }
        }

        sortAndCount();

        return this;
    }

    public AiPokerList remove(char[] ids) {

        for (char id : ids) {

            Iterator<Character> iterator = idArray.iterator();

            while (iterator.hasNext()) {

                if (id == iterator.next()) {
                    iterator.remove();
                    break;
                }
            }
        }

        sortAndCount();

        return this;
    }

    public Character getMinChar() {
        return idArray.get(size() - 1);
    }

    public Character getMaxChar() {
        return idArray.get(0);
    }

    public Set<Character> charSet() {
        Set<Character> charSet = new HashSet<Character>();
        charSet.addAll(idArray);
        return charSet;
    }

    public Map<Character, Integer> getCounter() {
        return counter;
    }

    public boolean contains(char id) {
        return counter.get(id) > 0;
    }

    public String toDisplay() {

        char[] ids = toString().toCharArray();
        StringBuilder output = new StringBuilder();

        for (char id : ids) {
            output.append(translateDisplayChar(id));
        }

        return output.toString();
    }

    public String toString() {

        StringBuilder buffer = new StringBuilder();

        for (char c : idArray) {
            buffer.append(c);
        }

        return buffer.toString();
    }

    protected void sortAndCount() {

        char[] allIds = Phase.ALL_POKERS.toCharArray();
        counter = new HashMap<Character, Integer>();

        Comparator<Character> comparator = new Comparator<Character>() {
            public int compare(Character o1, Character o2) {
                return o2.compareTo(o1);
            }
        };
        Collections.sort(idArray, comparator);

        for (char id : allIds) {

            int found = Collections.binarySearch(idArray, id, comparator);
            int count = found > -1 ? 1 : 0;

            if (found > -1) {

                int pos = found;

                while (++pos < size()) {
                    if (idArray.get(pos) != id) break;
                    count++;
                }

                pos = found;

                while (--pos > -1) {
                    if (idArray.get(pos) != id) break;
                    count++;
                }
            }

            counter.put(id, count);
        }
    }

    private char translateDisplayChar(char id) {

        switch (id) {

            case 'a':
                return '0';

            case 'b':
                return 'J';

            case 'c':
                return 'Q';

            case 'd':
                return 'K';

            case 'e':
                return 'A';

            case 'x':
                return '2';

            case 'y':
                return 'B';

            case 'z':
                return 'R';
        }

        return id;
    }

    public List<Character> getList() {
        return idArray;
    }

    protected List<Character> idArray;
    protected Map<Character, Integer> counter;
}
