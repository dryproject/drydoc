/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import java.lang.reflect.Field;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;

/** PartialEvaluator */
public class PartialEvaluator {

  /** Partially evaluates constant field initializer expressions. */
  public Object evalExpression(final Expression expr) {
    assert(expr != null);

    if (expr.isLiteralExpr()) {
      return this.evalLiteralExpression(expr.asLiteralExpr());
    }
    if (expr.isUnaryExpr()) {
      return this.evalUnaryExpression(expr.asUnaryExpr());
    }
    if (expr.isBinaryExpr()) {
      return this.evalBinaryExpression(expr.asBinaryExpr());
    }
    if (expr.isFieldAccessExpr()) {
      return this.evalFieldAccessExpression(expr.asFieldAccessExpr());
    }
    if (expr.isMethodCallExpr()) {
      return this.evalMethodCallExpression(expr.asMethodCallExpr());
    }

    return null; // unable to evaluate expression
  }

  public Object evalLiteralExpression(final LiteralExpr expr) {
    assert(expr.isLiteralExpr());

    if (expr.isNullLiteralExpr()) return null;
    if (expr.isBooleanLiteralExpr()) return expr.asBooleanLiteralExpr().getValue();
    if (expr.isCharLiteralExpr()) return expr.asCharLiteralExpr().asChar();
    if (expr.isDoubleLiteralExpr()) return expr.asDoubleLiteralExpr().asDouble();
    if (expr.isIntegerLiteralExpr()) return expr.asIntegerLiteralExpr().asInt();
    if (expr.isLongLiteralExpr()) return expr.asLongLiteralExpr().asLong();
    if (expr.isStringLiteralExpr()) return expr.asStringLiteralExpr().asString();

    return null; // unknown literal type
  }

  public Object evalUnaryExpression(final UnaryExpr expr) {
    assert(expr.isUnaryExpr());

    // Evaluate unary expressions such as `-1`, `-2`, etc:
    switch (expr.getOperator()) {
      case PLUS: {
        final Expression subExpr = expr.getExpression();
        return this.evalExpression(subExpr);
      }

      case MINUS: {
        final Expression subExpr = expr.getExpression();
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

  public Object evalBinaryExpression(final BinaryExpr expr) {
    assert(expr.isBinaryExpr());

    switch (expr.getOperator()) {
      case LEFT_SHIFT: {
        return null; // TODO
      }

      default: {
        return null; // unable to evaluate expression
      }
    }
  }

  public Object evalFieldAccessExpression(final FieldAccessExpr expr) {
    assert(expr.isFieldAccessExpr());

    final Expression scope = expr.getScope();
    if (!scope.isNameExpr()) return null; // dynamic field access not supported
    final String className = scope.asNameExpr().getNameAsString();
    final String fieldName = expr.getNameAsString();
    switch (className) {
      case "Boolean":
      case "Byte":
      case "Character":
      case "Double":
      case "Float":
      case "Integer":
      case "Long":
      case "Math":
      case "Number":
      case "Short":
      case "String":
        try {
          final Class<?> class_ = Class.forName("java.lang." + className);
          final Field field = class_.getField(fieldName);
          return field.get(null);
        }
        catch (final ClassNotFoundException error) {
          assert(false); // unreachable
          return null; // unreachable
        }
        catch (final NoSuchFieldException error) {
          return null; // unknown field access
        }
        catch (final IllegalAccessException error) {
          assert(false); // unreachable
          return null; // unreachable
        }
      default:
        return null; // unknown field access
    }
  }

  public Object evalMethodCallExpression(final MethodCallExpr expr) {
    assert(expr.isMethodCallExpr());

    // TODO: parse `UUID.fromString("...")`

    return null; // unable to evaluate expression
  }
}
