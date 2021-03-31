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

package org.quantdirect;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Tick {
    private String tickId;
    private String instrumentId;
    private String exchangeId;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double settlePrice;
    private double preSettlePrice;
    private double askPrice;
    private double bidPrice;
    private long askVolume;
    private long bidVolume;
    private long tradeVolume;
    private long openInterest;
    private LocalDate tradingDay;
    private LocalDateTime updateTime;

    public Tick() {
    }

    public String getTickId() {
        return tickId;
    }

    public void setTickId(String tickId) {
        this.tickId = tickId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(double settlePrice) {
        this.settlePrice = settlePrice;
    }

    public double getPreSettlePrice() {
        return preSettlePrice;
    }

    public void setPreSettlePrice(double preSettlePrice) {
        this.preSettlePrice = preSettlePrice;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public long getAskVolume() {
        return askVolume;
    }

    public void setAskVolume(long askVolume) {
        this.askVolume = askVolume;
    }

    public long getBidVolume() {
        return bidVolume;
    }

    public void setBidVolume(long bidVolume) {
        this.bidVolume = bidVolume;
    }

    public long getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(long tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public long getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(long openInterest) {
        this.openInterest = openInterest;
    }

    public LocalDate getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(LocalDate tradingDay) {
        this.tradingDay = tradingDay;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
