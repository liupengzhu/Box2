package com.larunda.selfdialog.util;

import android.content.Context;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;


import java.io.IOException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


/**
 * Created by sddt on 18-2-7.
 */

public class FingerprintUtil {
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private KeyGenParameterSpec.Builder builder;
    private Cipher defaultCipher;
    private Context context;
    private CancellationSignal cancellationSignal;
    private FingerprintOnClickListener fingerprintOnClickListener;


    public FingerprintUtil(Context context) {
        this.context = context;

        //KeyStore 是用于存储 获取密钥key的容器
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        //想要生成key 如果是对称加密 就需要KeyGenerator类
        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        //获得KeyGenerator对象后就可以生成一个key了；

        try {
            mKeyStore.load(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder = new KeyGenParameterSpec.Builder("ndroidKeyStore", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mKeyGenerator.init(builder.build());
                mKeyGenerator.generateKey();
            }

        } catch (IOException | NoSuchAlgorithmException | CertificateException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        //创建并初始化Cipher对象

        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        //初始化Cipher对象
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey("ndroidKeyStore", null);
            defaultCipher.init(Cipher.ENCRYPT_MODE, key);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void start() {
        cancellationSignal = new CancellationSignal();
        FingerprintManager manager = (FingerprintManager) context.getSystemService(context.FINGERPRINT_SERVICE);
        manager.authenticate(new FingerprintManager.CryptoObject(defaultCipher), cancellationSignal,
                0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (fingerprintOnClickListener != null) {
                            fingerprintOnClickListener.onError(errorCode, errString);
                        }

                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        if (fingerprintOnClickListener != null) {
                            fingerprintOnClickListener.onSuccess(result);
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        if (fingerprintOnClickListener != null) {
                            fingerprintOnClickListener.onFailed();
                        }
                    }
                }, null);
    }

    public void stopListening() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    public interface FingerprintOnClickListener {
        void onError(int errorCode, CharSequence errString);

        void onSuccess(FingerprintManager.AuthenticationResult result);

        void onFailed();
    }

    public void setFingerprintOnClickListener(FingerprintOnClickListener fingerprintOnClickListener) {
        this.fingerprintOnClickListener = fingerprintOnClickListener;
    }
}
