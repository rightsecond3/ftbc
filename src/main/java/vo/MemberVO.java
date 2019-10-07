package vo;

import java.io.Serializable;
import java.sql.Clob;

import org.springframework.stereotype.Component;
@Component
public class MemberVO implements Serializable{
	private static final long serialVersionUID = -7354587414573995513L;
	private String mem_email     = null;
	private String mem_name      = null;
	private String mem_pw        = null;
	private Clob   mem_pfimg     = null;
	private String mem_loc       = null;
	private String mem_hp        = null;
	private String mem_publickey = null;
	private String mem_authority = null;
	private String mem_zipcode   = null;
	private String mem_nickname  = null;
	private String msg			 = null;
	private String loginResult = null;
	private String isWalletExist = null;
	public String getLoginResult() {
		return loginResult;
	}
	public void setLoginResult(String loginResult) {
		this.loginResult = loginResult;
	}
	public String getIsWalletExist() {
		return isWalletExist;
	}
	public void setIsWalletExist(String isWalletExist) {
		this.isWalletExist = isWalletExist;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMem_email() {
		return mem_email;
	}
	public void setMem_email(String mem_email) {
		this.mem_email = mem_email;
	}
	public String getMem_name() {
		return mem_name;
	}
	public void setMem_name(String mem_name) {
		this.mem_name = mem_name;
	}
	public String getMem_pw() {
		return mem_pw;
	}
	public void setMem_pw(String mem_pw) {
		this.mem_pw = mem_pw;
	}
	public String getMem_loc() {
		return mem_loc;
	}
	public void setMem_loc(String mem_loc) {
		this.mem_loc = mem_loc;
	}
	public String getMem_hp() {
		return mem_hp;
	}
	public void setMem_hp(String mem_hp) {
		this.mem_hp = mem_hp;
	}
	public String getMem_publickey() {
		return mem_publickey;
	}
	public void setMem_publickey(String mem_publickey) {
		this.mem_publickey = mem_publickey;
	}
	public String getMem_authority() {
		return mem_authority;
	}
	public void setMem_authority(String mem_authority) {
		this.mem_authority = mem_authority;
	}
	public String getMem_zipcode() {
		return mem_zipcode;
	}
	public void setMem_zipcode(String mem_zipcode) {
		this.mem_zipcode = mem_zipcode;
	}
	public String getMem_nickname() {
		return mem_nickname;
	}
	public void setMem_nickname(String mem_nickname) {
		this.mem_nickname = mem_nickname;
	}
	public Clob getMem_pfimg() {
		return mem_pfimg;
	}
	public void setMem_pfimg(Clob mem_pfimg) {
		this.mem_pfimg = mem_pfimg;
	}
}
