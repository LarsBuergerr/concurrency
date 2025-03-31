package concurrency.exercise2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Problem1 {
    public static void main(String[] args) {
        ExecutorService executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Account a = new Account();
        try{
            for (int i = 0; i < 1000000; i++){
                executor.execute(new MyRunnable(a));
            }
        }catch(Exception err){
            err.printStackTrace();
        }
        executor.shutdown();
        System.out.println("Account balance: " + a.balance);
    }
}

class MyRunnable implements Runnable{

    private Account account;

    public MyRunnable(Account a){
        account = a;
    }

    @Override
    public void run() {
        account.deposit(100);
        account.withdraw(100);
    }
}

class Account {

    int balance;

    Account() {
        balance = 0;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }
}