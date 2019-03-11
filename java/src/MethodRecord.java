/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;

/** MethodRecord */
@JsonTypeName("!java/method")
public class MethodRecord implements Record {
  public final String id;
  public final String name;

  MethodRecord(final String id, final String name) {
    this.id = id;
    this.name = name;
  }
}
