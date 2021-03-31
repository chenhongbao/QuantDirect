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

package org.quantdirect.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public abstract class LOG {
    public static void write(String message, Object source) {
        try (PrintWriter pw = pw()) {
            write(pw, source);
            pw.write(message + "\n");
            pw.flush();
        } catch (IOException exception) {
            throw new Error("Can't create log file.", exception);
        }
    }

    public static void write(Throwable throwable, Object source) {
        try (PrintWriter pw = pw()) {
            write(pw, source);
            throwable.printStackTrace(pw);
            pw.write("\n");
            pw.flush();
        } catch (IOException exception) {
            throw new Error("Can't create log file.", exception);
        }
    }

    private static PrintWriter pw() throws IOException {
        return new PrintWriter(new FileWriter("QuantDirect.Messager.log", true));
    }

    private static void write(PrintWriter pw, Object source) {
        pw.write("[" + LocalDateTime.now() + "]");
        pw.write("[" + source + "]\n");
    }
}
