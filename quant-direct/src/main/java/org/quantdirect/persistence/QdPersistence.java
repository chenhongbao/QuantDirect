/*
 * Copyright (c) 2020-2021. Hongbao Chen <chenhongbao@outlook.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.quantdirect.persistence;

import org.quantdirect.*;
import org.quantdirect.tools.LOG;

import java.sql.*;
import java.time.*;
import java.util.Collection;
import java.util.HashSet;

class QdPersistence extends Persistence {

    private Connection c;

    QdPersistence() {
        driver();
    }

    private void driver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new Error("Can't find database driver.", e);
        }
    }

    private synchronized Connection conn() throws SQLException {
        if (!c.isValid(1)) {
            c = createConnection();
        }
        return c;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:./QuantDirect;AUTO_RECONNECT=TRUE", "sa", "");
    }

    private boolean tableExists(String table) throws SQLException {
        var meta = conn().getMetaData();
        var rs = meta.getTables(null, null, table, null);
        return rs.next();
    }

    private void ensureTrade() throws SQLException {
        if (!tableExists("_TRADE_TABLE")) {
            createTradeTable();
        }
    }

    private void ensureOrder() throws SQLException {
        if (!tableExists("_ORDER_TABLE")) {
            createOrderTable();
        }
    }

    private void ensureContractTable() throws SQLException {
        if (!tableExists("_CONTRACT_TABLE")) {
            createContractTable();
        }
    }

    private void sqlCreate(String sql) throws SQLException {
        try (Statement statement = conn().createStatement()) {
            var n = statement.execute(sql);
            var c = statement.getUpdateCount();
            if (c != 1) {
                LOG.write("Create table affects " + c + " row.", this);
            }
        }
    }

    private void createTradeTable() throws SQLException {
        String sql = "CREATE TABLE _TRADE_TABLE\n" +
                     "(\n" +
                     "    _TRADE_ID      CHAR(128),\n" +
                     "    _ORDER_ID      CHAR(128),\n" +
                     "    _INSTRUMENT_ID CHAR(128),\n" +
                     "    _EXCHANGE_ID   CHAR(128),\n" +
                     "    _PRICE         DECIMAL,\n" +
                     "    _QUANTITY      BIGINT,\n" +
                     "    _DIRECTION     CHAR(32),\n" +
                     "    _OFFSET        CHAR(32),\n" +
                     "    _TRADING_DAY   BIGINT,\n" +
                     "    _UPDATE_TIME   BIGINT\n" +
                     ")";
        sqlCreate(sql);
    }

    private void createOrderTable() throws SQLException {
        String sql = "CREATE TABLE _ORDER_TABLE\n" +
                     "(\n" +
                     "    _ORDER_ID       CHAR(128),\n" +
                     "    _INSTRUMENT_ID  CHAR(128),\n" +
                     "    _EXCHANGE_ID    CHAR(128),\n" +
                     "    _PRICE          DECIMAL,\n" +
                     "    _QUANTITY       BIGINT,\n" +
                     "    _STATUS         CHAR(32),\n" +
                     "    _STATUE_MESSAGE CHAR(256),\n" +
                     "    _DIRECTION      CHAR(32),\n" +
                     "    _OFFSET         CHAR(32),\n" +
                     "    _TRADING_DAY    BIGINT,\n" +
                     "    _UPDATE_TIME    BIGINT\n" +
                     ")";
        sqlCreate(sql);
    }

    private void createContractTable() throws SQLException {
        String sql = "CREATE TABLE _CONTRACT_TABLE\n" +
                     "(\n" +
                     "    _CONTRACT_ID   CHAR(128),\n" +
                     "    _TRADE_ID      CHAR(128),\n" +
                     "    _INSTRUMENT_ID CHAR(128),\n" +
                     "    _EXCHANGE_ID   CHAR(128),\n" +
                     "    _DIRECTION     CHAR(32),\n" +
                     "    _OPEN_PRICE    DECIMAL,\n" +
                     "    _CLOSE_PRICE   DECIMAL,\n" +
                     "    _TRADING_DAY   BIGINT\n" +
                     "    _CLOSE_TIME    BIGINT" +
                     ")";
        sqlCreate(sql);
    }

    @Override
    public void insert(Trade trade) {
        String sql = "INSERT INTO _TRADE_TABLE(" +
                     "_TRADE_ID, " +
                     "_ORDER_ID, " +
                     "_INSTRUMENT_ID, " +
                     "_EXCHANGE_ID, " +
                     "_PRICE, " +
                     "_QUANTITY, " +
                     "_DIRECTION, " +
                     "_OFFSET, " +
                     "_TRADING_DAY, " +
                     "_UPDATE_TIME)\n" +
                     "VALUES (" +
                     toSql(trade.getTradeId()) + "," +
                     toSql(trade.getOrderId()) + "," +
                     toSql(trade.getInstrumentId()) + "," +
                     toSql(trade.getExchangeId()) + "," +
                     toSql(trade.getPrice()) + "," +
                     toSql(trade.getQuantity()) + "," +
                     toSql(trade.getDirection()) + "," +
                     toSql(trade.getOffset()) + "," +
                     toSql(trade.getTradingDay()) + "," +
                     toSql(trade.getUpdateTime()) +
                     ")";
        try {
            ensureTrade();
            sqlUpdate(sql, 1);
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
        }
    }

    private void sqlUpdate(String sql, int rows) throws SQLException {
        var statement = conn().createStatement();
        var c = statement.executeUpdate(sql);
        if (c != rows) {
            LOG.write("Insert row affects " + c + " row.", this);
        }
    }

    private String toSql(String s) {
        if (s == null) {
            return "NULL";
        }
        return "'" + s + "'";
    }

    private String toSql(double v) {
        return String.format("%.4f", v);
    }

    private String toSql(long v) {
        return Long.toString(v);
    }

    private String toSql(Direction d) {
        return "'" + d.name() + "'";
    }

    private String toSql(Offset o) {
        return "'" + o.name() + "'";
    }

    private String toSql(LocalDateTime n) {
        if (n == null) {
            return "NULL";
        }
        var x = n.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return Long.toString(x);
    }

    private String toSql(LocalDate n) {
        if (n == null) {
            return "NULL";
        }
        var x = LocalDateTime.of(n, LocalTime.of(0, 0));
        return toSql(x);
    }

    private String toSql(Order.Status status) {
        return "'" + status.name() + "'";
    }

    @Override
    public void insert(Order order) {
        String sql = "INSERT INTO _ORDER_TABLE (" +
                     "_ORDER_ID, " +
                     "_INSTRUMENT_ID, " +
                     "_EXCHANGE_ID, " +
                     "_PRICE, " +
                     "_QUANTITY, " +
                     "_STATUS, " +
                     "_STATUE_MESSAGE, " +
                     "_DIRECTION, " +
                     "_OFFSET, " +
                     "_TRADING_DAY, " +
                     "_UPDATE_TIME) " +
                     "VALUES (" +
                     toSql(order.getOrderId()) + "," +
                     toSql(order.getInstrumentId()) + "," +
                     toSql(order.getExchangeId()) + "," +
                     toSql(order.getPrice()) + "," +
                     toSql(order.getQuantity()) + "," +
                     toSql(order.getStatus()) + "," +
                     toSql(order.getStatusMessage()) + "," +
                     toSql(order.getDirection()) + "," +
                     toSql(order.getOffset()) + "," +
                     toSql(order.getTradingDay()) + "," +
                     toSql(order.getUpdateTime()) +
                     ")";
        try {
            ensureOrder();
            sqlUpdate(sql, 1);
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
        }
    }

    @Override
    public void insert(Contract contract) {
        String sql = "INSERT INTO _CONTRACT_TABLE(" +
                     "_CONTRACT_ID, " +
                     "_TRADE_ID, " +
                     "_INSTRUMENT_ID, " +
                     "_EXCHANGE_ID, " +
                     "_DIRECTION, " +
                     "_OPEN_PRICE, " +
                     "_CLOSE_PRICE, " +
                     "_TRADING_DAY, " +
                     "_CLOSE_TIME)" +
                     "VALUES (" +
                     toSql(contract.getContractId()) + "," +
                     toSql(contract.getTradeId()) + "," +
                     toSql(contract.getInstrumentId()) + "," +
                     toSql(contract.getExchangeId()) + "," +
                     toSql(contract.getDirection()) + "," +
                     toSql(contract.getOpenPrice()) + "," +
                     toSql(contract.getClosePrice()) + "," +
                     toSql(contract.getTradingDay()) + "," +
                     toSql(contract.getCloseTime()) +
                     ")";
        try {
            ensureContractTable();
            sqlUpdate(sql, 1);
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
        }
    }

    @Override
    public void closeContract(String instrumentId, String exchangeId, Direction direction,
            double price, long quantity, LocalDateTime closeTime) {
        String sql = "UPDATE _CONTRACT_TABLE " +
                     "SET _CLOSE_PRICE = " + toSql(price) + ", " +
                     "_CLOSE_TIME  = " + toSql(closeTime) + " " +
                     "WHERE _CONTRACT_ID IN ( " +
                     "    SELECT _CONTRACT_ID " +
                     "    FROM _CONTRACT_TABLE " +
                     "    WHERE _INSTRUMENT_ID = " + toSql(instrumentId) +
                     "      AND _EXCHANGE_ID = " + toSql(exchangeId) +
                     "      AND _DIRECTION = " + toSql(direction) +
                     "    ORDER BY _TRADING_DAY " +
                     "    LIMIT " + toSql(quantity) + " " +
                     "    )";
        try {
            ensureContractTable();
            sqlUpdate(sql, (int) quantity);
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
        }
    }

    private ResultSet sqlQuery(String sql) throws SQLException {
        var statement = conn().createStatement();
        return statement.executeQuery(sql);
    }

    @Override
    public Collection<ContractName> getContractNames() {
        try {
            ensureContractTable();
            String sql = "SELECT DISTINCT _INSTRUMENT_ID, _EXCHANGE_ID FROM _CONTRACT_TABLE";
            var rs = sqlQuery(sql);
            return toContractNames(rs);
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
            return new HashSet<>();
        }
    }

    private Collection<ContractName> toContractNames(ResultSet rs) throws SQLException {
        var r = new HashSet<ContractName>();
        while (rs.next()) {
            var i = rs.getString("_INSTRUMENT_ID");
            var e = rs.getString("_EXCHANGE_ID");
            r.add(ContractName.create(i, e));
        }
        return r;
    }

    @Override
    public Collection<Contract> getContracts(String instrumentId, String exchangeId, Direction direction) {
        String sql = "SELECT * " +
                     "FROM _CONTRACT_TABLE " +
                     "WHERE _INSTRUMENT_ID = " + toSql(instrumentId) +
                     "  AND _EXCHANGE_ID = " + toSql(exchangeId) +
                     "  AND _DIRECTION = " + toSql(direction);
        try {
            ensureContractTable();
            var rs = sqlQuery(sql);
            return toContracts(rs);
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
            return new HashSet<>();
        }
    }

    private Collection<Contract> toContracts(ResultSet rs) throws SQLException {
        var r = new HashSet<Contract>();
        while (rs.next()) {
            var c = new Contract();
            c.setContractId(rs.getString("_CONTRACT_ID"));
            c.setTradeId(rs.getString("_TRADE_ID"));
            c.setInstrumentId(rs.getString("_INSTRUMENT_ID"));
            c.setExchangeId(rs.getString("_EXCHANGE_ID"));
            c.setDirection(toDirection(rs.getString("_DIRECTION")));
            c.setOpenPrice(rs.getDouble("_OPEN_PRICE"));
            c.setClosePrice(rs.getDouble("_CLOSE_PRICE"));
            c.setTradingDay(toTradingDay(rs.getLong("_TRADING_DAY")));
            r.add(c);
        }
        return r;
    }

    private LocalDate toTradingDay(long m) {
        var i = Instant.ofEpochMilli(m);
        return LocalDate.ofInstant(i, ZoneId.ofOffset("", ZoneOffset.ofHours(8)));
    }

    private Direction toDirection(String direction) {
        var d = direction.equals(Direction.BUY.name()) ? Direction.BUY :
                (direction.equals(Direction.SELL.name()) ? Direction.SELL : null);
        if (d == null) {
            throw new Error("Illegal direciton: " + direction + ".");
        }
        return d;
    }

    @Override
    public long countContractsBefore(String instrumentId, String exchangeId, Direction direction,
            LocalDateTime before) {
        var n = before.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        String sql = "SELECT COUNT(*) " +
                     "FROM _CONTRACT_TABLE " +
                     "WHERE _INSTRUMENT_ID = " + toSql(instrumentId) +
                     "  AND _EXCHANGE_ID = " + toSql(exchangeId) +
                     "  AND _DIRECTION = " + toSql(direction) +
                     "  AND _TRADING_DAY < " + toSql(before);
        try {
            ensureContractTable();
            var rs = sqlQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
        }
        return 0;
    }

    @Override
    public long countOpenContractsBefore(String instrumentId, String exchangeId,
            Direction direction, LocalDateTime before) {
        var n = before.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        String sql = "SELECT COUNT(*) " +
                     "FROM _CONTRACT_TABLE " +
                     "WHERE _INSTRUMENT_ID = " + toSql(instrumentId) +
                     "  AND _EXCHANGE_ID = " + toSql(exchangeId) +
                     "  AND _DIRECTION = " + toSql(direction) +
                     "  AND (_CLOSE_TIME IS NULL OR _CLOSE_TIME = 0)\n" +
                     "  AND _TRADING_DAY < " + toSql(before);
        try {
            ensureContractTable();
            var rs = sqlQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
        }
        return 0;
    }

    @Override
    public void setProperty(String key, String value) {
        var p = getProperty(key);
        if (p == null) {
            insertProperty(key, value);
        } else {
            updateProperty(key, value);
        }
    }

    private void updateProperty(String key, String value) {
        String sql = "UPDATE _PROPERTY_TABLE SET _VALUE = " +
                     toSql(value) +
                     " WHERE _KEY = " +
                     toSql(key);
        try {
            ensurePropertyTable();
            sqlUpdate(sql, 1);
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
        }
    }

    private void insertProperty(String key, String value) {
        String sql = "INSERT INTO _PROPERTY_TABLE (" +
                     "_KEY, " +
                     "_VALUE) " +
                     "VALUES (" +
                     toSql(key) + "," +
                     toSql(value) +
                     ")";
        try {
            sqlUpdate(sql, 1);
        } catch (SQLException throwables) {
            LOG.write(throwables, this);
        }
    }

    @Override
    public String getProperty(String key) {
        try {
            ensurePropertyTable();
            String sql = "SELECT _VALUE FROM _PROPERTY_TABLE WHERE _KEY = " + toSql(key);
            var rs = sqlQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }
        } catch (SQLException throwable) {
            LOG.write(throwable, this);
            return null;
        }
    }

    private void ensurePropertyTable() throws SQLException {
        if (!tableExists("_PROPERTY_TABLE")) {
            createPropertyTable();
        }
    }

    private void createPropertyTable() throws SQLException {
        String sql = "CREATE TABLE _PROPERTY_TABLE\n" +
                     "(\n" +
                     "    _KEY   CHAR(128),\n" +
                     "    _VALUE CHAR(128)\n" +
                     ")";
        sqlCreate(sql);
    }
}
