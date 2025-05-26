package Bodchain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; // also the hash of transaction
    public PublicKey sender;     
    public PublicKey receipient;
    public float value;    // amount transferred
    public byte[] signature;   // to verify transaction

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
    
    private static int sequence = 0;

    public Transaction(PublicKey sender, PublicKey receiver, float val, ArrayList<TransactionInput> inputs){
        this.sender = sender;
        this.receipient = receiver;
        this.value = val;
        this.inputs = inputs;
    }

    public String calculateHash(){
        sequence++;
        return StringUtil.applySha256(
            StringUtil.stringFromKey(sender) +
            StringUtil.stringFromKey(receipient) +
            Float.toString(value) + sequence
        );
    }

    public void generateSignatue(PrivateKey privateKey){
        String data = StringUtil.stringFromKey(sender)+StringUtil.stringFromKey(receipient)+Float.toString(value);
        this.signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature(){
        String data = StringUtil.stringFromKey(sender)+StringUtil.stringFromKey(receipient)+Float.toString(value);
        return StringUtil.verifyECDSASig(receipient, data, signature);
    }
}
