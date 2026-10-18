package com.deepconnect.ats.common.security;

import lombok.extern.slf4j.Slf4j;
                    
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HashIdUtil {

    private final Hashids hashids;

    public HashIdUtil(@Value("${secret.forSigning}") String secretForSigning, @Value("${secret.minHashIdLength}") Integer minHashIdLength) {
        this.hashids = new Hashids(secretForSigning, minHashIdLength); 
    }

    public String encodeHashId(Long id) {
        return hashids.encode(id);
    }

    public Long decodeHashId(String hash) {
        long[] decoded = hashids.decode(hash);
        return decoded.length > 0 ? decoded[0] : null;
    }

}
