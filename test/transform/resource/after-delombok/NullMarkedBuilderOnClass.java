//version 8:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
@org.jspecify.annotations.NullMarked
class NullMarkedBuilderOnClass {
	String nonNullField;
	@org.jspecify.annotations.Nullable
	String nullableField;

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	NullMarkedBuilderOnClass(final String nonNullField, @org.jspecify.annotations.Nullable final String nullableField) {
		java.util.Objects.requireNonNull(nonNullField, "nonNullField is marked non-null but is null");
		this.nonNullField = nonNullField;
		this.nullableField = nullableField;
	}


	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public static class NullMarkedBuilderOnClassBuilder {
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private String nonNullField;
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private String nullableField;

		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		NullMarkedBuilderOnClassBuilder() {
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public NullMarkedBuilderOnClass.NullMarkedBuilderOnClassBuilder nonNullField(final String nonNullField) {
			java.util.Objects.requireNonNull(nonNullField, "nonNullField is marked non-null but is null");
			this.nonNullField = nonNullField;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public NullMarkedBuilderOnClass.NullMarkedBuilderOnClassBuilder nullableField(@org.jspecify.annotations.Nullable final String nullableField) {
			this.nullableField = nullableField;
			return this;
		}

		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public NullMarkedBuilderOnClass build() {
			return new NullMarkedBuilderOnClass(this.nonNullField, this.nullableField);
		}

		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public java.lang.String toString() {
			return "NullMarkedBuilderOnClass.NullMarkedBuilderOnClassBuilder(nonNullField=" + this.nonNullField + ", nullableField=" + this.nullableField + ")";
		}
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public static NullMarkedBuilderOnClass.NullMarkedBuilderOnClassBuilder builder() {
		return new NullMarkedBuilderOnClass.NullMarkedBuilderOnClassBuilder();
	}
}
