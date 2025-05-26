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
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction(){
        if(verifySignature() == false){
            System.out.println("Transaction signature verification failed~!!!");
            return false;
        }
        for(TransactionInput i: inputs){
            i.UTXO = Bodchain.UTXOs.get(i.transactionOutputId);
        }
        
        if(getInputsValue() < Bodchain.minimumTransaction){
            System.out.println("Transaction Input too small: "+getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(receipient, value, transactionId));
        outputs.add(new TransactionOutput(sender, leftOver, transactionId));

        for(TransactionOutput o: outputs){
            Bodchain.UTXOs.put(o.id, o);
        }

        for(TransactionInput i:inputs){
            if(i.UTXO == null) continue;
            Bodchain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    public float getInputsValue(){
        float total=0;
        for(TransactionInput i:inputs){
            if(i.UTXO==null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue(){
        float total=0;
        for(TransactionOutput o:outputs){
            total+=o.value;
        }
        return total;
    }
}
