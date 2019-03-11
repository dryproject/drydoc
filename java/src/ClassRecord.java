/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;

/** ClassRecord */
@JsonTypeName("!java/class")
public class ClassRecord implements Record {
  public final String id;
  public final String name;

  ClassRecord(final String id, final String name) {
    this.id = id;
    this.name = name;
  }
}
