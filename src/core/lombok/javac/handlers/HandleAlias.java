package lombok.javac.handlers;

import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.recursiveSetGeneratedBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import lombok.Alias;
import lombok.core.AST.Kind;
import lombok.core.HandlerPriority;
import lombok.core.ImportList;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.permit.Permit;
import lombok.spi.Provides;

import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

/**
 * Handles {@link Alias}: replaces the alias type on local variable declarations, method
 * parameters, fields, and return types with the real type and annotation declared in
 * {@code @Alias(of=..., annotated=...)}.
 *
 * <p>Alias types are located in:
 * <ol>
 *   <li>The same compilation unit (fast path)</li>
 *   <li>Other source files in the same build (via compiler.todo)</li>
 *   <li>Pre-compiled jar dependencies (via Javac Elements API)</li>
 * </ol>
 */
@Provides(JavacASTVisitor.class)
@HandlerPriority(HandleDelegate.HANDLE_DELEGATE_PRIORITY + 100)
public class HandleAlias extends JavacASTAdapter {

	// Per-context registry of alias types found across all compilation units.
	// WeakHashMap ensures entries are GC'd when the compilation context is released.
	private static final WeakHashMap<Context, Map<String, AliasInfo>> CROSS_UNIT_REGISTRY =
			new WeakHashMap<Context, Map<String, AliasInfo>>();

	@Override
	public void endVisitLocal(JavacNode localNode, JCVariableDecl local) {
		applyAlias(localNode, local);
	}

	@Override
	public void endVisitMethodArgument(JavacNode argNode, JCVariableDecl arg, JCMethodDecl method) {
		applyAlias(argNode, arg);
	}

	@Override
	public void endVisitField(JavacNode fieldNode, JCVariableDecl field) {
		applyAlias(fieldNode, field);
	}

	@Override
	public void endVisitMethod(JavacNode methodNode, JCMethodDecl method) {
		applyAliasToReturnType(methodNode, method);
	}

	private void applyAlias(JavacNode node, JCVariableDecl var) {
		JCTree typeTree = var.vartype;
		if (typeTree == null) return;
		if (!(typeTree instanceof JCIdent)) return;
		String typeName = ((JCIdent) typeTree).name.toString();

		AliasInfo alias = findAlias(node, typeName);
		if (alias == null) return;

		JavacNode sourceNode = node.getNodeFor(typeTree);
		JavacTreeMaker maker = node.getTreeMaker();
		String declTarget = declarationTargetFor(node.getKind());

		JCExpression newVartype = chainDotsString(node, alias.ofTypeName);
		recursiveSetGeneratedBy(newVartype, sourceNode);

		for (String annotatedTypeName : alias.annotatedTypeNames) {
			Set<String> targets = getAnnotationTargets(node, annotatedTypeName);
			boolean isTypeUse = targets.contains("TYPE_USE");
			boolean isDeclTarget = declTarget != null && (targets.isEmpty() || targets.contains(declTarget));

			JCExpression annTypeExpr = chainDotsString(node, annotatedTypeName);
			JCAnnotation newAnn = maker.Annotation(annTypeExpr, List.<JCExpression>nil());
			recursiveSetGeneratedBy(newAnn, sourceNode);

			if (isTypeUse) {
				if (newVartype instanceof JCIdent) {
					// Simple (unqualified) type: add to mods so the annotation lands before the
					// keyword on its own line. JCAnnotatedType on a simple name prints inline
					// (e.g. "private @Nullable String"), which the parser re-distributes into
					// mods.annotations on re-parse, breaking idempotency.
					var.mods.annotations = var.mods.annotations == null
							? List.of(newAnn)
							: var.mods.annotations.prepend(newAnn);
				} else {
					// Qualified type (e.g. java.lang.String): TYPE_USE annotation must sit on
					// the innermost component ("java.lang.@Nullable String") — placing it in
					// mods gives a "scoping construct" compile error.
					newVartype = maker.AnnotatedType(List.of(newAnn), newVartype);
					recursiveSetGeneratedBy(newVartype, sourceNode);
				}
			} else if (isDeclTarget) {
				var.mods.annotations = var.mods.annotations == null
						? List.of(newAnn)
						: var.mods.annotations.prepend(newAnn);
			}
		}

		var.vartype = newVartype;
		node.getAst().setChanged();
	}

