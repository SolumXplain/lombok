//version 9:
import java.lang.annotation.*;

@lombok.RequiredArgsConstructor
@lombok.Getter
@lombok.Setter
@org.jspecify.annotations.NullMarked
class NullMarkedArrays {
	// should generate null check - TODO update after- and fix implementation
	private @org.jspecify.annotations.Nullable String[] arrayOfNullableStrings;
	// should not generate null check
	private byte @org.jspecify.annotations.Nullable[] nullableByteArray;

}