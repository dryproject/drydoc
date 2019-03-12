/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
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

/** OutputGenerator */
public abstract class OutputGenerator extends VoidVisitorAdapter<Void> {
  protected String comment;
  protected String packageName;
  protected List<String> className;

  protected abstract void emit(final Record record);

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

    emit(node.isInterface() ?
      new InterfaceRecord(
        this.comment,
        String.format("%s.%s", this.packageName, String.join(".", this.className)),
        node.getName().asString()
      ) :
      new ClassRecord(
        this.comment,
        String.format("%s.%s", this.packageName, String.join(".", this.className)),
        node.getName().asString()
      )
    );

    super.visit(node, arg);
  }

  @Override
  public void visit(final MethodDeclaration node, final Void arg) {
    emit(new MethodRecord(
      this.comment,
      String.format("%s.%s.%s", this.packageName, String.join(".", this.className), node.getName().asString()),
      node.getName().asString()
    ));

    super.visit(node, arg);
  }
}
