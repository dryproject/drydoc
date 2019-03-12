/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/** MethodRecord */
@JsonTypeName("!java/method")
public final class MethodRecord extends Record {
  public final String type;

  @JsonInclude(Include.NON_NULL)
  public final List<ParameterRecord> parameters;

  @JsonProperty("throws")
  @JsonInclude(Include.NON_NULL)
  public final List<String> throws_;

  MethodRecord(final String id,
               final String name,
               final String comment,
               final List<String> annotations,
               final List<String> modifiers,
               final String type,
               final List<ParameterRecord> parameters,
               final List<String> throws_) {
    super(id, name, comment, annotations, modifiers);
    this.type = type;
    this.parameters = (parameters != null && !parameters.isEmpty()) ? parameters : null;
    this.throws_ = (throws_ != null && !throws_.isEmpty()) ? throws_ : null;
  }
}
