public class sample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RSC_U485 servo = new RSC_U485("COM17", 115200);

		System.out.println("ID1�̃T�[�{�̃g���N���I��");
		servo.torque(1, true);

		System.out.println("�ō����x��100�x�̈ʒu�։�]");
		servo.move(1, 1000, 0);
		try {
			Thread.sleep(500);//���΂��҂�
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("���݊p�x:"+servo.getAngle(1));
		
		System.out.println("1�b������0�x�̈ʒu��");
		servo.move(1, 0, 100);
		
		try {
			Thread.sleep(1000);//���΂��҂�
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("���݊p�x:"+servo.getAngle(1));
		
	}

}
