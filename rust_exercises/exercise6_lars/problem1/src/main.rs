use std::sync::{Arc, Mutex};
use std::thread;

struct Account {
    balance: f64,
}

impl Account {
    fn new() -> Self {
        Account { balance: 0.0 }
    }

    fn deposit(&mut self, amount: f64) {
        println!("Depositing {}", amount);
        self.balance += amount;
    }

    fn withdraw(&mut self, amount: f64) {
        if self.balance >= amount {
            println!("Withdrawing {}", amount);
            self.balance -= amount;
        } else {
            println!("Insufficient funds for withdrawal of {}", amount);
        }
    }
}

enum Action {
    Deposit(f64),
    Withdraw(f64),
}

fn execute(account: Arc<Mutex<Account>>, action: Action) {
    let mut acc = account.lock().unwrap();
    match action {
        Action::Deposit(amount) => acc.deposit(amount),
        Action::Withdraw(amount) => acc.withdraw(amount),
    }
}

fn main() {
    let account = Arc::new(Mutex::new(Account::new()));
    let mut handles = vec![];

    for i in 0..10 {
        let account_clone = Arc::clone(&account);
        let action = if i % 2 == 0 {
            Action::Deposit(50.0)
        } else {
            Action::Withdraw(50.0)
        };

        let handle = thread::spawn(move || {
            execute(account_clone, action);
        });

        handles.push(handle);
    }

    for handle in handles {
        handle.join().unwrap();
    }

    println!("Final balance: {}", account.lock().unwrap().balance);
}
