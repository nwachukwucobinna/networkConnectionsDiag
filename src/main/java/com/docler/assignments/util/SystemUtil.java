package com.docler.assignments.util;

import java.io.IOException;
import java.util.List;

public class SystemUtil {
    public static Process executeCommand(List<String> command) throws IOException {
        return new ProcessBuilder(command).start();
    }
}
