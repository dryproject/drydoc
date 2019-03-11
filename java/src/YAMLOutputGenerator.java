/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

/** YAMLOutputGenerator */
public class YAMLOutputGenerator extends OutputGenerator {
  protected final ObjectMapper yamlMapper;

  YAMLOutputGenerator() {
    super();
    final YAMLFactory yamlFactory = new YAMLFactory();
    yamlFactory.enable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
    yamlFactory.enable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
    yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
    yamlFactory.enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS);
    yamlFactory.enable(YAMLGenerator.Feature.INDENT_ARRAYS);
    this.yamlMapper = new ObjectMapper(yamlFactory);
  }

  @Override
  protected void emit(final Record record) {
    try {
      System.out.println(yamlMapper.writeValueAsString(record));
    }
    catch (final JsonProcessingException error) {
      throw new RuntimeException(error);
    }
  }
}
