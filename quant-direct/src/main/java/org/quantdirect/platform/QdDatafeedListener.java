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
import org.quantdirect.messager.Messager;

class QdDatafeedListener implements DatafeedListener {

    QdDatafeedListener() {
    }

    @Override
    public void onStart() {
        Messager.instance().send("Datafeed starts.", this);
        callStart();
    }

    @Override
    public void onOpen() {
        Messager.instance().send("Datafeed opens.", this);
        callOpen();
    }

    @Override
    public void onClose() {
        Messager.instance().send("Datafeed closes.", this);
        callClose();
    }

    @Override
    public void onStop() {
        Messager.instance().send("Datafeed stops.", this);
        callStop();
    }

    void callStart() {
        final QdDatafeedListener k = this;
        Loader.instance().copyDirects().stream().parallel().forEach(direct -> {
            try {
                direct.onStart(QdPosition.instance());
            } catch (Throwable throwable) {
                Messager.instance().send(throwable, k);
            }
        });
    }

    void callOpen() {
        final QdDatafeedListener k = this;
        final Transaction tr = new QdTransaction();
        final Feed fd = new QdFeed();
        Loader.instance().copyDirects().stream().parallel().forEach(direct -> {
            try {
                direct.onOpen(tr, fd);
            } catch (Throwable throwable) {
                Messager.instance().send(throwable, k);
            }
        });
    }

    void callClose() {
        final QdDatafeedListener k = this;
        Loader.instance().copyDirects().stream().parallel().forEach(direct -> {
            try {
                direct.onClose();
            } catch (Throwable throwable) {
                Messager.instance().send(throwable, k);
            }
        });
    }

    void callStop() {
        final QdDatafeedListener k = this;
        Loader.instance().copyDirects().stream().parallel().forEach(direct -> {
            try {
                direct.onStop();
            } catch (Throwable throwable) {
                Messager.instance().send(throwable, k);
            }
        });
    }
}
