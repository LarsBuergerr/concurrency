use std::sync::Mutex;
use std::sync::Arc;

use rand::Rng;

fn main() {
    let factorizer = Arc::new(Mutex::new(FactorizerService::new()));

    let action = |factorizer: &mut FactorizerService| {
        for _ in 0..10_000 {
            let mut rng = rand::rng();
            let number = rng.random_range(1..20);

            let factors = factorizer.service(number);
            factorizer.print_result(factors, number);
        }
    };
    
    let t1 = std::thread::spawn({
        let factorizer = Arc::clone(&factorizer);
        move || {
            let mut factorizer = factorizer.lock().unwrap();
            action(&mut factorizer);
        }
    });

    let t2 = std::thread::spawn({
        let factorizer = Arc::clone(&factorizer);
        move || {
            let mut factorizer = factorizer.lock().unwrap();
            action(&mut factorizer);
        }
    });

    t1.join().unwrap();
    print!("Thread 1 terminated!");
    t2.join().unwrap(); 
    println!("Thread 2 terminated!");
}

struct FactorizerService {

    last_number: u64,
    last_factors: [u64; 100]
}

impl FactorizerService {
    fn new() -> Self {
        Self {
            last_number: 0,
            last_factors: [0; 100]
        }
    }

    fn service(&mut self, number: u64) -> [u64; 100] {
        if self.last_number == number {
            println!("cache hit!");
            return self.last_factors;
        }
        println!("cache miss!");
        self.last_number = number;
        let mut factors = [0; 100];
        Self::factorize(&mut factors, number);
        self.last_factors = factors;

        factors
    }

    fn factorize(factors: &mut[u64; 100], mut n: u64) {
        let mut i = 0;
        let upper_limit = (n as f64).sqrt() as u64 + 1;
        for j in 2..upper_limit {
            while n % j == 0 {
                factors[i] = j;
                i += 1;
                n /= j;
            }
        }

        if n > 1 {
            factors[i] = n;
        }
    }

    fn print_result(&mut self, factors: [u64; 100], n: u64) {
        let mut result = String::new();
        result.push_str(&(n.to_string() + " = "));

        for i in 0..factors.len() + 1 {
            
            if factors[i] == 0 {
                break;
            }
            result.push_str(&factors[i].to_string());
            result.push_str(" * ");
        }
        result.pop();
        result.pop();
        println!("{} \n", result);
    }
}
