//version 9:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
// CONF: lombok.nonNull.skipAnnotations += com.fasterxml.jackson.annotation.JsonProperty
package nullmarkedmodule.sub;

// Subpackage of a @NullMarked module - should also be null-marked without any package-info.java
@lombok.RequiredArgsConstructor
@lombok.Getter
@lombok.Setter
class NullMarkedModuleSub {
	int i;
	String s;
	@org.jspecify.annotations.Nullable
	Object o;
	@com.fasterxml.jackson.annotation.JsonProperty("v")
	String v;

}