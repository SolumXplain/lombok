//version 8:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
// CONF: lombok.nullSafePackages += nullmarked
// CONF: lombok.nonNull.skipAnnotations += com.fasterxml.jackson.annotation.JsonProperty
package nullmarked;

@lombok.RequiredArgsConstructor
@lombok.Getter
@lombok.Setter
class NullMarkedPackageInfo {
	int i;
	String s;
	@org.jspecify.annotations.Nullable
	Object o;
	@com.fasterxml.jackson.annotation.JsonProperty("v")
	String v;

}