	private void applyAliasToReturnType(JavacNode methodNode, JCMethodDecl method) {
		JCExpression restype = method.restype;
		if (restype == null) return;
		if (!(restype instanceof JCIdent)) return;
		String typeName = ((JCIdent) restype).name.toString();

		AliasInfo alias = findAlias(methodNode, typeName);
		if (alias == null) return;

		JavacNode sourceNode = methodNode.getNodeFor(restype);
		JavacTreeMaker maker = methodNode.getTreeMaker();

		JCExpression newRestype = chainDotsString(methodNode, alias.ofTypeName);
		recursiveSetGeneratedBy(newRestype, sourceNode);

		for (String annotatedTypeName : alias.annotatedTypeNames) {
			Set<String> targets = getAnnotationTargets(methodNode, annotatedTypeName);
			boolean isTypeUse = targets.contains("TYPE_USE");
			boolean isMethodTarget = targets.isEmpty() || targets.contains("METHOD");

			JCExpression annTypeExpr = chainDotsString(methodNode, annotatedTypeName);
			JCAnnotation newAnn = maker.Annotation(annTypeExpr, List.<JCExpression>nil());
			recursiveSetGeneratedBy(newAnn, sourceNode);

			if (isTypeUse) {
				if (newRestype instanceof JCIdent) {
					// Simple return type: annotation in mods prints before the access modifier
					// on its own line and is idempotent on re-parse.
					method.mods.annotations = method.mods.annotations == null
							? List.of(newAnn)
							: method.mods.annotations.prepend(newAnn);
				} else {
					// Qualified return type: must use JCAnnotatedType to avoid "scoping construct" error.
					newRestype = maker.AnnotatedType(List.of(newAnn), newRestype);
					recursiveSetGeneratedBy(newRestype, sourceNode);
				}
			} else if (isMethodTarget) {
				method.mods.annotations = method.mods.annotations == null
						? List.of(newAnn)
						: method.mods.annotations.prepend(newAnn);
			}
		}

		method.restype = newRestype;
		methodNode.getAst().setChanged();
	}

	private static String declarationTargetFor(Kind kind) {
		switch (kind) {
		case FIELD: return "FIELD";
		case ARGUMENT: return "PARAMETER";
		case LOCAL: return "LOCAL_VARIABLE";
		default: return null;
		}
	}

