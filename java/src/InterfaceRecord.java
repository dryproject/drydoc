/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;

/** InterfaceRecord */
@JsonTypeName("!java/interface")
public final class InterfaceRecord extends Record {
  public final String id;
  public final String name;

  InterfaceRecord(final String comment, final String id, final String name) {
    super(comment);
    this.id = id;
    this.name = name;
  }
}
