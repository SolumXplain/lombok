//version 9:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
// CONF: lombok.nonNull.skipAnnotations += com.fasterxml.jackson.annotation.JsonProperty
package nullmarkedmodule;

@lombok.RequiredArgsConstructor
@lombok.Getter
@lombok.Setter
class NullMarkedModule {
	int i;
	String s;
	@org.jspecify.annotations.Nullable
	Object o;
	@com.fasterxml.jackson.annotation.JsonProperty("v")
	String v;

}