/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/** Record */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({ @JsonSubTypes.Type(ClassRecord.class), @JsonSubTypes.Type(MethodRecord.class) })
public abstract class Record {
  @JsonInclude(Include.NON_NULL)
  public final String id;

  public final String name;

  @JsonInclude(Include.NON_NULL)
  public String comment;

  @JsonInclude(Include.NON_NULL)
  public final List<String> annotations;

  @JsonInclude(Include.NON_NULL)
  public final List<String> modifiers;

  Record(final String id,
         final String name,
         final String comment,
         final List<String> annotations,
         final List<String> modifiers) {
    this.id = id;
    this.name = name;
    this.comment = comment;
    this.annotations = (annotations != null && !annotations.isEmpty()) ? annotations : null;
    this.modifiers = (modifiers != null && !modifiers.isEmpty()) ? modifiers : null;
  }
}
