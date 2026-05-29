class ExtensionMethodOnInterface {
	public String test() {
		int[] intArray = {5, 3, 8, 2};
		java.util.Arrays.sort(intArray);
		String iAmNull = null;
		return ExtensionMethodOnInterface.Extensions.or(iAmNull, ExtensionMethodOnInterface.Extensions.toTitleCase("hELlO, WORlD!"));
	}
	static interface Extensions {
		static <T> T or(T obj, T ifNull) {
			return obj != null ? obj : ifNull;
		}
		static String toTitleCase(String in) {
			if (in.isEmpty()) return in;
			return "" + Character.toTitleCase(in.charAt(0)) + in.substring(1).toLowerCase();
		}
	}
}
