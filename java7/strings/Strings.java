// Copyright (c) 2014 Daniel S. Dickstein

package library.java7.strings;

import java.util.Collection;

public class Strings {
  public static String fill(String padding, int nTimes) {
    if (nTimes <= 0) {
      return "";
    } else {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < nTimes; i++) {
        builder.append(padding);
      }
      return builder.toString();
    }
  }

  public static String join(Collection<Object> collection, String separator) {
    return join(collection.toArray(), separator);
  }

  public static String join(Object[] arr, String separator) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < arr.length; i++) {
      builder.append(arr[i]);
      if (i < arr.length - 1) {
        builder.append(separator);
      }
    }
    return builder.toString();
  }
}