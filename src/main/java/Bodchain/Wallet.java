package Bodchain;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PublicKey publicKey;
    public PrivateKey privateKey;
    
    public Map<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

    public Wallet(){
        generateKeyPair();
    }

    public void generateKeyPair(){
        try {  
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            //Initialize the key generator and generate KeyPair
            keyGen.initialize(ecSpec, random);

            KeyPair keyPair = keyGen.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance(){
        float total = 0;
        for(Map.Entry<String,TransactionOutput> item:Bodchain.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)){
                this.UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;  
            } 
        }
        // System.out.println("Total of "+publicKey+" is: "+total);
        return total;
    }

    public Transaction sendFunds(PublicKey receipient, float value){
        if(getBalance() < value){
            System.out.println("Insufficient funds");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        float total=0;
        for(Map.Entry<String,TransactionOutput> item:this.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total+=UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total>=value){
                break;
            }
        }

        Transaction transaction = new Transaction(publicKey, receipient, value, inputs);
        transaction.generateSignatue(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        return transaction;
    }
}
