/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;

/** ClassRecord */
@JsonTypeName("!java/class")
public final class ClassRecord extends Record {
  public final String id;
  public final String name;

  ClassRecord(final String comment, final String id, final String name) {
    super(comment);
    this.id = id;
    this.name = name;
  }
}
