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
package cafe.josh.rsmm.model;

import java.math.BigDecimal;

/**
 * @author joshua
 */
public class RSIntegers {

    private RSIntegers() {}

    public static String toString(int value) {
        if (value >= 10000000) {
            return (value / 1000000) + "M";
        } else if (value >= 100000) {
            return (value / 1000) + "k";
        } else {
            return Integer.toString(value);
        }
    }

    public static int parseInt(String s) {
        s = s.toLowerCase();

        int multiplier;
        if (s.endsWith("k")) {
            s = s.substring(0, s.length() - 1);
            multiplier = 1000;
        } else if (s.endsWith("m")) {
            s = s.substring(0, s.length() - 1);
            multiplier = 1000000;
        } else if (s.endsWith("b")) {
            s = s.substring(0, s.length() - 1);
            multiplier = 1000000000;
        } else {
            multiplier = 1;
        }

        return Integer.parseInt(s) * multiplier;
    }

}
