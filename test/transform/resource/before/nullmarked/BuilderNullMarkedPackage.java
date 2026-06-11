//version 8:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
// CONF: lombok.nullSafePackages += nullmarked
package nullmarked;

@lombok.Builder
class BuilderNullMarkedPackage {
	String nonNullField;
	@org.jspecify.annotations.Nullable
	String nullableField;
}