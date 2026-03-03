//version 9:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
// CONF: lombok.nonNull.skipAnnotations += com.fasterxml.jackson.annotation.JsonProperty
import java.lang.annotation.*;

@lombok.RequiredArgsConstructor
@lombok.Getter
@lombok.Setter
@org.jspecify.annotations.NullMarked
class NullMarkedPlain {
	int i;
	String s;
	@org.jspecify.annotations.Nullable
	Object o;
	@com.fasterxml.jackson.annotation.JsonProperty("v")
	String v;

}