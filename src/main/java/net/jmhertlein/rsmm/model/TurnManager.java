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

import net.jmhertlein.rsmm.model.update.UpdatableManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author joshua
 */
public class TurnManager extends UpdatableManager {
    private final Connection conn;

    public TurnManager(Connection conn) {
        this.conn = conn;
    }

    public Optional<Turn> getOpenTurn(String itemName) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Turn WHERE item_name=? AND close_ts IS NULL")) {
            p.setString(1, itemName);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Turn(conn, rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public void newTurn(String itemName) throws SQLException, DuplicateOpenTurnException {
        if (getOpenTurn(itemName).isPresent()) {
            throw new DuplicateOpenTurnException(itemName);
        }

        try (PreparedStatement p = conn.prepareStatement("INSERT INTO Turn(item_name) VALUES(?)")) {
            p.setString(1, itemName);
            p.executeUpdate();
        }
    }

    public List<Turn> getOpenTurns() throws SQLException {
        List<Turn> turns = new ArrayList<>();
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Turn WHERE close_ts IS NULL")) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    turns.add(new Turn(conn, rs));
                }
            }
        }
        return turns;
    }
}
