package com.deepconnect.ats.common.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deepconnect.ats.common.enums.MacAlgoType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigningUtil {

    @Value("${secret.forSigning}")
    private String secretForSigning;

    private final static MacAlgoType macAlgoType = MacAlgoType.HMAC_SHA256;

    public SecretKey getSignKey() { 
        byte[] keyBytes= Decoders.BASE64.decode(secretForSigning); 
        return Keys.hmacShaKeyFor(keyBytes); 
    } 

    public String signData(String data) throws Exception {
        return this.signData(data, macAlgoType);
    }

    public String signData(String data, MacAlgoType macAlgoType) throws Exception {
        Mac mac = Mac.getInstance(macAlgoType.getName());
        mac.init(this.getSignKey());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public Hashids getHashIdsObject(int minHashIdLength){
        return new Hashids(secretForSigning, minHashIdLength);
    }

}
