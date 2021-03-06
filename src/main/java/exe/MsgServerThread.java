package exe;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import blockchain.AuthChain;
import blockchain.Register;
import blockchain.Wallet;
import blockchain.util.Base64Conversion;
import blockchain.util.StringUtil;
import exe.util.Protocol;

public class MsgServerThread extends Thread {
	MsgServer msgServer = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	String loginID = null;
	// 자동 체크박스 동기화 유무 확인 - 초기값은 항상 체크되있다.
	boolean isCheck = true;

	public MsgServerThread(MsgServer msgServer) {
		this.msgServer = msgServer;
		try {
			oos = new ObjectOutputStream(msgServer.cSocket.getOutputStream());
			ois = new ObjectInputStream(msgServer.cSocket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String msg) {
		try {
			oos.writeObject(msg);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void appendLog(String msg) {
		msgServer.jta_log.append(msg + "\n");
	}

	@Override
	public void run() {
		boolean isStop = false;
		try {
			while (!isStop) {
				String msg = (String) ois.readObject();
				appendLog(msg);
				int protocol = 0;
				StringTokenizer stz = null;
				if (msg != null) {
					stz = new StringTokenizer(msg, "|");
					protocol = Integer.parseInt(stz.nextToken());
				}
				switch (protocol) {
				case Protocol.LOGIN: {
					this.loginID = stz.nextToken();
					String password = stz.nextToken();
					isCheck = true;
					//////////////// ############ DB ################
					// # DB 전송 후 로그인이 성공 됬다고 가정 - 성공 : 성공 실패 : 실패
					String loginResult = loginID;
					// # DB 전송 publicKey의 유무 -> 지갑의 유무 -> 존재 : publicKey를 가져옴 | 비존재 : 공개키없음
					// String isWalletExist =
					// "rO0ABXNyACxvcmcuYm91bmN5Y2FzdGxlLmpjZS5wcm92aWRlci5KQ0VFQ1B1YmxpY0tleY6Gt8LRnIUNAwAFWgAPd2l0aENvbXByZXNzaW9uTAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7TAAGZWNTcGVjdAAkTGphdmEvc2VjdXJpdHkvc3BlYy9FQ1BhcmFtZXRlclNwZWM7TAAKZ29zdFBhcmFtc3QAQExvcmcvYm91bmN5Y2FzdGxlL2FzbjEvY3J5cHRvcHJvL0dPU1QzNDEwUHVibGljS2V5QWxnUGFyYW1ldGVycztMAAFxdAAiTG9yZy9ib3VuY3ljYXN0bGUvbWF0aC9lYy9FQ1BvaW50O3hwdXIAAltCrPMX+AYIVOACAAB4cAAAAEswSTATBgcqhkjOPQIBBggqhkjOPQMBAQMyAATO6pC9pDgcpWJwDY54q6+qJApbFcFPUF9SeYIlS+rItB+E2caPcsSmJx59Jx7ANUZ0AAVFQ0RTQXcBAHg=";
					String isWalletExist = "공개키없음";
					// 로그인이 성공 되었을 때만 실행
					if (loginID.equals(loginResult)) {
						Object keys[] = msgServer.globalMaps.keySet().toArray();
						for (int i = 0; i < msgServer.globalMaps.size(); i++) {
							if (keys[i].equals(loginID)) {
								loginResult = "중복로그인";
								break;
							}
						}
						msgServer.globalMaps.put(loginID, this);
						System.out.println("글로벌 맵 사이즈 : " + msgServer.globalMaps.size());
					}
					this.send(Protocol.LOGIN + Protocol.seperator + loginResult + Protocol.seperator + isWalletExist);
				}
					break;
				case Protocol.EXIT: {
					msgServer.globalMaps.remove(loginID);
					this.send(Protocol.EXIT+Protocol.seperator+loginID);
					isStop = true;
					if (oos != null)
						oos.close();
					if (ois != null)
						ois.close();
					if (msgServer.cSocket != null)
						msgServer.cSocket.close();
				}
					break;
				case Protocol.CHAIN_AUTHENTICATE_START: {
					boolean isHashValid = false;
					boolean isEqual = false;
					AuthChain authChain = new AuthChain();
					// 파일 검사 부
					try {
						authChain.decodeInitialization(loginID);
					} catch (Exception e) {
						this.send(Protocol.BASE64_DECODE_EXCEPTION + Protocol.seperator + loginID);
						break;
					}
					try {
						isHashValid = authChain.isHashValid();
						if (isHashValid == false) {
							this.send(Protocol.NOT_HASH_VALID + Protocol.seperator + loginID);
							break;
						}
						// 두 체인의 사이즈 비교
						isEqual = authChain.isChainComparation(loginID);
						if (isEqual) {
							this.send(Protocol.ALREADY_SYNC + Protocol.seperator + loginID);
						} else {
							this.send(Protocol.ADD_BLOCK + Protocol.seperator + loginID);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					break;
				case Protocol.SEND_KEY_PAIR: {
					String publicBase64 = stz.nextToken();
					String privateBase64 = stz.nextToken();
					// 공개키와 개인키의 짝이 맞는지 확인후 박아주기
					PublicKey publicKey = null;
					PrivateKey privateKey = null;
					try {
						publicKey = (PublicKey) Base64Conversion.decodeBase64(publicBase64);
						privateKey = (PrivateKey) Base64Conversion.decodeBase64(privateBase64);
					} catch (Exception e) {
						// 사용자가 보낸 개인키가 올바르지 않을 경우
						this.send(Protocol.DAMAGED_PRIVATE_KEY + Protocol.seperator);
					}
					if (StringUtil.isValidKeys(publicKey, privateKey)) {
						this.send(Protocol.VALIDATED_KEYS + Protocol.seperator + privateBase64);
					} else {
						this.send(Protocol.UNVERIFIED_KEY + Protocol.seperator);
					}
				}
					break;
				case Protocol.CREATE_NEW_WALLET: {
					Register register = new Register();
					// 지갑 생성
					Wallet wallet = register.createMoneyWallet();
					// base64 코드 보내기
					String publicBase64 = Base64Conversion.encodePublicKey(wallet.getPublicKey());
					String privateBase64 = Base64Conversion.encodePrivateKey(wallet.getPrivateKey());
					this.send(Protocol.SEND_NEW_KEYS + Protocol.seperator + publicBase64 + Protocol.seperator
							+ privateBase64);
					// # DB에 넣어줘야함
				}
					break;
				case Protocol.CHECKBOX_CHECK: {
					this.isCheck = true;
					appendLog("클라이언트가 체크박스 선택!");
				}
					break;
				case Protocol.CHECKBOX_UNCHECK: {
					this.isCheck = false;
					appendLog("클라이언트가 체크박스 선택해제!");
				}
					break;
				case Protocol.ALERT_ADD_BLOCK: {
					appendLog("서블릿에서 온 블록 추가 신호!!");
					// # 파일 다운로드 할 녀석들만 추리자 일단
					this.send(Protocol.SERVLET_DISCONNECT+Protocol.seperator);
					isStop = true;
					if (ois != null)
						ois.close();
					if (oos != null)
						oos.close();
					if (msgServer.cSocket != null)
						msgServer.cSocket.close();
					Map<String, MsgServerThread> globalMaps = msgServer.globalMaps;
					msgServer.syncClientList = new Vector<>();
					if (globalMaps != null) {
						Object keys[] = globalMaps.keySet().toArray();
						msgServer.syncClientList = new Vector<String>();
						for (int i = 0; i < globalMaps.size(); i++) {
							MsgServerThread msgServerThread = globalMaps.get(keys[i]);
							if (msgServerThread.isCheck) {
								msgServer.syncClientList.add(msgServerThread.loginID);
								if (i == globalMaps.size()-1) { // 마지막 파일일 경우
									msgServer.jta_log.append("i의 값 : " + i + ", 로그인 아이디 : " + msgServerThread.loginID + "\n");
									msgServerThread.send(Protocol.AUTOSYNC_UPLOAD_START_LAST + Protocol.seperator
											+ msgServerThread.loginID);
									// 프로토콜이 가는 속도를 늦춤
									Thread.sleep(250);
								} else {
									msgServerThread.send(Protocol.AUTOSYNC_UPLOAD_START + Protocol.seperator
											+ msgServerThread.loginID);
									Thread.sleep(250);
								}
							}
						}
					}

				}
					break;
				case Protocol.AUTOSYNC_CHAIN_DOWNLOAD_ALERT: {
					appendLog("AUTOSYNC_CHAIN_DOWNLOAD_ALERT");
					// # 자동동기화 체크박스 되어잇는 사람들 체인 다운로드
					this.send(Protocol.SERVLET_DISCONNECT+Protocol.seperator);
					isStop = true;
					if (ois != null)
						ois.close();
					if (oos != null)
						oos.close();
					if (msgServer.cSocket != null)
						msgServer.cSocket.close();
					Map<String, MsgServerThread> globalMaps = msgServer.globalMaps;
					if (globalMaps != null) {
						Object keys[] = globalMaps.keySet().toArray();
						for (int i = 0; i < globalMaps.size(); i++) {
							MsgServerThread msgServerThread = globalMaps.get(keys[i]);
							if (msgServerThread.isCheck) {
								// 자동동기화 체크박스가 되있는 클라이언트들에게 파일 다운로드 - 다운로드 시킬때는 다 다운로드 시킨다.
								msgServerThread.send(Protocol.AUTOSYNC_CHAIN_DOWNLOAD + Protocol.seperator);
								MsgServerThread.sleep(250);
							}
						}
					}
				}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
