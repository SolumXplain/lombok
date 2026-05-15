package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Declare a type alias: an interface annotated with {@code @Alias} is replaced by its target type
 * plus annotation at every use site.
 *
 * <p>Example:
 * <pre>
 * {@literal @}Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
 * interface Sector {}
 *
 * public class Company {
 *   private Sector code; // becomes: {@literal @}org.jspecify.annotations.Nullable String code;
 * }
 * </pre>
 *
 * <p>The alias interface still compiles to a real (empty) interface; it is harmless but unused at runtime.
 *
 * <h2>Disabling alias expansion</h2>
 *
 * <p>Expansion can be disabled without removing {@code @Alias} declarations, which is useful when
 * consuming alias types in a module that should not expand them.
 *
 * <p><b>lombok.config</b> (per-module or per-project):
 * <pre>
 * lombok.alias.enabled = false
 * </pre>
 *
 * <p><b>Maven</b> (compiler JVM system property via the {@code -J} prefix):
 * <pre>
 * &lt;plugin&gt;
 *   &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;
 *   &lt;configuration&gt;
 *     &lt;compilerArgs&gt;
 *       &lt;arg&gt;-J-Dlombok.alias.enabled=false&lt;/arg&gt;
 *     &lt;/compilerArgs&gt;
 *   &lt;/configuration&gt;
 * &lt;/plugin&gt;
 * </pre>
 *
 * <p><b>Gradle</b>:
 * <pre>
 * tasks.withType(JavaCompile).configureEach {
 *     options.compilerArgs += ['-J-Dlombok.alias.enabled=false']
 * }
 * </pre>
 *
 * <p>The system property takes precedence over {@code lombok.config}.
 */
@Target(ElementType.TYPE)
public @interface Alias {
	/** The real type to substitute at each use site. */
	Class<?> of();

	/** The annotations to add to each use site. */
	Class<? extends Annotation>[] annotated() default {};
}
