/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Record */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@JsonSubTypes.Type(ClassRecord.class),
  @JsonSubTypes.Type(FieldRecord.class),
  @JsonSubTypes.Type(InterfaceRecord.class),
  @JsonSubTypes.Type(MethodRecord.class),
  @JsonSubTypes.Type(ParameterRecord.class),
})
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
    assert(name != null);
    this.id = id;
    this.name = name;
    this.comment = comment;
    this.annotations = (annotations != null && !annotations.isEmpty()) ? annotations : null;
    this.modifiers = (modifiers != null && !modifiers.isEmpty()) ? modifiers : null;
  }
}
