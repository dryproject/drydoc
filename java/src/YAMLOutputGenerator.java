/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** YAMLOutputGenerator */
public class YAMLOutputGenerator extends VoidVisitorAdapter<Void> {
  private final ObjectMapper yamlMapper;
  private String packageName;
  private List<String> className;

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
  public void visit(final PackageDeclaration node, final Void arg) {
    this.packageName = node.getName().asString();

    super.visit(node, arg);
  }

  private void emit(final Record record) {
    try {
      System.out.println(yamlMapper.writeValueAsString(record));
    }
    catch (final JsonProcessingException error) {
      throw new RuntimeException(error);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void visit(final ClassOrInterfaceDeclaration node, final Void arg) {
    this.className = new ArrayList(Arrays.asList(node.getName().asString()));

    Optional<Node> parent = node.getParentNode();
    while (parent.isPresent()) {
      final Node parentNode = parent.get();
      if (parentNode.getClass().equals(ClassOrInterfaceDeclaration.class)) {
        final ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration)parentNode;
        this.className.add(0, parentClass.getName().asString());
      }
      parent = parentNode.getParentNode();
    }

    emit(new ClassRecord(
      String.format("%s.%s", this.packageName, String.join(".", this.className)),
      node.getName().asString()
    ));

    super.visit(node, arg);
  }

  @Override
  public void visit(final MethodDeclaration node, final Void arg) {
    emit(new MethodRecord(
      String.format("%s.%s.%s", this.packageName, String.join(".", this.className), node.getName().asString()),
      node.getName().asString()
    ));

    super.visit(node, arg);
  }
}
