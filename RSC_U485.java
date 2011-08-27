//import java.io.IOException;

import Serial.Serial;
import Serial.SerialException;

public class RSC_U485 {

	private static Serial serial;
	private byte[] sendbuf = new byte[32];
	private byte[] readbuf = new byte[32];

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}

	/**
	 *  @param portname シリアルポート名
	 *  @param baudrate 通信速度
	 */
	public RSC_U485(String portname,int baudrate) {
		try {
			serial = new Serial(portname, baudrate, 'N', 8, 1);
			serial.clear();
		} catch (SerialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  サーボを指定角度へ動かす
	 *  可動範囲は中央が0度で，サーボ上面から見て時計回りが+，反時計回りが-
	 *  指定角度の単位は0.1度単位
	 *  指定時間の単位は10ms単位(最大0.5%の誤差)
	 *  
	 *  @param sId サーボID
	 *  @param sPos 指定角度
	 *  @param sTime 指定時間
	 */
	public void move(int sId, int sPos, int sTime) {
		byte sum;

		// パケット作成
		sendbuf[0] = (byte) 0xFA; // ヘッダー1
		sendbuf[1] = (byte) 0xAF; // ヘッダー2
		sendbuf[2] = (byte) sId; // サーボID
		sendbuf[3] = (byte) 0x00; // フラグ
		sendbuf[4] = (byte) 0x1E; // アドレス(0x1E=30)
		sendbuf[5] = (byte) 0x04; // 長さ(4byte)
		sendbuf[6] = (byte) 0x01; // 個数
		sendbuf[7] = (byte) (sPos & 0x00FF); // 位置
		sendbuf[8] = (byte) ((sPos & 0xFF00) >> 8); // 位置
		sendbuf[9] = (byte) (sTime & 0x00FF); // 時間
		sendbuf[10] = (byte) ((sTime & 0xFF00) >> 8); // 時間
		// チェックサムの計算
		sum = sendbuf[2];
		for (int i = 3; i < 11; i++) {
			sum = (byte) (sum ^ sendbuf[i]);
		}
		sendbuf[11] = sum; // チェックサム

		// 送信
		// serialport.out.write(sendbuf, 0, 12);
		for(int i=0;i<12;i++)
			serial.write(sendbuf[i]);
	}

	/**
	 *  サーボのトルクをON/OFFできる
	 *  
	 *  @param sId サーボID
	 *  @param sMode ON/OFFフラグ trueでトルクON
	 */
	public void torque(int sId, boolean sMode) {
		byte sum;

		// パケット作成
		sendbuf[0] = (byte) (0xFA); // ヘッダー1
		sendbuf[1] = (byte) (0xAF); // ヘッダー2
		sendbuf[2] = (byte) (sId); // サーボID
		sendbuf[3] = (byte) (0x00); // フラグ
		sendbuf[4] = (byte) (0x24); // アドレス(0x24=36)
		sendbuf[5] = (byte) (0x01); // 長さ(4byte)
		sendbuf[6] = (byte) (0x01); // 個数
		if (sMode)
			sendbuf[7] = (byte) (0x001); // ON/OFFフラグ
		else
			sendbuf[7] = (byte) (0x0000);
		// チェックサムの計算
		sum = sendbuf[2];
		for (int i = 3; i < 8; i++) {
			sum = (byte) (sum ^ sendbuf[i]);
		}
		sendbuf[8] = sum; // チェックサム

		// 送信
		for(int i=0;i<9;i++)
			serial.write(sendbuf[i]);
	}

	/**
	 *  サーボの現在角度を0.1度単位で得る
	 *  可動範囲の中央を0として反時計方向に-150度，時計方向に150度の範囲
	 *  
	 *  @param sId サーボID
	 *  @return 現在角度
	 */
	int getAngle(int sId) {
		this.getParam(sId);
		return ((readbuf[8] << 8) & 0x0000FF00) | (readbuf[7] & 0x000000FF);
	}
	
	/**
	 *  サーボが指令を受信し，移動を開始してからの経過時間を10msの単位で得る
	 *  移動が完了すると最後の時間を保持する
	 *  
	 *  @param sId サーボID
	 *  @return 経過時間
	 */
	int getTime(int sId) {
		this.getParam(sId);
		return ((readbuf[10] << 8) & 0x0000FF00) | (readbuf[9] & 0x000000FF);
	}
	
	/**
	 *  サーボの現在回転スピードをdeg/sec単位で得る
	 *  瞬間のスピードをあらわしている
	 *  
	 *  @param sId サーボID
	 *  @return 現在回転スピード
	 */
	int getSpeed(int sId) {
		this.getParam(sId);
		return ((readbuf[12] << 8) & 0x0000FF00) | (readbuf[11] & 0x000000FF);
	}
	
	/**
	 *  サーボの負荷をmA単位で得る
	 *  
	 *  @param sId サーボID
	 *  @return 現在負荷
	 */
	int getLoad(int sId) {
		this.getParam(sId);
		return ((readbuf[14] << 8) & 0x0000FF00) | (readbuf[13] & 0x000000FF);
	}
	
	/**
	 *  サーボの基板上の温度を得る
	 *  温度センサには個体差により +-3度程度の誤差がある
	 *  一度温度による保護機能が働くと，サーボをリセットする必要がある
	 *  
	 *  @param sId サーボID
	 *  @return 現在温度
	 */
	int getTemperature(int sId) {
		this.getParam(sId);
		return ((readbuf[16] << 8) & 0x0000FF00) | (readbuf[15] & 0x000000FF);
	}
	
	/**
	 *  サーボに供給されている電源の電圧を10mV単位で得る
	 *  およそ+-0.3V程度の誤差がある
	 *  
	 *  @param sId サーボID
	 *  @return 電源電圧
	 */
	int getVoltage(int sId) {
		this.getParam(sId);
		return ((readbuf[18] << 8) & 0x0000FF00) | (readbuf[17] & 0x000000FF);
	}
	

	
	private void getParam(int sId)
	{
		byte sum;

		// パケット作成
		sendbuf[0] = (byte) 0xFA; // ヘッダー1
		sendbuf[1] = (byte) 0xAF; // ヘッダー2
		sendbuf[2] = (byte) sId; // サーボID
		sendbuf[3] = (byte) 0x09; // フラグ(0x01 | 0x04<<1)
		sendbuf[4] = (byte) 0x00; // アドレス(0x00)
		sendbuf[5] = (byte) 0x00; // 長さ(0byte)
		sendbuf[6] = (byte) 0x01; // 個数
		// チェックサムの計算
		sum = sendbuf[2];
		for (int i = 3; i < 7; i++) {
			sum = (byte) (sum ^ sendbuf[i]);
		}
		sendbuf[7] = sum; // チェックサム

		// 送信
		for(int i=0;i<8;i++)
			serial.write(sendbuf[i]);

		// 受信のために少し待つ
		for (int i = 0; i < 10; i++) {
			if (serial.available() >= 26)
				break;

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 読み込み
		for(int i=0;i<26;i++)
			readbuf[i] = (byte)(serial.read() & 0xff);

//		if (len < 26) {
//			// 受信エラー
//			System.out.println("受信エラー");
//			// return -2;
//		}

		// 受信データの確認
//		sum = readbuf[2];
//		for (i = 3; i < 26; i++) {
//			sum = sum ^ readbuf[i];
//		}
//		if (sum) {
//			// チェックサムエラー
//			return -3;
//		}

	}

}
