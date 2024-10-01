package com.cryptofundamentals;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class UseBouncyCastle {
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void please() {
        
    }
    
}