//version 9:
// CONF: lombok.nonNull.checkMethod = java.util.Objects.requireNonNull
// CONF: lombok.nonNull.skipAnnotations += com.fasterxml.jackson.annotation.JsonProperty
package nullmarkedmodule;

class NullMarkedModule {
	int i;
	String s;
	@org.jspecify.annotations.Nullable
	Object o;
	@com.fasterxml.jackson.annotation.JsonProperty("v")
	String v;

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public NullMarkedModule() {
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public int getI() {
		return this.i;
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public String getS() {
		return this.s;
	}

	@org.jspecify.annotations.Nullable
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public Object getO() {
		return this.o;
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public String getV() {
		return this.v;
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public void setI(final int i) {
		this.i = i;
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public void setS(final String s) {
		java.util.Objects.requireNonNull(s, "s is marked non-null but is null");
		this.s = s;
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public void setO(@org.jspecify.annotations.Nullable final Object o) {
		this.o = o;
	}

	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public void setV(final String v) {
		this.v = v;
	}
}