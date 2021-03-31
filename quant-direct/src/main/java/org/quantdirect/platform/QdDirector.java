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

import org.h2.tools.Server;
import org.quantdirect.Datafeed;
import org.quantdirect.Quanter;
import org.quantdirect.Gateway;
import org.quantdirect.loader.Loader;
import org.quantdirect.persistence.DbServer;
import org.quantdirect.tools.LOG;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class QdDirector extends Director {
    private final File base;
    private Status status;

    QdDirector(String baseDirectory) {
        status = Status.STOPPED;
        base = baseDir(baseDirectory);
        DbServer.start();
    }

    private File baseDir(String dir) {
        var f = new File(dir);
        if (!f.isDirectory()) {
            try {
                Files.createDirectories(f.toPath());
            } catch (IOException exception) {
                throw new Error("Can't create base dir: " + f.getAbsolutePath()  +".", exception);
            }
        }
        return f;
    }

    @Override
    public void addDirect(Quanter quanter) {
        Loader.instance().addDirect(quanter);
    }

    @Override
    public void removeDirect(Quanter quanter) {
        Loader.instance().removeDirect(quanter);
    }

    @Override
    public void setGateway(Gateway gateway) {
        Loader.instance().setGateway(gateway);
    }

    @Override
    public void setDatafeed(Datafeed datafeed) {
        Loader.instance().setDatafeed(datafeed);
    }

    @Override
    public void start() {
        try {
            Loader.instance().gateway().start(new QdGatewayListener());
            Loader.instance().datafeed().start(new QdDatafeedListener());
            status = Status.STATED;
        } catch (Throwable throwable) {
            status = Status.START_FAIL;
            LOG.write(throwable, this);
        }
    }

    @Override
    public void stop() {
        try {
            Loader.instance().datafeed().stop();
            Loader.instance().gateway().stop();
            status = Status.STOPPED;
        } catch (Throwable throwable) {
            status = Status.STOP_FAIL;
            LOG.write(throwable, this);
        }
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
