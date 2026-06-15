/*
 * Copyright (C) 2012-2025 The Project Lombok Authors.
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
package lombok.javac.handlers;

import static com.sun.tools.javac.code.Flags.*;
import static lombok.core.handlers.HandlerUtil.*;
import static lombok.javac.handlers.JavacHandlerUtil.*;
import static lombok.javac.handlers.JavacResolver.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.ElementKind;

import lombok.ConfigurationKeys;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.experimental.ExtensionMethod;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacResolution;
import lombok.spi.Provides;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.TypeSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.code.Type.ErrorType;
import com.sun.tools.javac.code.Type.ForAll;
import com.sun.tools.javac.code.Type.MethodType;
import com.sun.tools.javac.code.Types;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

/**
 * Handles the {@link ExtensionMethod} annotation for javac.
 */
@Provides
@HandlerPriority(66560) // 2^16 + 2^10; we must run AFTER HandleVal which is at 2^16
public class HandleExtensionMethod extends JavacAnnotationHandler<ExtensionMethod> {
	@Override
	public void handle(final AnnotationValues<ExtensionMethod> annotation, final JCAnnotation source, final JavacNode annotationNode) {
		handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.EXTENSION_METHOD_FLAG_USAGE, "@ExtensionMethod");
		
		deleteAnnotationIfNeccessary(annotationNode, ExtensionMethod.class);
		JavacNode typeNode = annotationNode.up();
		boolean isClassEnumInterfaceOrRecord = isClassEnumInterfaceOrRecord(typeNode);
		
		if (!isClassEnumInterfaceOrRecord) {
			annotationNode.addError("@ExtensionMethod can only be used on a class, an enum, an interface or a record");
			return;
		}
		
		boolean suppressBaseMethods = annotation.getInstance().suppressBaseMethods();
		
		List<Object> extensionProviders = annotation.getActualExpressions("value");
		if (extensionProviders.isEmpty()) {
			annotationNode.addError(String.format("@%s has no effect since no extension types were specified.", ExtensionMethod.class.getName()));
			return;
		}
		final List<Extension> extensions = getExtensions(annotationNode, extensionProviders);
		if (extensions.isEmpty()) return;
		
		new ExtensionMethodReplaceVisitor(annotationNode, extensions, suppressBaseMethods).replace();
		
