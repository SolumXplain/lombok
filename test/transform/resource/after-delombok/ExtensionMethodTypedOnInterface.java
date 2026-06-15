import com.example.Typed;

class ExtensionMethodTypedOnInterface {
	public String test() {
		return getMasked("test");
	}

	private String getMasked(@Typed(Password.class) String password) {
		return ExtensionMethodTypedOnInterface.Password.masked(password);
	}

	interface SnakeyPassword {
		static String masked(@Typed(SnakeyPassword.class) String _this) {
			return "_".repeat(_this.length());
		}
	}

	interface Password {
		static String masked(@Typed(Password.class) String _this) {
			return "*".repeat(_this.length());
		}
	}
}