	private static Set<String> getAnnotationTargets(JavacNode node, String annotationFqn) {
		Set<String> targets = new HashSet<String>();
		try {
			TypeElement annElement = JavacElements.instance(node.getContext()).getTypeElement(annotationFqn);
			if (annElement == null) return targets;
			for (AnnotationMirror mirror : annElement.getAnnotationMirrors()) {
				if (!"java.lang.annotation.Target".equals(mirror.getAnnotationType().toString())) continue;
				for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
					addTargetValues(entry.getValue().getValue(), targets);
				}
			}
		} catch (Exception e) {
			// If lookup fails, return empty targets; caller treats this as "no @Target known"
		}
		return targets;
	}

	private static void addTargetValues(Object value, Set<String> targets) {
		if (value instanceof java.util.List) {
			for (Object v : (java.util.List<?>) value) {
				if (v instanceof AnnotationValue) addTargetValues(((AnnotationValue) v).getValue(), targets);
			}
		} else if (value instanceof VariableElement) {
			targets.add(((VariableElement) value).getSimpleName().toString());
		}
	}

	// -------------------------------------------------------------------------
	// Alias lookup: same CU → other source CUs → pre-compiled jars
	// -------------------------------------------------------------------------

	private AliasInfo findAlias(JavacNode node, String typeName) {
		// 1. Current compilation unit — fast path, no context required
		JavacNode cuNode = node;
		while (cuNode.up() != null) cuNode = cuNode.up();
		AliasInfo info = findAliasInDefs(((JCCompilationUnit) cuNode.get()).defs, typeName);
		if (info != null) return info;

		// 2. Other source files compiled in the same build
		info = getCrossUnitRegistry(node.getContext()).get(typeName);
		if (info != null) return info;

		// 3. Pre-compiled jar dependency — resolve via FQN from imports
		return findAliasInJar(node, typeName);
	}

	// -------------------------------------------------------------------------
	// Cross-unit registry: populated from compiler.todo on first use
	// -------------------------------------------------------------------------

	private static Map<String, AliasInfo> getCrossUnitRegistry(Context ctx) {
		synchronized (CROSS_UNIT_REGISTRY) {
			Map<String, AliasInfo> cached = CROSS_UNIT_REGISTRY.get(ctx);
			if (cached != null) return cached;

			Map<String, AliasInfo> registry = new HashMap<String, AliasInfo>();

			// compiler.todo is populated by the Enter phase (which runs before annotation
			// processing), so all source files in this build are already represented.
			// JavaCompiler.instance() and the todo field require reflective access via Permit.
			try {
				Object compiler = Permit.invoke(
						Permit.getMethod(JavaCompiler.class, "instance", Context.class),
						null, ctx);
				Iterable<?> todo = (Iterable<?>) Permit.getField(JavaCompiler.class, "todo").get(compiler);
				Set<JCCompilationUnit> seen = Collections.newSetFromMap(
						new IdentityHashMap<JCCompilationUnit, Boolean>());
				for (Object envObj : todo) {
					@SuppressWarnings("unchecked")
					Env<AttrContext> env = (Env<AttrContext>) envObj;
					JCCompilationUnit cu = env.toplevel;
					if (cu == null || !seen.add(cu)) continue;
					scanDefsIntoRegistry(cu.defs, registry);
				}
			} catch (Exception e) {
				// If reflective access fails, fall back to jar-only resolution
			}

			CROSS_UNIT_REGISTRY.put(ctx, registry);
			return registry;
		}
	}

	private static void scanDefsIntoRegistry(List<? extends JCTree> defs, Map<String, AliasInfo> registry) {
		for (JCTree def : defs) {
			if (!(def instanceof JCClassDecl)) continue;
			JCClassDecl classDecl = (JCClassDecl) def;
			AliasInfo info = extractAliasInfo(classDecl);
			// First definition wins; avoids ambiguity when two packages define same simple name
			if (info != null && !registry.containsKey(classDecl.name.toString()))
				registry.put(classDecl.name.toString(), info);
			scanDefsIntoRegistry(classDecl.defs, registry);
		}
	}

	// -------------------------------------------------------------------------
	// Jar support: resolve FQN via imports, read annotation mirrors
	// -------------------------------------------------------------------------

	private static AliasInfo findAliasInJar(JavacNode node, String typeName) {
		ImportList imports = node.getAst().getImportList();
		String fqn = imports.getFullyQualifiedNameForSimpleName(typeName);
		if (fqn == null) return null; // can't resolve FQN from explicit imports — skip

		TypeElement typeElement = JavacElements.instance(node.getContext()).getTypeElement(fqn);
		if (typeElement == null) return null;

		String ofTypeName = null;
		java.util.List<String> annotatedTypeNames = new ArrayList<String>();

		for (AnnotationMirror mirror : typeElement.getAnnotationMirrors()) {
			if (!Alias.class.getName().equals(mirror.getAnnotationType().toString())) continue;

			for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
					mirror.getElementValues().entrySet()) {
				String memberName = entry.getKey().getSimpleName().toString();
				Object value = entry.getValue().getValue();
				if ("of".equals(memberName)) {
					if (value instanceof TypeMirror) ofTypeName = value.toString();
				} else if ("annotated".equals(memberName)) {
					if (value instanceof TypeMirror) {
						annotatedTypeNames.add(value.toString());
					} else if (value instanceof java.util.List) {
						for (Object item : (java.util.List<?>) value) {
							if (item instanceof AnnotationValue) {
								Object itemValue = ((AnnotationValue) item).getValue();
								if (itemValue instanceof TypeMirror) annotatedTypeNames.add(itemValue.toString());
							}
						}
					}
				}
			}
			break;
		}

		if (ofTypeName != null)
			return new AliasInfo(ofTypeName, annotatedTypeNames);
		return null;
	}

	// -------------------------------------------------------------------------
	// Shared AST scanning helpers
	// -------------------------------------------------------------------------

	private static AliasInfo findAliasInDefs(List<? extends JCTree> defs, String typeName) {
		for (JCTree def : defs) {
			if (!(def instanceof JCClassDecl)) continue;
			JCClassDecl classDecl = (JCClassDecl) def;
			if (typeName.equals(classDecl.name.toString())) {
				AliasInfo info = extractAliasInfo(classDecl);
				if (info != null) return info;
			}
			AliasInfo nested = findAliasInDefs(classDecl.defs, typeName);
			if (nested != null) return nested;
		}
		return null;
	}

	/**
	 * Reads {@code @Alias(of=X.class, annotated={Y.class, ...})} from a class declaration AST node.
	 * Uses a direct name check (both simple and fully-qualified) rather than import resolution,
	 * so it works correctly when scanning compilation units other than the current one.
	 */
	private static AliasInfo extractAliasInfo(JCClassDecl classDecl) {
		for (JCAnnotation ann : classDecl.mods.annotations) {
			String annName = ann.annotationType.toString();
			if (!"Alias".equals(annName) && !"lombok.Alias".equals(annName)) continue;

			String ofTypeName = null;
			java.util.List<String> annotatedTypeNames = new ArrayList<String>();

			for (JCExpression arg : ann.args) {
				if (!(arg instanceof JCAssign)) continue;
				JCAssign assign = (JCAssign) arg;
				if (!(assign.lhs instanceof JCIdent)) continue;
				String memberName = ((JCIdent) assign.lhs).name.toString();
				if ("of".equals(memberName)) {
					ofTypeName = extractClassLiteralName(assign.rhs);
				} else if ("annotated".equals(memberName)) {
					extractClassLiteralNames(assign.rhs, annotatedTypeNames);
				}
			}

			if (ofTypeName != null)
				return new AliasInfo(ofTypeName, annotatedTypeNames);
		}
		return null;
	}

	private static String extractClassLiteralName(JCExpression expr) {
		if (expr instanceof JCFieldAccess) {
			JCFieldAccess fa = (JCFieldAccess) expr;
			if ("class".equals(fa.name.toString())) return fa.selected.toString();
		}
		return null;
	}

	private static void extractClassLiteralNames(JCExpression expr, java.util.List<String> out) {
		String single = extractClassLiteralName(expr);
		if (single != null) {
			out.add(single);
			return;
		}
		if (expr instanceof JCNewArray) {
			for (JCExpression elem : ((JCNewArray) expr).elems) {
				String name = extractClassLiteralName(elem);
				if (name != null) out.add(name);
			}
		}
	}

	private static final class AliasInfo {
		final String ofTypeName;
		final java.util.List<String> annotatedTypeNames;

		AliasInfo(String ofTypeName, java.util.List<String> annotatedTypeNames) {
			this.ofTypeName = ofTypeName;
			this.annotatedTypeNames = annotatedTypeNames;
		}
	}
}
