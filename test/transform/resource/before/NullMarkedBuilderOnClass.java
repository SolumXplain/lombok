//version 8:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull

@lombok.Builder
@org.jspecify.annotations.NullMarked
class NullMarkedBuilderOnClass {
	String nonNullField;
	@org.jspecify.annotations.Nullable
	String nullableField;
}
