/*
 * Copyright (C) 2016 joshua
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jmhertlein.rsmm.model;

/**
 *
 * @author joshua
 */
public class RSInteger extends Number {

    private int value;

    public RSInteger(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        if(value >= 10000000) {
            return (value / 1000000) + "M";
        } else if(value >= 100000) {
            return (value / 1000) + "k";
        } else {
            return Integer.toString(value);
        }
    }

    public static RSInteger parseInt(String s) {
        s = s.toLowerCase();

        int multiplier;
        if(s.endsWith("k")) {
            s = s.substring(0, s.length()-1);
            multiplier = 1000;
        } else if(s.endsWith("m")) {
            s = s.substring(0, s.length()-1);
            multiplier = 1000000;
        } else if(s.endsWith("b")) {
            s = s.substring(0, s.length()-1);
            multiplier = 1000000000;
        } else {
            multiplier = 1;
        }

        int value = Integer.parseInt(s) * multiplier;
        return new RSInteger(value);
    }

}
