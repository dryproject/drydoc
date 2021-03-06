/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** FieldRecord */
@JsonTypeName("!java/field")
public final class FieldRecord extends Record {
  public final String type;

  @JsonInclude(Include.NON_NULL)
  public final Object value;

  FieldRecord(final String id,
              final String name,
              final String comment,
              final List<String> annotations,
              final List<String> modifiers,
              final String type,
              final Object value) {
    super(id, name, comment, annotations, modifiers);
    assert(type != null);
    this.type = type;
    this.value = value;
  }
}
