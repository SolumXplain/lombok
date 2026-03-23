//version 9:
import java.lang.annotation.*;
@org.jspecify.annotations.NullMarked
class NullMarkedArrays {
	// should generate null check - TODO update after- and fix implementation
	@org.jspecify.annotations.Nullable
	private String[] arrayOfNullableStrings;
	// should not generate null check
	private byte @org.jspecify.annotations.Nullable [] nullableByteArray;
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public NullMarkedArrays() {
	}

	@org.jspecify.annotations.Nullable
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public String[] getArrayOfNullableStrings() {
		return this.arrayOfNullableStrings;
	}
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public byte @org.jspecify.annotations.Nullable [] getNullableByteArray() {
		return this.nullableByteArray;
	}
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public void setArrayOfNullableStrings(@org.jspecify.annotations.Nullable final String[] arrayOfNullableStrings) {
		this.arrayOfNullableStrings = arrayOfNullableStrings;
	}
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public void setNullableByteArray(final byte @org.jspecify.annotations.Nullable [] nullableByteArray) {
		this.nullableByteArray = nullableByteArray;
	}
}
