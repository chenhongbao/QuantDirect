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

import org.quantdirect.Candle;
import org.quantdirect.Feed;
import org.quantdirect.MarketHandler;
import org.quantdirect.Tick;
import org.quantdirect.loader.Loader;
import org.quantdirect.messager.Messager;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

class QdFeed implements Feed {

    private final QdMarketHandler h;

    QdFeed() {
        h = new QdMarketHandler();
    }

    @Override
    public void subscribe(String instrumentId, MarketHandler handler) throws IOException {
        if (instrumentId == null || handler == null) {
            return;
        }
        if (!h.contains(instrumentId)) {
            Loader.instance().datafeed().subscribe(instrumentId, h);
        }
        h.subscribe(instrumentId, handler);
    }

    @Override
    public void unsubscribe(String instrumentId) throws IOException {
        if (instrumentId == null) {
            return;
        }
        if (!h.contains(instrumentId)) {
            return;
        }
        Loader.instance().datafeed().unsubscribe(instrumentId);
        h.remove(instrumentId);
    }

    @Override
    public void unsubscribe(String instrumentId, MarketHandler handler) throws IOException {
        if (instrumentId == null || handler == null) {
            return;
        }
        h.remove(instrumentId, handler);
        if (!h.contains(instrumentId)) {
            Loader.instance().datafeed().unsubscribe(instrumentId);
        }
    }

    @Override
    public Date getTradingDay() {
        return Loader.instance().datafeed().getTradingDay();
    }

    private class QdMarketHandler implements MarketHandler {

        private final Map<String, Set<MarketHandler>> sub;

        QdMarketHandler() {
            sub = new ConcurrentHashMap<>();
        }

        void subscribe(String instrumentId, MarketHandler handler) {
            var s = sub.computeIfAbsent(instrumentId, k -> new ConcurrentSkipListSet<>());
            s.add(handler);
        }

        void remove(String instrumentId) {
            sub.remove(instrumentId);
        }

        void remove(String instrumentId, MarketHandler handler) {
            var s = sub.get(instrumentId);
            if (s != null) {
                s.remove(handler);
                if (s.isEmpty()) {
                    sub.remove(instrumentId);
                }
            }
        }

        boolean contains(String instrumentId) {
            return sub.containsKey(instrumentId);
        }

        @Override
        public void onTick(Tick tick) {
            final QdMarketHandler self = this;
            final var s = sub.get(tick.getInstrumentId());
            if (s != null) {
                s.stream().parallel().forEach(h -> {
                    try {
                        h.onTick(tick);
                    } catch (Throwable throwable) {
                        Messager.instance().send(throwable, self);
                    }
                });
            }
        }

        @Override
        public void onCandle(Candle candle) {
            final QdMarketHandler self = this;
            final var s = sub.get(candle.getInstrumentId());
            if (s != null) {
                s.stream().parallel().forEach(h -> {
                    try {
                        h.onCandle(candle);
                    } catch (Throwable throwable) {
                        Messager.instance().send(throwable, self);
                    }
                });
            }
        }

        @Override
        public void onError(int code, String message) {
            final QdMarketHandler self = this;
            sub.values().stream().parallel().forEach(s -> {
                s.stream().parallel().forEach(h -> {
                    try {
                        h.onError(code, message);
                    } catch (Throwable throwable) {
                        Messager.instance().send(throwable, self);
                    }
                });
            });
        }
    }
}