		annotationNode.rebuild();
	}
	
	
	public List<Extension> getExtensions(final JavacNode typeNode, final List<Object> extensionProviders) {
		List<Extension> extensions = new ArrayList<Extension>();
		for (Object extensionProvider : extensionProviders) {
			if (!(extensionProvider instanceof JCFieldAccess)) continue;
			JCFieldAccess provider = (JCFieldAccess) extensionProvider;
			if (!("class".equals(provider.name.toString()))) continue;
			Type providerType = CLASS.resolveMember(typeNode, provider.selected);
			if (providerType == null) continue;
			if ((providerType.tsym.flags() & ANNOTATION) != 0) continue;
			
			extensions.add(getExtension(typeNode, (ClassType) providerType));
		}
		return extensions;
	}
	
	public Extension getExtension(final JavacNode typeNode, final ClassType extensionMethodProviderType) {
		List<MethodSymbol> extensionMethods = new ArrayList<MethodSymbol>();
		TypeSymbol tsym = extensionMethodProviderType.asElement();
		if (tsym != null) for (Symbol member : tsym.getEnclosedElements()) {
			if (member.getKind() != ElementKind.METHOD) continue;
			MethodSymbol method = (MethodSymbol) member;
			if ((method.flags() & STATIC) == 0) continue;
			if ((method.flags() & PUBLIC) == 0) continue;
			if (method.params().isEmpty()) continue;
			extensionMethods.add(method);
		}
		return new Extension(extensionMethods, tsym);
	}
	
	private static class Extension {
		final List<MethodSymbol> extensionMethods;
		final TypeSymbol extensionProvider;
		
		public Extension(List<MethodSymbol> extensionMethods, TypeSymbol extensionProvider) {
			this.extensionMethods = extensionMethods;
			this.extensionProvider = extensionProvider;
		}
	}
	
	private static class ExtensionMethodReplaceVisitor extends TreeScanner<Void, Void> {
		final JavacNode annotationNode;
		final List<Extension> extensions;
		final boolean suppressBaseMethods;
		final Set<String> names = new HashSet<String>();
		
		public ExtensionMethodReplaceVisitor(JavacNode annotationNode, List<Extension> extensions, boolean suppressBaseMethods) {
			this.annotationNode = annotationNode;
			this.extensions = extensions;
			this.suppressBaseMethods = suppressBaseMethods;
			
			for (Extension extension : extensions) {
				for (MethodSymbol methodSymbol : extension.extensionMethods) {
					names.add(methodSymbol.name.toString());
				}
			}
		}
		
		public void replace() {
			annotationNode.up().get().accept(this, null);
		}
		
		@Override
		public Void visitMethodInvocation(final MethodInvocationTree tree, final Void p) {
			scan(tree.getTypeArguments(), p);
			scan(tree.getMethodSelect(), p);
			handleMethodCall((JCMethodInvocation) tree);
			scan(tree.getArguments(), p);
			return null;
		}
		
		private void handleMethodCall(final JCMethodInvocation methodCall) {
			JavacNode methodCallNode = annotationNode.getAst().get(methodCall);
			
			if (methodCallNode == null) {
				// This should mean the node does not exist in the source at all. This is the case for generated nodes, such as implicit super() calls.
				return;
			}
			
			JavacNode surroundingType = upToTypeNode(methodCallNode);
			
			TypeSymbol surroundingTypeSymbol = ((JCClassDecl)surroundingType.get()).sym;
			JCExpression receiver = receiverOf(methodCall);
			String methodName = methodNameOf(methodCall);
			
			if (!names.contains(methodName)) return;
			if ("this".equals(receiver.toString()) || "this".equals(methodName) || "super".equals(methodName)) return;
			Map<JCTree, JCTree> resolution = new JavacResolution(methodCallNode.getContext()).resolveMethodMember(methodCallNode);
			
			JCTree resolvedMethodCall = resolution.get(methodCall);
			if (resolvedMethodCall == null || resolvedMethodCall.type == null) return;
			if (!suppressBaseMethods && !(resolvedMethodCall.type instanceof ErrorType)) return;
			
			JCTree resolvedReceiver = resolution.get(receiver);
			if (resolvedReceiver == null || resolvedReceiver.type == null) return;
			Type receiverType = resolvedReceiver.type;
			if (receiverType.isErroneous()) return;
			
			// Skip static method access
			Symbol sym = null;
			if (resolvedReceiver instanceof JCIdent) {
				sym = ((JCIdent) resolvedReceiver).sym;
			} else if (resolvedReceiver instanceof JCFieldAccess) {
				sym = ((JCFieldAccess) resolvedReceiver).sym;
			}
			if (sym instanceof ClassSymbol) return;
			
			Types types = Types.instance(annotationNode.getContext());
			Symbol receiverTyped = typedTarget(findVarDecl(sym));
			for (Extension extension : extensions) {
				TypeSymbol extensionProvider = extension.extensionProvider;
				if (surroundingTypeSymbol == extensionProvider) continue;
				for (MethodSymbol extensionMethod : extension.extensionMethods) {
					if (!methodName.equals(extensionMethod.name.toString())) continue;
					Type extensionMethodType = extensionMethod.type;
					if (!MethodType.class.isInstance(extensionMethodType) && !ForAll.class.isInstance(extensionMethodType)) continue;
					Type firstArgType = types.erasure(extensionMethodType.asMethodType().argtypes.get(0));
					if (!types.isAssignable(receiverType, firstArgType)) continue;
					// When both the receiver and the extension method's first parameter carry a @Typed
					// discriminator, they must agree; this lets @Typed pick between otherwise-identical
					// candidates (same name, same erased first argument type).
					Symbol methodTyped = typedTarget(findVarDecl(extensionMethod.params().get(0)));
					if (receiverTyped != null && methodTyped != null && receiverTyped != methodTyped) continue;
					methodCall.args = methodCall.args.prepend(receiver);
					methodCall.meth = chainDotsString(annotationNode, extensionProvider.toString() + "." + methodName);
					recursiveSetGeneratedBy(methodCall.meth, methodCallNode);
					return;
				}
			}
		}
		
		/**
		 * Finds the source declaration of the given symbol within the current compilation unit, or
		 * {@code null} if it is not declared here (for example, an extension method living in another
		 * source file). {@code @Typed} is a {@code TYPE_USE} annotation, and the type-annotation API
		 * needed to read it from a resolved symbol only exists in javac8+, so we read it from the
		 * original AST instead.
		 */
		private JCVariableDecl findVarDecl(final Symbol symbol) {
			if (symbol == null) return null;
			final JCVariableDecl[] result = new JCVariableDecl[1];
			annotationNode.getAst().top().get().accept(new TreeScanner<Void, Void>() {
				@Override public Void visitVariable(VariableTree node, Void p) {
					if (result[0] == null && ((JCVariableDecl) node).sym == symbol) result[0] = (JCVariableDecl) node;
					return super.visitVariable(node, p);
				}
			}, null);
			return result[0];
		}

		/**
		 * Returns the type referenced by a {@code @lombok.Typed(SomeType.class)} annotation on the given
		 * declaration, or {@code null} if there is none. Used to discriminate between extension methods
		 * whose signatures are otherwise indistinguishable (same name, same erased first argument type).
		 */
		private Symbol typedTarget(final JCVariableDecl varDecl) {
			if (varDecl == null || varDecl.mods == null) return null;
			JavacNode declNode = annotationNode.getAst().get(varDecl);
			if (declNode == null) return null;
			JavacNode typeNode = upToTypeNode(declNode);
			for (JCAnnotation annotation : varDecl.mods.annotations) {
				if (!isTypedAnnotation(annotation)) continue;
				JCExpression value = annotation.args.isEmpty() ? null : annotation.args.head;
				if (value instanceof JCAssign) value = ((JCAssign) value).rhs;
				if (!(value instanceof JCFieldAccess)) continue;
				JCFieldAccess classLiteral = (JCFieldAccess) value;
				if (!"class".equals(classLiteral.name.toString())) continue;
				Type targetType = CLASS.resolveMember(typeNode, classLiteral.selected);
				if (targetType != null) return targetType.tsym;
			}
			return null;
		}

		private boolean isTypedAnnotation(final JCAnnotation annotation) {
			String name = annotation.annotationType.toString();
			return "Typed".equals(name) || name.endsWith(".Typed");
		}

		private String methodNameOf(final JCMethodInvocation methodCall) {
			if (methodCall.meth instanceof JCIdent) {
				return ((JCIdent) methodCall.meth).name.toString();
			} else {
				return ((JCFieldAccess) methodCall.meth).name.toString();
			}
		}
		
		private JCExpression receiverOf(final JCMethodInvocation methodCall) {
			if (methodCall.meth instanceof JCIdent) {
				return annotationNode.getTreeMaker().Ident(annotationNode.toName("this"));
			} else {
				return ((JCFieldAccess) methodCall.meth).selected;
			}
		}
	}
}
