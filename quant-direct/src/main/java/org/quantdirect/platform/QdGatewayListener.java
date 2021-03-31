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

import org.quantdirect.Gateway;
import org.quantdirect.GatewayListener;
import org.quantdirect.messager.Messager;

class QdGatewayListener implements GatewayListener {
    @Override
    public void onStart() {
        Messager.instance().send("Gateway starts.", this);
    }

    @Override
    public void onOpen() {
        Messager.instance().send("Gateway opens.", this);
    }

    @Override
    public void onClose() {
        Messager.instance().send("Gateway closes.", this);
    }

    @Override
    public void onStop() {
        Messager.instance().send("Gateway stops.", this);
    }
}
