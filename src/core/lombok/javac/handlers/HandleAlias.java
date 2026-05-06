package lombok.javac.handlers;

import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.recursiveSetGeneratedBy;
import static lombok.javac.handlers.JavacHandlerUtil.typeMatches;

import lombok.Alias;
import lombok.core.HandlerPriority;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacNode;
import lombok.spi.Provides;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.List;

/**
 * Handles {@link Alias}: replaces the alias type on a local variable declaration with the
 * real type and annotation declared in {@code @Alias(of=..., annotated=...)}.
 *
 * <p>Only local variable declarations in the same compilation unit as the alias type are
 * currently supported. Cross-file (jar) alias types are a future enhancement.
 */
@Provides(JavacASTVisitor.class)
@HandlerPriority(HandleDelegate.HANDLE_DELEGATE_PRIORITY + 100)
public class HandleAlias extends JavacASTAdapter {

	@Override
	public void endVisitLocal(JavacNode localNode, JCVariableDecl local) {
		JCTree typeTree = local.vartype;
		if (typeTree == null) return;

		// Only handle simple name references (e.g. "Sector", not "pkg.Sector" or arrays).
		// Qualified names and arrays can be added later.
		if (!(typeTree instanceof JCIdent)) return;
		String typeName = ((JCIdent) typeTree).name.toString();

		AliasInfo alias = findAliasInCompilationUnit(localNode, typeName);
		if (alias == null) return;

		JavacNode sourceNode = localNode.getNodeFor(typeTree);

		// Replace the declared type with the alias target type
		JCExpression newVartype = chainDotsString(localNode, alias.ofTypeName);
		recursiveSetGeneratedBy(newVartype, sourceNode);
		local.vartype = newVartype;

		// Prepend the target annotation to any annotations already on the variable
		JCExpression annTypeExpr = chainDotsString(localNode, alias.annotatedTypeName);
		JCAnnotation newAnn = localNode.getTreeMaker().Annotation(annTypeExpr, List.<JCExpression>nil());
		recursiveSetGeneratedBy(newAnn, sourceNode);
		local.mods.annotations = local.mods.annotations == null
				? List.of(newAnn)
				: local.mods.annotations.prepend(newAnn);

		localNode.getAst().setChanged();
	}

	/**
	 * Walks the compilation unit's type declarations (top-level and nested) looking for one named
	 * {@code typeName} that carries {@code @Alias}.
	 */
	private AliasInfo findAliasInCompilationUnit(JavacNode localNode, String typeName) {
		JavacNode cuNode = localNode;
		while (cuNode.up() != null) cuNode = cuNode.up();
		JCCompilationUnit cu = (JCCompilationUnit) cuNode.get();
		return findAliasInDefs(localNode, cu.defs, typeName);
	}

	private AliasInfo findAliasInDefs(JavacNode sourceNode, List<? extends JCTree> defs, String typeName) {
		for (JCTree def : defs) {
			if (!(def instanceof JCClassDecl)) continue;
			JCClassDecl classDecl = (JCClassDecl) def;

			if (typeName.equals(classDecl.name.toString())) {
				AliasInfo info = extractAliasInfo(sourceNode, classDecl);
				if (info != null) return info;
			}

			// Recurse into nested type declarations
			AliasInfo nested = findAliasInDefs(sourceNode, classDecl.defs, typeName);
			if (nested != null) return nested;
		}
		return null;
	}

	/**
	 * Reads {@code @Alias(of=X.class, annotated=Y.class)} from a class declaration.
	 * Returns {@code null} if the declaration does not have a valid {@code @Alias}.
	 */
	private AliasInfo extractAliasInfo(JavacNode sourceNode, JCClassDecl classDecl) {
		for (JCAnnotation ann : classDecl.mods.annotations) {
			if (!typeMatches(Alias.class, sourceNode, ann.annotationType)) continue;

			String ofTypeName = null;
			String annotatedTypeName = null;

			for (JCExpression arg : ann.args) {
				if (!(arg instanceof JCAssign)) continue;
				JCAssign assign = (JCAssign) arg;
				if (!(assign.lhs instanceof JCIdent)) continue;
				String memberName = ((JCIdent) assign.lhs).name.toString();
				String value = extractClassLiteralName(assign.rhs);
				if (value == null) continue;
				if ("of".equals(memberName)) ofTypeName = value;
				else if ("annotated".equals(memberName)) annotatedTypeName = value;
			}

			if (ofTypeName != null && annotatedTypeName != null) {
				return new AliasInfo(ofTypeName, annotatedTypeName);
			}
		}
		return null;
	}

	/**
	 * Extracts the type name from a {@code Foo.class} expression — returns {@code "Foo"} or a
	 * fully qualified name. Returns {@code null} if the expression is not a class literal.
	 */
	private String extractClassLiteralName(JCExpression expr) {
		if (expr instanceof JCFieldAccess) {
			JCFieldAccess fa = (JCFieldAccess) expr;
			if ("class".equals(fa.name.toString())) {
				return fa.selected.toString();
			}
		}
		return null;
	}

	private static final class AliasInfo {
		final String ofTypeName;
		final String annotatedTypeName;

		AliasInfo(String ofTypeName, String annotatedTypeName) {
			this.ofTypeName = ofTypeName;
			this.annotatedTypeName = annotatedTypeName;
		}
	}
}
