//version 8:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
// CONF: lombok.nonNull.skipAnnotations += com.fasterxml.jackson.annotation.JsonProperty
package nullmarked.nullunmarked;

@lombok.RequiredArgsConstructor
@lombok.Getter
@lombok.Setter
class NullUnmarkedInSubpackage {
	int i;
	String s;
	@org.jspecify.annotations.Nullable
	Object o;
	@com.fasterxml.jackson.annotation.JsonProperty("v")
	String v;

}