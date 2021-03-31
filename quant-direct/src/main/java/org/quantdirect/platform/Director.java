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

import org.quantdirect.Datafeed;
import org.quantdirect.Gateway;
import org.quantdirect.Quanter;

public abstract class Director {

    private static Director director;

    public static synchronized Director create(String baseDirectory) {
        if (director == null) {
            director = new QdDirector(baseDirectory);
        }
        return director;
    }

    public abstract void addDirect(Quanter quanter);

    public abstract void removeDirect(Quanter quanter);

    public abstract void setGateway(Gateway gateway);

    public abstract void setDatafeed(Datafeed datafeed);

    public abstract void start();

    public abstract void stop();

    public abstract Status getStatus();

    public enum Status {
        START_FAIL, STATED, STOP_FAIL, STOPPED
    }
}
