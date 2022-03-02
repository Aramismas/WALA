package com.ibm.wala.util.io;

import com.ibm.wala.util.PlatformUtil;
import com.ibm.wala.util.collections.ArrayIterator;
import com.ibm.wala.util.collections.FilterIterator;
import com.ibm.wala.util.collections.MapIterator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.jar.JarFile;

public class RtJar {

  public static JarFile getRtJar(Iterator<JarFile> x) {
    while (x.hasNext()) {
      JarFile JF = x.next();
      switch (Paths.get(JF.getName()).getFileName().toString()) {
        case "core.jar":
        case "java.base.jmod":
        case "rt.jar":
          return JF;
        case "classes.jar":
          if (PlatformUtil.onMacOSX()) return JF;
          // $FALL-THROUGH$
        default:
      }
    }

    return null;
  }

  public static void main(String[] args) throws IOException {
    JarFile rt =
        getRtJar(
            new MapIterator<>(
                new FilterIterator<>(
                    new ArrayIterator<>(
                        System.getProperty("sun.boot.class.path").split(File.pathSeparator)),
                    t -> t.endsWith(".jar")),
                object -> {
                  try {
                    return new JarFile(object); // Method closed not invoked on new JarFile. False Positive
                  } catch (IOException e) {
                    assert false : e.toString();
                    return null;
                  }
                }));
    rt.close(); // Edited TP
    System.err.println(rt.getName());
  }
}
