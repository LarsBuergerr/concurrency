use std::sync::Arc;
use std::sync::Mutex;

struct Account {
    balance: f64,
}


impl Account {
    fn new() -> Self {
        Account { balance: 0.0 }
    }

    fn deposit(&mut self, amount: f64) {
        self.balance += amount;
    }


    fn withdraw(&mut self, amount: f64) {
        if self.balance >= amount {
            self.balance -= amount;
        } else {
            println!("Insufficient funds");
        }
    }
}


fn main() {
    let account_actions = |account: &mut Account| {
        println!("depositing 100.0");
        account.deposit(100.0);
        println!("withdrawing 50.0");
        account.withdraw(50.0);
        println!("withdrawing 100.0");
        account.withdraw(100.0);
        println!("depositing 1000.0");
        account.deposit(1000.0);
    };


    let account = Arc::new(Mutex::new(Account::new()));


    let thread1 = std::thread::spawn({
        let account = account.clone();
        move || {
            account_actions(&mut account.lock().unwrap());
        }
    });
    let thread2 = std::thread::spawn({
        let account = account.clone();
        move || {
            account_actions(&mut account.lock().unwrap());
        }
    });
    thread1.join().unwrap();
    thread2.join().unwrap();
    println!("Final balance: {}", account.lock().unwrap().balance);
    println!("Thread 1 and Thread 2 have finished executing");
}
