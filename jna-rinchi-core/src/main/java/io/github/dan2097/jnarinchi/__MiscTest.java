package io.github.dan2097.jnarinchi;

public class __MiscTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
	}
	
	public void test() {
		int res = new MyClass(3,5).lineFunction(10);
	}
	
	private class MyClass {
		private int a = 0;
		private int b = 1;
		
		public MyClass(int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		public int lineFunction(int x) {
			return a*x + b;
		}
	}

}
