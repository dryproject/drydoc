/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/** Record */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({ @JsonSubTypes.Type(ClassRecord.class), @JsonSubTypes.Type(MethodRecord.class) })
public abstract class Record {
  @JsonInclude(Include.NON_NULL)
  public String comment;

  Record(final String comment) {
    this.comment = comment;
  }
}
