package Bodchain;
import java.security.*;
import java.util.*;

public class StringUtil {
    // Applies Sha256 to a string and returns the result.
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String stringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static byte[] applyECDSASig(PrivateKey key, String input){
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(key);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public static boolean verifyECDSASig(PublicKey publicKey,  String data, byte[] signature){
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);   
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions){
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        
        for(Transaction t:transactions){
            previousTreeLayer.add(t.transactionId);
        }

        ArrayList<String> treeLayer = previousTreeLayer;
        while(count>1){
            treeLayer = new ArrayList<String>();
            for(int i=1;i<previousTreeLayer.size();i++){
                treeLayer.add(applySha256(previousTreeLayer.get(i-1)+previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        String merkelRoot = treeLayer.size()==1 ? treeLayer.get(0) : "";
        return merkelRoot;
    }
}
