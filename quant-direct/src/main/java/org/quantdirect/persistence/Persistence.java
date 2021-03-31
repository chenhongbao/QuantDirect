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

import java.time.LocalDateTime;
import java.util.Collection;

public abstract class Persistence {

    private static Persistence p;

    public static synchronized Persistence instance() {
        if (p == null) {
            p = new QdPersistence();
        }
        return p;
    }

    public abstract void insert(Trade trade);

    public abstract void insert(Order order);

    public abstract void insert(Contract contract);

    public abstract void removeContract(String instrumentId, String exchangeId,
            Direction direction, Offset offset, double price, long quantity);

    public abstract Collection<ContractName> getContractNames();

    public abstract Collection<Contract> getContracts(String instrumentId, String exchangeId, Direction direction);

    public abstract long countContracts(String instrumentId, String exchangeId, Direction direction, LocalDateTime before);
}