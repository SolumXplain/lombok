/*
 * Copyright (C) 2025 The Project Lombok Authors.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.core;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.Context;
import lombok.javac.JavacAST;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BenchmarkHasNonNullAnnotations {
	private JavacNode noAnnotations;
	private JavacNode singleNonNull;
	private JavacNode singleNonMatching;
	private JavacNode manyNoMatch;
	private JavacNode manyLastMatch;
	private JavacNode androidNonNullFirst;

	private static final String SOURCE =
		"class BenchmarkTarget {\n" +
		"    int noAnnotations;\n" +
		"    @lombok.NonNull String singleNonNull;\n" +
		"    @java.lang.Deprecated String singleNonMatching;\n" +
		"    @java.lang.Deprecated @java.lang.SuppressWarnings(\"all\") @java.lang.Override int manyNoMatch;\n" +
		"    @java.lang.Deprecated @java.lang.SuppressWarnings(\"all\") @java.lang.Override @lombok.NonNull int manyLastMatch;\n" +
		"    @android.annotation.NonNull String androidNonNullFirst;\n" +
		"}\n";

	@Setup(Level.Trial)
	public void setup() throws Exception {
		Context context = new Context();
		JavaCompiler compiler = new JavaCompiler(context);
		compiler.genEndPos = true;

		JavaFileObject sourceFile = new SimpleJavaFileObject(
				URI.create("mem:///BenchmarkTarget.java"), JavaFileObject.Kind.SOURCE) {
			@Override public CharSequence getCharContent(boolean ignoreEncodingErrors) {
				return SOURCE;
			}
		};

		JCCompilationUnit cu = compiler.parse(sourceFile);
		JavacAST ast = new JavacAST(null, context, cu, new CleanupRegistry());

		JCClassDecl classDef = null;
		for (com.sun.tools.javac.tree.JCTree def : cu.defs) {
			if (def instanceof JCClassDecl) { classDef = (JCClassDecl) def; break; }
		}

		for (com.sun.tools.javac.tree.JCTree member : classDef.defs) {
			if (!(member instanceof JCVariableDecl)) continue;
			JCVariableDecl v = (JCVariableDecl) member;
			switch (v.name.toString()) {
				case "noAnnotations":    noAnnotations    = ast.get(v); break;
				case "singleNonNull":    singleNonNull    = ast.get(v); break;
				case "singleNonMatching": singleNonMatching = ast.get(v); break;
				case "manyNoMatch":      manyNoMatch      = ast.get(v); break;
				case "manyLastMatch":    manyLastMatch    = ast.get(v); break;
				case "androidNonNullFirst": androidNonNullFirst = ast.get(v); break;
				default: break;
			}
		}
	}

	@Benchmark public boolean noAnnotations()    { return JavacHandlerUtil.hasNonNullAnnotations(noAnnotations); }
	@Benchmark public boolean singleNonNull()    { return JavacHandlerUtil.hasNonNullAnnotations(singleNonNull); }
	@Benchmark public boolean singleNonMatching() { return JavacHandlerUtil.hasNonNullAnnotations(singleNonMatching); }
	@Benchmark public boolean manyNoMatch()       { return JavacHandlerUtil.hasNonNullAnnotations(manyNoMatch); }
	@Benchmark public boolean manyLastMatch()     { return JavacHandlerUtil.hasNonNullAnnotations(manyLastMatch); }
	@Benchmark public boolean androidNonNullFirst() { return JavacHandlerUtil.hasNonNullAnnotations(androidNonNullFirst); }

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(BenchmarkHasNonNullAnnotations.class.getSimpleName())
			.warmupIterations(1)
			.measurementIterations(1)
			.forks(1)
			.build();
		new Runner(opt).run();
	}
}

