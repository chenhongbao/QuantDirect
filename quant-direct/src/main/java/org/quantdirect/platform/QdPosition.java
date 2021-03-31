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

import org.quantdirect.Contract;
import org.quantdirect.ContractName;
import org.quantdirect.Direction;
import org.quantdirect.Position;
import org.quantdirect.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.Collection;

class QdPosition implements Position {

    private static Position p;

    static synchronized Position instance() {
        if (p == null) {
            p = new QdPosition();
        }
        return p;
    }

    @Override
    public Collection<ContractName> getContractNames() {
        return Persistence.instance().getContractNames();
    }

    @Override
    public Collection<Contract> getContracts(String instrumentId, String exchangeId, Direction direction) {
        return Persistence.instance().getContracts(instrumentId, exchangeId, direction);
    }

    @Override
    public long countContracts(String instrumentId, String exchangeId, Direction direction, LocalDateTime before) {
        return Persistence.instance().countContracts(instrumentId, exchangeId, direction, before);
    }
}
