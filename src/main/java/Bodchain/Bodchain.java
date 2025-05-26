package Bodchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Bodchain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    
    public static int difficulty = 3; // Difficulty level for mining
    public static Wallet walletA;
    public static Wallet walletB;
	public static float minimumTransaction = 0.1f;

    public static Transaction genesis0Transaction;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();
        // System.out.println("Public and private keys of wallet A: ");
        // System.out.println(StringUtil.stringFromKey(walletA.publicKey)+" "+StringUtil.stringFromKey(walletA.privateKey));

        genesis0Transaction = new Transaction(coinbase.publicKey, walletA.publicKey, 50, null);
        genesis0Transaction.generateSignatue(walletA.privateKey);
        genesis0Transaction.transactionId = "0";
        genesis0Transaction.outputs.add(new TransactionOutput(genesis0Transaction.receipient, genesis0Transaction.value, genesis0Transaction.transactionId));
        UTXOs.put(genesis0Transaction.outputs.get(0).id, genesis0Transaction.outputs.get(0));

        System.out.println("Creating and Mining genesis-0 Block");
        Block genesis0Block = new Block("0");
        genesis0Block.addTransaction(genesis0Transaction);
        addBlock(genesis0Block);

        Block block1 = new Block(genesis0Block.hash);
        System.out.println("\n WalletA's balance is: "+walletA.getBalance());
        System.out.println("\n WalletA is attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB Attempting to send funds (20) to WalletA");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        addBlock(block3);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

    }

    public static boolean isChainValid(){
        Block currentBlock;
        Block prevBlock;

        String target = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>();
        tempUTXOs.put(genesis0Transaction.outputs.get(0).id, genesis0Transaction.outputs.get(0));


        for(int i=1;i<blockchain.size();i++){
            currentBlock = blockchain.get(i);
            prevBlock = blockchain.get(i-1);

            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current Hash invalid");
                return false;
            }
            if(!prevBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("Previous block hash mismatch");
                return false;
            }
            if(!currentBlock.hash.substring(0, difficulty).equals(target)){
                System.out.println("Block mining failed");
                return false;
            }
            if(!currentBlock.previousHash.equals("0") && currentBlock.timeStamp < prevBlock.timeStamp){
                System.out.println("Timestamp of current block is earlier than previous block");
                return false;
            }

            TransactionOutput temOutput;
            for(int t=0;t<currentBlock.transactions.size();t++){
                Transaction currentTransaction = currentBlock.transactions.get(i);

                if(!currentTransaction.verifySignature()){
                    System.out.println("#Signature verification on Transaction("+t+") failed");
                    return false;
                }

                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()){
                    System.out.println("#Inputs are not equal to outputs");
                    return false;
                }

                for(TransactionInput input:currentTransaction.inputs){
                    temOutput = tempUTXOs.get(input.transactionOutputId);

                    if(temOutput == null){
                        System.out.println("#Referenced input Transaction("+t+") Missing");
                        return false;
                    }
                    if(input.UTXO.value != temOutput.value){
                        System.out.println("#Referenced input Transaction("+t+") value is Invalid");
                        return false;
                    }
                }

                for(TransactionOutput output:currentTransaction.outputs)
                    tempUTXOs.put(output.id, output);

                if(currentTransaction.outputs.get(0).receipient != currentTransaction.receipient){
                    System.out.println("#Transaction("+t+") receipient is not whom it should be?!!");
                    return false;
                }

                if(currentTransaction.outputs.get(1).receipient != currentTransaction.sender){
                    System.out.println("#Transaction("+t+") sender is not whom it should be?!!");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block block){
        block.mineBlock(difficulty);
        blockchain.add(block);
        System.out.println("Block added successfully!");
    }

}
