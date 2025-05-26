package Bodchain;

public class TransactionInput {
    public String transactionOutputId;
    public TransactionOutput UTXO;

    public TransactionInput(String transactionId){
        this.transactionOutputId = transactionId;
    }
}
