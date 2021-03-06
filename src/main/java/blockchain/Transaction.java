package blockchain;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import blockchain.util.StringUtil;



public class Transaction implements Serializable {
	private static final long serialVersionUID = -7729233203265660208L;
	public String txId;
	public PublicKey sender;
	public PublicKey reciepient;
	public long value;
	// 무슨 선물을 선택했는지 알기 위한 코드ㅛ
	public String giftCode;
	public byte[] signature;
	public List<Input> inputs = new ArrayList<Input>();
	public List<Output> outputs = new ArrayList<Output>();
	public static int sequence = 0;

	public Transaction(PublicKey from, PublicKey to, String giftCode, long value, List<Input> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.giftCode = giftCode;
		this.value = value;
		this.inputs = inputs;
	}
	public String calculateHash() {
		sequence++;
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender)
				+ StringUtil.getStringFromKey(reciepient)
				+ Long.toString(value)
				+ giftCode
				+ sequence
				);
	}
	// 변조하고 싶지 않은 모든 데이터에 서명함.
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) 
					+ StringUtil.getStringFromKey(reciepient)
					+ Long.toString(value)
					+ giftCode;
		signature = StringUtil.applyECDSASig(privateKey, data);
	}
	// 서명 한 데이터가 변조되지 않았는지 확인
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) 
				+ StringUtil.getStringFromKey(reciepient)
				+ Long.toString(value)
				+ giftCode;
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
	// 새 트랜잭션을 만들 수 있는 경우 true를 반환
	public boolean processTransaction(BlockChain blockChain) {
		if(verifiySignature() == false) {
			System.out.println("#트랜잭션 서명 증명 실패");
			return false;
		}
		// 트랜잭션 입력을 수집한다.(사용하지 않았는지 확인)
		for(Input i : inputs) {
			i.UTXO = blockChain.UTXOs.get(i.outputId);
		}
		// 거래가 유효한지 확인
		if(getInputsValue() < blockChain.minimumTransaction) {
			System.out.println("#Transaction Inputs to small : "+getInputsValue());
			return false;
		}
		// 트랜잭션 출력을 생성
		long leftOver = getInputsValue() - value; // 인풋한 값을 얻어오고 잔액을 남긴다.
		txId = calculateHash();
		// 수신자에게 값을 보냄
		outputs.add(new Output(this.reciepient, value, txId, giftCode));
		// 남은 잔액을 발신자에게 다시 보낸다.
		outputs.add(new Output(this.sender, leftOver, txId, giftCode));
		// 미사용 목록에 출력 추가
		for(Output o : outputs) {
			blockChain.UTXOs.put(o.id, o);
		}
		// 지출 한대로 UTXO 목록에서 트랜잭션 입력을 제거 한다.
		for(Input i : inputs) {
			if(i.UTXO == null) continue; // 거래를 찾을 수 없으면 건너 뛰라
			blockChain.UTXOs.remove(i.UTXO.id);
		}
		return true;
	}
	// 입력 합계(UTXO) 값을 반환한다.
	public int getInputsValue() {
		int total = 0;
		for(Input i : inputs) {
			if(i.UTXO == null) continue; // 거래를 찾을 수 없으면 건너 뛰어라.
			total += i.UTXO.value;
		}
		return total;
	}	
	// 출력 합계를 반환합니다.
	public float getOutputsValue() {
		float total = 0;
		for(Output o : outputs) {
			total += o.value;
		}
		return total;
	}
}
