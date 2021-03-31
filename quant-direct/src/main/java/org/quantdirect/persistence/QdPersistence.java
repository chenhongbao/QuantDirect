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

class QdPersistence extends Persistence {
    @Override
    public void insert(Trade trade) {

    }

    @Override
    public void insert(Order order) {

    }

    @Override
    public void insert(Contract contract) {

    }

    @Override
    public void removeContract(String instrumentId, String exchangeId, Direction direction,
            Offset offset, double price, long quantity) {

    }

    @Override
    public Collection<ContractName> getContractNames() {
        return null;
    }

    @Override
    public Collection<Contract> getContracts(String instrumentId, String exchangeId, Direction direction) {
        return null;
    }

    @Override
    public long countContracts(String instrumentId, String exchangeId, Direction direction, LocalDateTime before) {
        return 0;
    }
}
