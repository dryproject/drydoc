/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/** InterfaceRecord */
@JsonTypeName("!java/interface")
public final class InterfaceRecord extends Record {
  public final List<String> parameters;

  @JsonProperty("extends")
  public final List<String> extends_;

  InterfaceRecord(final String id,
                  final String name,
                  final String comment,
                  final List<String> annotations,
                  final List<String> modifiers,
                  final List<String> parameters,
                  final List<String> extends_) {
    super(id, name, comment, annotations, modifiers);
    this.parameters = (parameters != null && !parameters.isEmpty()) ? parameters : null;
    this.extends_ = (extends_ != null && !extends_.isEmpty()) ? extends_ : null;
  }
}
