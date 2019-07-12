/*
 * Copyright: 2018 smarabbit studio.
 *
 * Licensed under the Confluent Community License; you may not use this file
 * except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @作   者： 黄开晖 (117227773@qq.com)
 * @日   期:  2019年4月9日
 */
package com.massyframework.assembly.crypto;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * Rsa助手
 */
public final class RsaHelper {
	
	public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
	
	/**
	 * 生成密钥,包含公钥和私钥
	 * @param keyLength 密钥长度，默认采用1024位
	 * @param seed {@link String},种子
	 * @return {@link Keys},密钥
	 */
	public Keys genericKeys(int keyLength, String seed) 
			throws NoSuchAlgorithmException{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
		
		SecureRandom random = new SecureRandom();
		random.setSeed(seed.getBytes());
		
		// 密钥位数
		keyPairGen.initialize(1024, random);
		
		// 密钥对
		KeyPair keyPair = keyPairGen.generateKeyPair();
		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		return new Keys(
				this.getKeyString(privateKey),
				this.getKeyString(publicKey));
	}
	
	/**
	 * 使用<code>privateKey</code>对<code>message</code>进行签名
	 * @param data {@link byte}数组,要签名的数据
	 * @param privateKey {@link String}, 私钥
	 * @return {@link String},签名的数据
	 */
	public String sign(byte[] data, String privateKey) 
			throws SignatureException{
		try {

            Signature signature = Signature.getInstance("SHA1withRSA");
            
            RSAPrivateKey key = this.loadPrivateKey(privateKey);
            signature.initSign(key);
            signature.update(data);

            byte[] signed = signature.sign();
            Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(signed);
        } catch (Exception e) {
            throw new SignatureException("RSAcontent="
            		+ this.bytesToHexString(data) + "." , e);
        }
	}

	/**
	 * 使用<code>publicKey</code>对<code>message</code>的<code>signedData</code>签名进行验证
	 * @param data {@link byte}数组,要验证签名的数据
	 * @param signedData {@link String},签名
	 * @param publicKey {@link String},验证签名的公钥
	 * @return {@link boolean},返回<code>true</code>表示验证通过，返回<code>false</code>表示验证失败
	 */
	public boolean verify(byte[] data, String signedData, String publicKey)
			throws SignatureException{
		try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            RSAPublicKey key = this.loadPublicKey(publicKey);
            signature.initVerify(key);
            signature.update(data);
            byte[] bytes = Base64.getDecoder().decode(signedData);
            return signature.verify(bytes);
        } catch (Exception e) {
            throw new SignatureException("RSA验证签名[content = " + 
            		this.bytesToHexString(data) + "]发生异常!", e);
        }
	}
	
	/**
	 * 对<code>info</code>的签名进行验证
	 * @param info
	 * @param publicKey
	 * @return {@link boolean},返回<code>true</code>表示验证通过，返回<code>false</code>表示验证失败
	 */
	public boolean verify(SignatureInformation info, String publicKey) 
			throws SignatureException{
		String message = info.getContent();
		String signedData = info.getSignedData();
		return this.verify(message.getBytes(), signedData, publicKey);
	}
	
	/**
	 * 得到密钥字符串（经过base64编码）
	 * @return
	 */
	private String getKeyString(Key key){
		byte[] keyBytes = key.getEncoded();
		Encoder encoder = Base64.getEncoder();
		String result= encoder.encodeToString(keyBytes);
		return result;
	}   
	
	/**
	 * 根据<code>privateKey</code>加载私钥
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private RSAPrivateKey loadPrivateKey(String privateKey) 
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		//通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Decoder decoder = Base64.getDecoder();
       
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(decoder.decode(privateKey));
        RSAPrivateKey result = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return result;
	}
	
	/**
	 * 根据<code>publicKey</code>加载公钥
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private RSAPublicKey loadPublicKey(String publicKey) 
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		//通过PKCS#8编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Decoder decoder = Base64.getDecoder();

        X509EncodedKeySpec pkcs8KeySpec = 
        		new X509EncodedKeySpec(decoder.decode(publicKey));
        RSAPublicKey result = (RSAPublicKey) keyFactory.generatePublic(pkcs8KeySpec);
        return result;
	}
	
	/**
	 * 二进制转十六进制
	 * @param src
	 * @return
	 */
	private String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  
}
