package com.deepconnect.ats.common.security;

import io.jsonwebtoken.Jwts.ENC;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.AeadAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deepconnect.ats.common.enums.EncryptionAlgoType;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptionUtil {

    @Value("${secret.forEncryption}")
    private String secretForEncryption;

    public final static AeadAlgorithm encAlgoType = ENC.A256GCM;
    public SecretKey getEncryptionKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretForEncryption);
        return new SecretKeySpec(keyBytes, EncryptionAlgoType.AES.getName()); 
    }

}
