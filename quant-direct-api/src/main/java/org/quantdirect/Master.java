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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.logging.Logger;

public interface Master {
    Logger getLogger();

    Collection<ContractName> getContractNames();

    Collection<Contract> getContracts(String instrumentId, String exchangeId, Direction direction);

    long countOpenContracts(String instrumentId, String exchangeId, Direction direction, LocalDateTime before);

    long countContracts(String instrumentId, String exchangeId, Direction direction, LocalDateTime before);

    void setProperty(String key, String value);

    String getProperty(String key);
}
