/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithModifiers;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/** OutputGenerator */
public abstract class OutputGenerator extends VoidVisitorAdapter<Void> {
  // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/visitor/VoidVisitor.html

  protected String comment;
  protected String packageName;
  protected List<String> className;

  protected abstract void emit(final Record record);

  protected abstract void emit(final Map<String, Object> map);

  @Override
  public void visit(final CompilationUnit node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/CompilationUnit.html

    super.visit(node, arg);
  }

  @Override
  public void visit(final JavadocComment node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/comments/JavadocComment.html

    this.comment = node.getContent();

    super.visit(node, arg);
  }

  @Override
  public void visit(final PackageDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/PackageDeclaration.html

    this.packageName = node.getName().asString();

    super.visit(node, arg);
  }

  @Override
  public void visit(final ClassOrInterfaceDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/ClassOrInterfaceDeclaration.html
    if (node.isPrivate()) {
      super.visit(node, arg);
      return;
    }

    final List<String> annotations = this.parseAnnotations(node);
    final List<String> modifiers = this.parseModifiers(node);

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

    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/type/TypeParameter.html
    final List<String> parameters = new ArrayList<String>();
    // TODO

    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/nodeTypes/NodeWithExtends.html
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/type/ClassOrInterfaceType.html
    final List<String> extends_ = new ArrayList<String>();
    for (final ClassOrInterfaceType extendedType : node.getExtendedTypes()) {
      extends_.add(extendedType.asString());
    }

    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/nodeTypes/NodeWithImplements.html
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/type/ClassOrInterfaceType.html
    final List<String> implements_ = new ArrayList<String>();
    for (final ClassOrInterfaceType implementedType : node.getImplementedTypes()) {
      implements_.add(implementedType.asString());
    }

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
  public void visit(final ConstructorDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/ConstructorDeclaration.html
    if (node.isPrivate()) {
      super.visit(node, arg);
      return;
    }

    super.visit(node, arg);
  }

  @Override
  public void visit(final FieldDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/FieldDeclaration.html
    if (node.isPrivate()) {
      super.visit(node, arg);
      return;
    }

    final List<String> annotations = this.parseAnnotations(node);
    final List<String> modifiers = this.parseModifiers(node);

    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/VariableDeclarator.html
    final Character separator = node.isStatic() ? '.' : '#';
    for (final VariableDeclarator variable : node.getVariables()) {
      final String name = variable.getName().asString();
      final String id = String.format("%s.%s%c%s", this.packageName, String.join(".", this.className), separator, name);
      final String type = variable.getTypeAsString();

      emit(new FieldRecord(id, name, this.comment, annotations, modifiers, type));
    }

    super.visit(node, arg);
  }

  @Override
  public void visit(final MethodDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/MethodDeclaration.html
    if (node.isPrivate()) {
      super.visit(node, arg);
      return;
    }

    final List<String> annotations = this.parseAnnotations(node);
    final List<String> modifiers = this.parseModifiers(node);

    final String name = node.getName().asString();
    final String id = this.makeMethodID(node);

    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/type/Type.html
    final String type = node.getTypeAsString();

    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/Parameter.html
    final List<ParameterRecord> parameters = new ArrayList<ParameterRecord>();
    for (final Parameter parameter : node.getParameters()) {
      final String paramName = parameter.getNameAsString();
      final String paramType = parameter.getTypeAsString();
      final List<String> paramAnnotations = this.parseAnnotations(parameter);
      final List<String> paramModifiers = this.parseModifiers(parameter);
      parameters.add(new ParameterRecord(paramName, paramType, paramAnnotations, paramModifiers));
    }

    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/type/ReferenceType.html
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

  protected String makeMethodID(final MethodDeclaration node) {
    final List<String> paramTypes = new ArrayList<String>();
    for (final Parameter parameter : node.getParameters()) {
      paramTypes.add(parameter.getTypeAsString());
    }
    final Character separator = node.isStatic() ? '.' : '#';
    return String.format("%s.%s%c%s(%s)",
      this.packageName,
      String.join(".", this.className),
      separator, node.getName().asString(),
      String.join(",", paramTypes));
  }

  protected List<String> parseAnnotations(final NodeWithAnnotations<?> node) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/nodeTypes/NodeWithAnnotations.html
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/expr/AnnotationExpr.html
    final List<String> result = new ArrayList<String>();
    for (final AnnotationExpr annotation : node.getAnnotations()) {
      result.add(annotation.toString());
    }
    return result;
  }

  protected List<String> parseModifiers(final NodeWithModifiers<?> node) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/nodeTypes/NodeWithModifiers.html
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/Modifier.html
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/Modifier.Keyword.html
    final List<String> result = new ArrayList<String>();
    for (final Modifier modifier : node.getModifiers()) {
      result.add(modifier.getKeyword().asString());
    }
    return result;
  }
}
