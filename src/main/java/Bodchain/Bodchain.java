package Bodchain;

import java.security.Security;
import java.util.ArrayList;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Bodchain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static int difficulty = 5; // Difficulty level for mining
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();
        System.out.println("Public and private keys of wallet A: ");
        System.out.println(StringUtil.stringFromKey(walletA.publicKey)+" "+StringUtil.stringFromKey(walletA.privateKey));
    }

    void mineBlock(){
        blockchain.add(new Block("First Block", "0"));
        blockchain.get(0).mineBlock(difficulty);
        blockchain.add(new Block("Second Block", blockchain.get(0).hash));
        blockchain.get(1).mineBlock(difficulty);
        blockchain.add(new Block("Third Block", blockchain.get(1).hash));   
        blockchain.get(2).mineBlock(difficulty);

        // for(block b: blockchain){
        //     System.out.println(b.data+" "+b.hash);
        // }
        if(isChainValid()) System.out.println("Blockchain is valid");
        else System.out.println("Blockchain is invalid");
    }

    public static boolean isChainValid(){
        Block currentBlock;
        Block prevBlock;
        String target = new String(new char[difficulty]).replace('\0', '0');
        for(int i=1;i<blockchain.size();i++){
            currentBlock = blockchain.get(i);
            prevBlock = blockchain.get(i-1);

            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current Hash invalid");
                return false;
            }
            else if(!prevBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("Previous block hash mismatch");
                return false;
            }
            else if(!currentBlock.hash.substring(0, difficulty).equals(target)){
                System.out.println("Block mining failed");
                return false;
            }
            else if(!currentBlock.previousHash.equals("0") && currentBlock.timeStamp < prevBlock.timeStamp){
                System.out.println("Timestamp of current block is earlier than previous block");
                return false;
            }
        }
        return true;
    }
}
