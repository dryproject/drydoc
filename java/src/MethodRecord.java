/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;

/** MethodRecord */
@JsonTypeName("!java/method")
public final class MethodRecord extends Record {
  public final String id;
  public final String name;

  MethodRecord(final String comment, final String id, final String name) {
    super(comment);
    this.id = id;
    this.name = name;
  }
}
