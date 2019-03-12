/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

/** ParameterRecord */
@JsonTypeName("!java/parameter")
public final class ParameterRecord extends Record {
  public final String type;

  ParameterRecord(final String name,
                  final String type,
                  final List<String> annotations,
                  final List<String> modifiers) {
    super(null, name, null, annotations, modifiers);
    this.type = type;
  }
}
