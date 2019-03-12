/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** OutputGenerator */
public abstract class OutputGenerator extends VoidVisitorAdapter<Void> {
  protected String comment;
  protected String packageName;
  protected List<String> className;

  protected abstract void emit(final Record record);

  protected abstract void emit(final Map<String, Object> map);

  @Override
  public void visit(final CompilationUnit node, final Void arg) {
    super.visit(node, arg);
  }

  @Override
  public void visit(final JavadocComment node, final Void arg) {
    this.comment = node.getContent();

    super.visit(node, arg);
  }

  @Override
  public void visit(final PackageDeclaration node, final Void arg) {
    this.packageName = node.getName().asString();

    super.visit(node, arg);
  }

  //@SuppressWarnings("unchecked")
  @Override
  public void visit(final ClassOrInterfaceDeclaration node, final Void arg) {
    final String name = node.getName().asString();

    this.className = new ArrayList<String>(Arrays.asList(name));
    Optional<Node> parent = node.getParentNode();
    while (parent.isPresent()) {
      final Node parentNode = parent.get();
      if (parentNode.getClass().equals(ClassOrInterfaceDeclaration.class)) {
        final ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration)parentNode;
        this.className.add(0, parentClass.getName().asString());
      }
      parent = parentNode.getParentNode();
    }

    final String id = String.format("%s.%s", this.packageName, String.join(".", this.className));

    final List<String> annotations = new ArrayList<String>();
    // TODO

    final List<String> modifiers = new ArrayList<String>();
    if (node.isPublic()) modifiers.add("public");
    if (node.isPrivate()) modifiers.add("private");
    if (node.isProtected()) modifiers.add("protected");
    if (node.isAbstract()) modifiers.add("abstract");
    if (node.isStatic()) modifiers.add("static");
    if (node.isFinal()) modifiers.add("final");
    if (node.isStrictfp()) modifiers.add("strictfp");

    final List<String> parameters = new ArrayList<String>();
    // TODO

    final List<String> extends_ = new ArrayList<String>();
    // TODO

    final List<String> implements_ = new ArrayList<String>();
    // TODO

    emit(node.isInterface() ?
      new InterfaceRecord(
        id,
        name,
        this.comment,
        annotations,
        modifiers,
        parameters,
        extends_
      ) :
      new ClassRecord(
        id,
        name,
        this.comment,
        annotations,
        modifiers,
        parameters,
        extends_,
        implements_
      )
    );

    super.visit(node, arg);
  }

  @Override
  public void visit(final MethodDeclaration node, final Void arg) {
    final String name = node.getName().asString();
    final String id = String.format("%s.%s.%s", this.packageName, String.join(".", this.className), name);

    final List<String> annotations = new ArrayList<String>();
    // TODO

    final String type = node.getTypeAsString();

    final List<String> modifiers = new ArrayList<String>();
    if (node.isPublic()) modifiers.add("public");
    if (node.isPrivate()) modifiers.add("private");
    if (node.isProtected()) modifiers.add("protected");
    if (node.isAbstract()) modifiers.add("abstract");
    if (node.isStatic()) modifiers.add("static");
    if (node.isFinal()) modifiers.add("final");
    if (node.isDefault()) modifiers.add("default");
    if (node.isSynchronized()) modifiers.add("synchronized");
    if (node.isNative()) modifiers.add("native");
    if (node.isStrictfp()) modifiers.add("strictfp");

    final List<ParameterRecord> parameters = new ArrayList<ParameterRecord>();
    for (final Parameter parameter : node.getParameters()) {
      final String paramName = parameter.getNameAsString();
      final String paramType = parameter.getTypeAsString();
      parameters.add(new ParameterRecord(paramName, paramType, null, null)); // TODO
    }

    final List<String> exceptions = new ArrayList<String>();
    for (final ReferenceType exception : node.getThrownExceptions()) {
      exceptions.add(exception.asString());
    }

    emit(new MethodRecord(
      id,
      name,
      this.comment,
      annotations,
      modifiers,
      type,
      parameters,
      exceptions
    ));

    super.visit(node, arg);
  }
}
