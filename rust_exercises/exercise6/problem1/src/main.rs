use std::sync::{Arc, Mutex};
use std::thread;
fn main() {
    let play_with_bank_account = move |account: &mut Account| {
        account.deposit(50.0);
        account.withdraw(50.0);
        println!("{}", account.balance);
    };

    println!("Hello, world!");
    let account = Arc::new(Mutex::new(Account{ balance: 100.0 }));

    let pointer1 = Arc::clone(&account);
    let pointer2 = Arc::clone(&account);
    let pointer3 = Arc::clone(&account);

    println!("{}", account.lock().unwrap().balance);
    account.lock().unwrap().withdraw(30.0);
    println!("{}", account.lock().unwrap().balance);
    account.lock().unwrap().deposit(200.0);
    println!("{}", account.lock().unwrap().balance);
    account.lock().unwrap().withdraw(170.0);
    println!("account balance before Threads begin: {}", account.lock().unwrap().balance);
    let handle1 =  thread::spawn(move || {
        play_with_bank_account(&mut pointer1.lock().unwrap());
    });

    let handle2= thread::spawn(move || {
        play_with_bank_account(&mut pointer2.lock().unwrap());
    });

    let handle3= thread::spawn(move || {
        play_with_bank_account(&mut pointer3.lock().unwrap());
    });




    let _ = handle1.join();
    let _ = handle2.join();
    let _ = handle3.join();
}

struct Account {
    balance: f64,
}

impl Account {
    fn deposit(&mut self, amount: f64) {
        self.balance += amount;
    }

    fn withdraw(&mut self, amount: f64) {
        self.balance -= amount;
    }
}


