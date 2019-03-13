/* This is free and unencumbered software released into the public domain. */

package org.drydoc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.LineComment;

/** ParseJava */
public class ParseJava {

  /** ParseJava.main(String[]) */
  public static void main(final String[] args) throws IOException {
    boolean verbose = false;

    final ParserConfiguration config = new ParserConfiguration();
    config.setLanguageLevel(LanguageLevel.CURRENT);
    config.setLexicalPreservationEnabled(false);
    config.setTabSize(2);

    final JavaParser parser = new JavaParser();

    final OutputGenerator output = new YAMLOutputGenerator();

    for (final String arg : args) {
      switch (arg) {
        case "-v": {
          verbose = true;
          break;
        }

        default: {
          if (verbose) System.err.printf(">>> Parsing: %s\n", arg);
          try {
            final FileInputStream input = new FileInputStream(arg);
            parser.parse(input).ifSuccessful(originalCode -> {
              // Remove all non-Javadoc comments:
              originalCode.walk(node -> {
                if (node instanceof LineComment) {
                  node.remove();
                }
                else {
                  node.getComment().ifPresent(comment -> {
                    if (!comment.isJavadocComment()) comment.remove();
                  });
                }
              });
              //System.out.println(originalCode.toString()); // DEBUG
              parser.parse(originalCode.toString()).ifSuccessful(preprocessedCode -> {
                // Parse the full Java source, generating output:
                preprocessedCode.accept(output, null);
              });
            });
          }
          catch (final FileNotFoundException error) {
            if (verbose) System.err.printf(">>> ERROR: %s\n", error.getMessage());
            continue; // skip this file
          }
        }
      }
    }
  }
}
