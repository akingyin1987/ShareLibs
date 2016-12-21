package com.zlcdgroup.libs.ocr.rsa;

/**
 * 生成RSA明钥和秘钥
 * 
 * @author Administrator
 *
 */
public class RSACreator {
	public static String testPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIwqH0VWCeolPgTNjZO1qaQvBDWXBNfBijwaqxkjH3GSIZEureF+LgmY/ivHCKEtYGiqviPa+ThfctJPUQ3e7+8zacbdOkqW7nfSVRdrPxsQiph1lCCWHT8fFPlGV0tIfkjhOlnn2CrXA/TbHxjdz2hnKaDn+hXafCcxJRS5QZFwIDAQAB";
	public static String testPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIjCofRVYJ6iU+BM2Nk7WppC8ENZcE18GKPBqrGSMfcZIhkS6t4X4uCZj+K8cIoS1gaKq+I9r5OF9y0k9RDd7v7zNpxt06Spbud9JVF2s/GxCKmHWUIJYdPx8U+UZXS0h+SOE6WefYKtcD9NsfGN3PaGcpoOf6Fdp8JzElFLlBkXAgMBAAECgYArZgA45gMENCUDz78cqG4m98kAxlgR7qhvBt0g/nCmpi4g4NTxZ4kSlwiG/h+EYVfTuZuz7rlRyjhW9hxintrhRkBvp1zCXCTgoYCiGZlmTvTUcLyvcEqyVX6qq4DKWXcVly72nhTGKyBB4GmEiLSObOJYHd2dO6KroTSyDra4SQJBAP+PJEK1JMOEclrtneHujborb0J0RovZCsLGGfJJp1ZXeVb+yh0uCJ/QAqlaX8GrIUEg/yejyLtbl3DTl1TDREsCQQCI/wce6lXjGBRHMSU7SZIYh2OJKUXtDnJZ1VlUH1lijbx3dynOGrpF3/zSSdhaTm0m4Vrj1X5Krg/bCQ3MgsblAkEA93TAdMN4VMXUAU3iuhnHLITQV/XFNbc1H0K5bw14tjc/bEiMptKjUTQWz6uN2zb8nVb5GoYSYbEfpAWOnGnznwJAenKAGdjX9YfjMBK0NX63r2brx3/1eUHtYW/5TOBMU3NeHeWLnyVyktv2LLtGPslUdGj+hR+gpv1XPK6l/8JwJQJBANGRuDRYx4nZxCFWdjz2su2nXb7E7/B60I1f0XZnz/YsA/S2+ekm5fZkpIpEm1SBTfBkhxXEaiUj2qfgwu+wC9o=";

	public static void main(String[] args) {
		// RSA.generateKeyPair();
		String oldOrgTxt = "test is just test!";
		String txt = RSA.encrypt(testPublicKey, oldOrgTxt);
		System.out.println("加密后网络传输的文本：" + txt);
		System.out.println("加密后网络传输的文本长度：" + txt.length());
		String orgTxt = RSA.decrypt(testPrivateKey, txt);
		System.out.println("源数据：" + orgTxt);
		System.out.println(orgTxt.equalsIgnoreCase(oldOrgTxt));
	}

}
