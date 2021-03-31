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

package org.quantdirect.loader;

import org.quantdirect.Datafeed;
import org.quantdirect.Quanter;
import org.quantdirect.Gateway;

import javax.management.ServiceNotFoundException;
import java.io.File;
import java.util.HashSet;
import java.util.ServiceConfigurationError;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

class QdLoader extends Loader {

    private final Set<DirectConfig> d;
    private File gwFile;
    private File dfFile;
    private String gwClazz;
    private String dfClazz;
    private Gateway gw;
    private Datafeed df;

    QdLoader() {
        d = new ConcurrentSkipListSet<>();
    }

    @Override
    public void setGateway(File file, String className) {
        if (file.equals(gwFile) && className.equals(gwClazz)) {
            return;
        } else {
            loadGateway(file, className);
        }
    }

    @Override
    public void setDatafeed(File file, String className) {
        if (file.equals(dfFile) && className.equals(dfClazz)) {
            return;
        } else {
            loadGateway(file, className);
        }
    }

    @Override
    public void addDirect(File file, String className) {
        var x = new DirectConfig(null, file, className);
        if (d.contains(x)) {
            return;
        } else {
            var direct = loadDirect(file, className);
            d.add(new DirectConfig(direct, file, className));
        }
    }

    @Override
    public void removeDirect(File file, String className) {
        var x = new DirectConfig(null, file, className);
        d.remove(x);
    }

    @Override
    public void setGateway(Gateway gateway) {
        gw = gateway;
    }

    @Override
    public void setDatafeed(Datafeed datafeed) {
        df = datafeed;
    }

    @Override
    public void addDirect(Quanter quanter) {
        var x = new DirectConfig(quanter, null, null);
        d.add(x);
    }

    @Override
    public void removeDirect(Quanter quanter) {
        var x = new DirectConfig(quanter, null, null);
        d.remove(x);
    }

    @Override
    public Gateway gateway() {
        if (gw == null) {
            throw new ServiceConfigurationError("No gateway service.");
        }
        return gw;
    }

    @Override
    public Datafeed datafeed() {
        if (df == null) {
            throw new ServiceConfigurationError("No datafeed service.");
        }
        return df;
    }

    @Override
    public Set<Quanter> copyDirects() {
        Set<Quanter> r = new HashSet<>();
        d.forEach(c -> {
            if (c.direct() != null) {
                r.add(c.quanter);
            }
        });
        return r;
    }

    private void loadGateway(File file, String className) {
        gwFile = file;
        gwClazz = className;
        try {
            gw = ServiceSelector.selectService(Gateway.class, gwClazz, gwFile, file);
        } catch (ServiceNotFoundException e) {
            throw new Error("Can't load gateway service " + className + " from file "
                            + file.getAbsolutePath() + ".");
        }
    }

    private void loadDatafeed(File file, String className) {
        dfFile = file;
        dfClazz = className;
        try {
            df = ServiceSelector.selectService(Datafeed.class, dfClazz, dfFile, file);
        } catch (ServiceNotFoundException e) {
            throw new Error("Can't load datafeed service " + className + " from file "
                            + file.getAbsolutePath() + ".");
        }
    }

    private Quanter loadDirect(File file, String className) {
        try {
            return ServiceSelector.selectService(Quanter.class, className, file);
        } catch (ServiceNotFoundException e) {
            throw new Error("Can't load direct service " + className + " from file "
                            + file.getAbsolutePath() + ".");
        }
    }

    private class DirectConfig {

        private final Quanter quanter;
        private final File file;
        private final String clz;

        DirectConfig(Quanter quanter, File file, String clazz) {
            this.quanter = quanter;
            this.file = file;
            this.clz = clazz;
        }


        Quanter direct() {
            return quanter;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof DirectConfig) {
                var c = (DirectConfig) o;
                if (c.clz != null && c.file != null) {
                    return (c.clz.equals(this.clz) && c.file.equals(this.file));
                } else {
                    return c.quanter == this.quanter;
                }
            }
            return false;
        }
    }
}
