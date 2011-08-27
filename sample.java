public class sample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RSC_U485 servo = new RSC_U485("COM17", 115200);

		System.out.println("ID1のサーボのトルクをオン");
		servo.torque(1, true);

		System.out.println("最高速度で100度の位置へ回転");
		servo.move(1, 1000, 0);
		try {
			Thread.sleep(500);//しばし待つ
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("現在角度:"+servo.getAngle(1));
		
		System.out.println("1秒かけて0度の位置へ");
		servo.move(1, 0, 100);
		
		try {
			Thread.sleep(1000);//しばし待つ
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("現在角度:"+servo.getAngle(1));
		
	}

}
