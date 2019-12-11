package com.kma.securechatapp.core.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
    static KeyFactory kf;
    PublicKey publicKey;
    PrivateKey privateKey;
    public RSA() throws NoSuchAlgorithmException {
        if (kf == null) {
            KeyFactory kf = KeyFactory.getInstance("RSA");
        }
    }

    public void setPrivateKey(String base64String) throws   InvalidKeySpecException {

        PKCS8EncodedKeySpec specPriv = new PKCS8EncodedKeySpec(RSAUtil.base64Decode(base64String));
        PrivateKey privKey = kf.generatePrivate(specPriv);
        this.privateKey = privKey;

    }

    public void  setPublicKey(String base64String) throws  InvalidKeySpecException {

        X509EncodedKeySpec spec  = new X509EncodedKeySpec(RSAUtil.base64Decode(base64String));
        PublicKey pubKey = kf.generatePublic(spec);
        this.publicKey = pubKey;
    }

    public byte[] encrypt(final String plain) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {


        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plain.getBytes());
        return encryptedBytes;
    }

    public String decrypt(final byte[] encryptedBytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decrypted = new String(decryptedBytes);

        return decrypted;
    }



}
