/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/** ParseJava */
public class ParseJava {

  /** ParseJava.main(String[]) */
  public static void main(final String[] args) throws IOException {
    final JavaParser parser = new JavaParser();

    for (final String arg : args) {
      System.err.printf(">>> Parsing: %s\n", arg);
      try {
        final FileInputStream input = new FileInputStream(arg);
        final ParseResult<CompilationUnit> result = parser.parse(input);
        result.ifSuccessful(unit -> unit.accept(new Visitor(), null));
      }
      catch (final FileNotFoundException error) {
        System.err.printf(">>> ERROR: %s\n", error.getMessage());
        continue; // skip this file
      }
    }
  }

  private static class Visitor extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(final PackageDeclaration node, final Void arg) {
      System.out.printf("%s\n", node.getName()); // TODO
      super.visit(node, arg);
    }

    @Override
    public void visit(final ClassOrInterfaceDeclaration node, final Void arg) {
      System.out.printf("\t%s\n", node.getName()); // TODO
      super.visit(node, arg);
    }

    @Override
    public void visit(final MethodDeclaration node, final Void arg) {
      System.out.printf("\t\t%s\n", node.getName()); // TODO
      super.visit(node, arg);
    }
  }
}
