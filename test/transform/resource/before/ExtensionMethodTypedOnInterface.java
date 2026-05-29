import lombok.experimental.ExtensionMethod;
import com.example.Typed;

@ExtensionMethod({
		ExtensionMethodTypedOnInterface.SnakeyPassword.class,
		ExtensionMethodTypedOnInterface.Password.class
})
class ExtensionMethodTypedOnInterface {
	public String test() {
		return getMasked("test");
	}

	private String getMasked(@Typed(Password.class) String password) {
		return password.masked();
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
