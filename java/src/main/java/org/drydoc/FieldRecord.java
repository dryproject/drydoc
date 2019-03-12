/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

/** FieldRecord */
@JsonTypeName("!java/field")
public final class FieldRecord extends Record {
  public final String type;

  FieldRecord(final String id,
              final String name,
              final String comment,
              final List<String> annotations,
              final List<String> modifiers,
              final String type) {
    super(id, name, comment, annotations, modifiers);
    assert(type != null);
    this.type = type;
  }
}
