// Copyright 2012 Google Inc. All Rights Reserved.
// Author: sreeni@google.com (Sreeni Viswanadha)

/* Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.javacc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the Constants file.
 */
public class OtherFilesGenCPP extends JavaCCGlobals implements JavaCCParserConstants {

  static public void start() throws MetaParseException {

    Token t = null;
    if (JavaCCErrors.get_error_count() != 0) throw new MetaParseException();

    CPPFiles.gen_JavaCCDefs();
    CPPFiles.gen_CharStream();
    CPPFiles.gen_Token();  // TODO(theov): issued twice??
    CPPFiles.gen_TokenManager();
    CPPFiles.gen_TokenMgrError();
    CPPFiles.gen_ParseException();
    CPPFiles.gen_Token();  // TODO(theov): issued twice??
    CPPFiles.gen_ErrorHandler();

    try {
      ostr = new java.io.PrintWriter(
                new java.io.BufferedWriter(
                   new java.io.FileWriter(
                     new java.io.File(Options.getOutputDirectory(), cu_name + "Constants.h")
                   ),
                   8192
                )
             );
    } catch (java.io.IOException e) {
      JavaCCErrors.semantic_error("Could not open file " + cu_name + "Constants.h for writing.");
      throw new Error();
    }

    List tn = new ArrayList(toolNames);
    tn.add(toolName);
    ostr.println("/* " + getIdString(tn, cu_name + "Constants.java") + " */");

    if (cu_to_insertion_point_1.size() != 0 &&
        ((Token)cu_to_insertion_point_1.get(0)).kind == PACKAGE
       ) {
      for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
        if (((Token)cu_to_insertion_point_1.get(i)).kind == SEMICOLON) {
          printTokenSetup((Token)(cu_to_insertion_point_1.get(0)));
          for (int j = 0; j <= i; j++) {
            t = (Token)(cu_to_insertion_point_1.get(j));
            printToken(t, ostr);
          }
          printTrailingComments(t, ostr);
          ostr.println("");
          ostr.println("");
          break;
        }
      }
    }
    ostr.println("");
    ostr.println("/**");
    ostr.println(" * Token literal values and constants.");
    ostr.println(" * Generated by org.javacc.parser.OtherFilesGen#start()");
    ostr.println(" */");

    String define = (cu_name + "Constants_h").toUpperCase();
    ostr.println("#ifndef " + define);
    ostr.println("#define " + define);
    ostr.println("");
    if (Options.stringValue("NAMESPACE").length() > 0) {
      ostr.println("namespace " + Options.stringValue("NAMESPACE_OPEN"));
    }

    RegularExpression re;
    String constPrefix = "const";
    ostr.println("  /** End of File. */");
    ostr.println(constPrefix + "  int _EOF = 0;");
    for (java.util.Iterator it = ordered_named_tokens.iterator(); it.hasNext();) {
      re = (RegularExpression)it.next();
      ostr.println("  /** RegularExpression Id. */");
      ostr.println(constPrefix + "  int " + re.label + " = " + re.ordinal + ";");
    }
    ostr.println("");

    if (!Options.getUserTokenManager() && Options.getBuildTokenManager()) {
      for (int i = 0; i < Main.lg.lexStateName.length; i++) {
        ostr.println("  /** Lexical state. */");
        ostr.println(constPrefix + "  int " + LexGen.lexStateName[i] + " = " + i + ";");
      }
      ostr.println("");
    }
    ostr.println("  /** Literal token values. */");
    int cnt = 0;
    ostr.println("  static JAVACC_CHAR_TYPE tokenImage_arr_" + cnt + "[] = ");
    printCharArray(ostr, "<EOF>");
    ostr.println(";");

    for (java.util.Iterator it = rexprlist.iterator(); it.hasNext();) {
      TokenProduction tp = (TokenProduction)(it.next());
      List respecs = tp.respecs;
      for (java.util.Iterator it2 = respecs.iterator(); it2.hasNext();) {
        RegExprSpec res = (RegExprSpec)(it2.next());
        re = res.rexp;
        ostr.println("  static JAVACC_CHAR_TYPE tokenImage_arr_" + ++cnt + "[] = ");
        if (re instanceof RStringLiteral) {
          printCharArray(ostr, "\"" + ((RStringLiteral)re).image + "\"");
        } else if (!re.label.equals("")) {
          printCharArray(ostr, "\"<" + re.label + ">\"");
        } else {
          if (re.tpContext.kind == TokenProduction.TOKEN) {
            JavaCCErrors.warning(re, "Consider giving this non-string token a label for better error reporting.");
          }
          printCharArray(ostr, "\"<token of kind " + re.ordinal + ">\"");
        }
        ostr.println(";");
      }
    }

    ostr.println("  static JAVACC_STRING_TYPE tokenImage[] = {");
    for (int i = 0; i <= cnt; i++) {
      ostr.println("tokenImage_arr_" + i + ", ");
    }
    ostr.println("  };");
    ostr.println("");
    if (Options.stringValue("NAMESPACE").length() > 0) {
      ostr.println(Options.stringValue("NAMESPACE_CLOSE"));
    }
    ostr.println("#endif");

    ostr.close();

  }

   // Used by the CPP code generatror
   public static void printCharArray(java.io.PrintWriter ostr, String s) {
     ostr.print("{");
     for (int i = 0; i < s.length(); i++) {
       ostr.print("0x" + Integer.toHexString((int)s.charAt(i)) + ", ");
     }
     ostr.print("0}");
   }

  static private java.io.PrintWriter ostr;

  public static void reInit()
  {
    ostr = null;
  }

}