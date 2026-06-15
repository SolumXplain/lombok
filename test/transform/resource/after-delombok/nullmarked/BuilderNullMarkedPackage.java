//version 8:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
// CONF: lombok.nullSafePackages += nullmarked
package nullmarked;

class BuilderNullMarkedPackage {
	String nonNullField;
	@org.jspecify.annotations.Nullable
	String nullableField;

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	BuilderNullMarkedPackage(final String nonNullField, @org.jspecify.annotations.Nullable final String nullableField) {
		java.util.Objects.requireNonNull(nonNullField, "nonNullField is marked non-null but is null");
		this.nonNullField = nonNullField;
		this.nullableField = nullableField;
	}


	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public static class BuilderNullMarkedPackageBuilder {
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private String nonNullField;
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private String nullableField;

		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		BuilderNullMarkedPackageBuilder() {
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public BuilderNullMarkedPackage.BuilderNullMarkedPackageBuilder nonNullField(final String nonNullField) {
			java.util.Objects.requireNonNull(nonNullField, "nonNullField is marked non-null but is null");
			this.nonNullField = nonNullField;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public BuilderNullMarkedPackage.BuilderNullMarkedPackageBuilder nullableField(@org.jspecify.annotations.Nullable final String nullableField) {
			this.nullableField = nullableField;
			return this;
		}

		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public BuilderNullMarkedPackage build() {
			return new BuilderNullMarkedPackage(this.nonNullField, this.nullableField);
		}

		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public java.lang.String toString() {
			return "BuilderNullMarkedPackage.BuilderNullMarkedPackageBuilder(nonNullField=" + this.nonNullField + ", nullableField=" + this.nullableField + ")";
		}
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public static BuilderNullMarkedPackage.BuilderNullMarkedPackageBuilder builder() {
		return new BuilderNullMarkedPackage.BuilderNullMarkedPackageBuilder();
	}
}
