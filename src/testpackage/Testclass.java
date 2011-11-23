package testpackage;

public class Testclass {
	public static String myMethod() {
		return "hallo";
	}
	
	public static String myMethod(int a, int b, int c) {
		return "3 parameter: " + a + ", " + b + ", " + c;
	}
	
	public static String myMethod(int x) {
		return "hallo " + x + "!";
	}
	
	public static int integerArrayTest(int[] x){
		int result = 0;
		for(int c  : x)  {
			result += c;
		}
		return result;
	}
}
