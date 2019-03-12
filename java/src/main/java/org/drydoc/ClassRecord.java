/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/** ClassRecord */
@JsonTypeName("!java/class")
public final class ClassRecord extends Record {
  @JsonInclude(Include.NON_NULL)
  public final List<String> parameters;

  @JsonProperty("extends")
  @JsonInclude(Include.NON_NULL)
  public final List<String> extends_;

  @JsonProperty("implements")
  @JsonInclude(Include.NON_NULL)
  public final List<String> implements_;

  ClassRecord(final String id,
              final String name,
              final String comment,
              final List<String> annotations,
              final List<String> modifiers,
              final List<String> parameters,
              final List<String> extends_,
              final List<String> implements_) {
    super(id, name, comment, annotations, modifiers);
    this.parameters = (parameters != null && !parameters.isEmpty()) ? parameters : null;
    this.extends_ = (extends_ != null && !extends_.isEmpty()) ? extends_ : null;
    this.implements_ = (implements_ != null && !implements_.isEmpty()) ? implements_ : null;
  }
}
