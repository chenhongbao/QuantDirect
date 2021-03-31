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

package org.quantdirect.platform;

import org.quantdirect.*;
import org.quantdirect.loader.Loader;
import org.quantdirect.tools.LOG;
import org.quantdirect.persistence.Persistence;
import org.quantdirect.tools.TOOLS;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class QdTransaction implements Transaction {
    @Override
    public void trade(Order order, int timeout, TimeUnit unit) throws TimeoutException, IOException {
        var h = new QdOrderHandler(order);
        Loader.instance().gateway().create(order, h);
        if (!h.wait(timeout, unit)) {
            Loader.instance().gateway().delete(order);
            if (!h.wait(timeout, unit)) {
                throw new TimeoutException("Delete order timeout.");
            }
            throw new TimeoutException("Create order timeout.");
        }
    }

    @Override
    public Date getTradingDay() {
        return Loader.instance().gateway().getTradingDay();
    }

    private class QdOrderHandler implements OrderHandler {

        private final CountDownLatch latch;
        private final Order od;

        QdOrderHandler(Order order) {
            od = order;
            latch = new CountDownLatch(1);
        }

        boolean wait(int timeout, TimeUnit unit) {
            try {
                return latch.await(timeout, unit);
            } catch (InterruptedException e) {
                LOG.write(e, this);
                return false;
            }
        }

        private void wake() {
            latch.countDown();
        }

        @Override
        public void onOrder(Order order) {
            Persistence.instance().insert(order);
            if (order.getStatus() != Order.Status.ACCEPTED) {
                wake();
            }
        }

        @Override
        public void onTrade(Trade trade) {
            Persistence.instance().insert(trade);
            adjustContracts(trade);
        }

        private void adjustContracts(Trade trade) {
            if (trade.getOffset() == Offset.OPEN) {
                addContracts(trade);
            } else {
                removeContracts(trade);
            }
        }

        private void removeContracts(Trade trade) {
            Persistence.instance().closeContract(trade.getInstrumentId(),
                    trade.getExchangeId(), opDirection(trade.getDirection()),
                    trade.getPrice(), trade.getQuantity(), trade.getUpdateTime());
        }

        private Direction opDirection(Direction direction) {
            return direction == Direction.BUY ? Direction.SELL : Direction.BUY;
        }

        private void addContracts(Trade trade) {
            var q = trade.getQuantity();
            while (q-- > 0) {
                Persistence.instance().insert(createContract(trade));
            }
        }

        private Contract createContract(Trade trade) {
            var c = new Contract();
            c.setContractId(TOOLS.nextId());
            c.setInstrumentId(trade.getInstrumentId());
            c.setExchangeId(trade.getExchangeId());
            c.setDirection(trade.getDirection());
            c.setOpenPrice(trade.getPrice());
            c.setTradingDay(trade.getTradingDay());
            c.setTradeId(trade.getTradeId());
            return c;
        }

        @Override
        public void onError(int code, String message) {
            od.setStatus(Order.Status.REJECTED);
            od.setStatusMessage("[" + code + "]" + message);
            onOrder(od);
        }
    }
}
