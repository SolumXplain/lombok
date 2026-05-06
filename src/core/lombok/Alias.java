package lombok;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Declare a type alias: an interface annotated with {@code @Alias} is replaced by its target type
 * plus annotation at every local variable use site.
 *
 * <p>Example:
 * <pre>
 * {@literal @}Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
 * interface Sector {}
 *
 * Sector code = getValue(); // becomes: {@literal @}org.jspecify.annotations.Nullable String code = getValue();
 * </pre>
 *
 * <p>The alias interface still compiles to a real (empty) interface; it is harmless but unused at runtime.
 * This feature currently handles local variable declarations only.
 */
@Target(ElementType.TYPE)
public @interface Alias {
	/** The real type to substitute at each use site. */
	Class<?> of();

	/** The annotation to add to each use site. */
	Class<? extends Annotation> annotated();
}
