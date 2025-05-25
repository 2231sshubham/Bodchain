package com.build;

import java.util.ArrayList;

public class Bodchain {
    static int difficulty = 6; // Difficulty level for mining
    public static ArrayList<block> blockchain = new ArrayList<>();
    public static void main(String[] args) {
        blockchain.add(new block("First Block", "0"));
        blockchain.get(0).mineBlock(difficulty);
        blockchain.add(new block("Second Block", blockchain.get(0).hash));
        blockchain.get(1).mineBlock(difficulty);
        blockchain.add(new block("Third Block", blockchain.get(1).hash));   
        blockchain.get(2).mineBlock(difficulty);

        // for(block b: blockchain){
        //     System.out.println(b.data+" "+b.hash);
        // }
        if(isChainValid()) System.out.println("Blockchain is valid");
        else System.out.println("Blockchain is invalid");
    }

    public static boolean isChainValid(){
        block currentBlock;
        block prevBlock;
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
