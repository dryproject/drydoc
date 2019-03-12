/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

/** ParameterRecord */
@JsonTypeName("!java/parameter")
public final class ParameterRecord extends Record {
  public final String type;

  ParameterRecord(final String name,
                  final String type,
                  final List<String> annotations,
                  final List<String> modifiers) {
    super(null, name, null, annotations, modifiers);
    assert(type != null);
    this.type = type;
  }
}
