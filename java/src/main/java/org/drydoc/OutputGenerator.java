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
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithModifiers;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/** OutputGenerator */
public abstract class OutputGenerator extends VoidVisitorAdapter<Void> {
  // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/visitor/VoidVisitor.html

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
  public void visit(final PackageDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/PackageDeclaration.html

    this.packageName = node.getName().asString();

    super.visit(node, arg);
  }

  @Override
  public void visit(final ClassOrInterfaceDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/ClassOrInterfaceDeclaration.html
    if (node.isPrivate()) return;
    if (node.isLocalClassDeclaration()) return;
    if (!node.isTopLevelType()) return; // TODO: check this

    final String comment = this.parseComment(node);
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
        comment,
        annotations,
        modifiers,
        parameters,
        extends_
      ) :
      new ClassRecord(
        id,
        name,
        comment,
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
    if (node.isPrivate()) return;

    super.visit(node, arg);
  }

  @Override
  public void visit(final FieldDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/FieldDeclaration.html
    if (node.isPrivate()) return;

    final String comment = this.parseComment(node);
    final List<String> annotations = this.parseAnnotations(node);
    final List<String> modifiers = this.parseModifiers(node);

    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/VariableDeclarator.html
    final Character separator = node.isStatic() ? '.' : '#';
    for (final VariableDeclarator variable : node.getVariables()) {
      final String name = variable.getName().asString();
      final String id = String.format("%s.%s%c%s", this.packageName, String.join(".", this.className), separator, name);
      final String type = variable.getTypeAsString();
      final Object value = variable.getInitializer().isPresent() ? this.evalExpression(variable.getInitializer().get()) : null;

      emit(new FieldRecord(id, name, comment, annotations, modifiers, type, value));
    }

    super.visit(node, arg);
  }

  @Override
  public void visit(final MethodDeclaration node, final Void arg) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/body/MethodDeclaration.html
    if (node.isPrivate()) return;

    final String comment = this.parseComment(node);
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
      comment,
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

  protected String parseComment(final Node node) {
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/Node.html
    // See: https://static.javadoc.io/com.github.javaparser/javaparser-core/3.13.3/com/github/javaparser/ast/comments/Comment.html
    final Comment comment = node.getComment().orElse(null);
    return (comment != null) ? comment.getContent() : null;
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

  /** Partially evaluates constant field initializer expressions. */
  Object evalExpression(final Expression expr) {
    if (expr.isLiteralExpr()) {
      if (expr.isNullLiteralExpr()) return null;
      if (expr.isBooleanLiteralExpr()) return expr.asBooleanLiteralExpr().getValue();
      if (expr.isCharLiteralExpr()) return expr.asCharLiteralExpr().asChar();
      if (expr.isDoubleLiteralExpr()) return expr.asDoubleLiteralExpr().asDouble();
      if (expr.isIntegerLiteralExpr()) return expr.asIntegerLiteralExpr().asInt();
      if (expr.isLongLiteralExpr()) return expr.asLongLiteralExpr().asLong();
      if (expr.isStringLiteralExpr()) return expr.asStringLiteralExpr().asString();
      return null; // unknown literal type
    }

    if (expr.isUnaryExpr()) {
      // Evaluate unary expressions such as `-1`, `-2`, etc:
      switch (expr.asUnaryExpr().getOperator()) {
        case PLUS: {
          final Expression subExpr = expr.asUnaryExpr().getExpression();
          return this.evalExpression(subExpr);
        }
        case MINUS: {
          final Expression subExpr = expr.asUnaryExpr().getExpression();
          if (subExpr.isDoubleLiteralExpr()) return -1.0 * subExpr.asDoubleLiteralExpr().asDouble();
          if (subExpr.isIntegerLiteralExpr()) return -1 * subExpr.asIntegerLiteralExpr().asInt();
          if (subExpr.isLongLiteralExpr()) return -1L * subExpr.asLongLiteralExpr().asLong();
          return null; // unable to evaluate expression
        }
        default: {
          return null; // unable to evaluate expression
        }
      }
    }

    if (expr.isFieldAccessExpr()) {
      // TODO: reimplement this using reflection.
      switch (expr.toString()) {
        case "Byte.BYTES":               return Byte.BYTES;
        case "Byte.MAX_VALUE":           return Byte.MAX_VALUE;
        case "Byte.MIN_VALUE":           return Byte.MIN_VALUE;
        case "Byte.SIZE":                return Byte.SIZE;
        case "Character.MAX_CODE_POINT": return Character.MAX_CODE_POINT;
        case "Character.MAX_VALUE":      return Character.MAX_VALUE;
        case "Character.MIN_CODE_POINT": return Character.MIN_CODE_POINT;
        case "Character.MIN_VALUE":      return Character.MIN_VALUE;
        case "Integer.BYTES":            return Integer.BYTES;
        case "Integer.MAX_VALUE":        return Integer.MAX_VALUE;
        case "Integer.MIN_VALUE":        return Integer.MIN_VALUE;
        case "Integer.SIZE":             return Integer.SIZE;
        case "Long.BYTES":               return Long.BYTES;
        case "Long.MAX_VALUE":           return Long.MAX_VALUE;
        case "Long.MIN_VALUE":           return Long.MIN_VALUE;
        case "Long.SIZE":                return Long.SIZE;
        case "Short.BYTES":              return Short.BYTES;
        case "Short.MAX_VALUE":          return Short.MAX_VALUE;
        case "Short.MIN_VALUE":          return Short.MIN_VALUE;
        case "Short.SIZE":               return Short.SIZE;
        default: return null; // unknown field access
      }
    }

    if (expr.isMethodCallExpr()) {
      // TODO: parse `UUID.fromString("...")`
    }

    return null; // unable to evaluate expression
  }
}
