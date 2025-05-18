use std::sync::{Arc, Mutex};
use rand::Rng;

fn main() {
    let factorizer = Arc::new(Mutex::new(FactorizerService::new()));

    let action = |factorizer: Arc<Mutex<FactorizerService>>| {
        for _ in 0..10_000 {
            let number = rand::rng().random_range(1..20);

            let factors = FactorizerService::service(&factorizer, number);
            FactorizerService::print_result(factors, number);
        }
    };

    let f1 = Arc::clone(&factorizer);
    let t1 = std::thread::spawn(move || {
        action(f1);
    });

    let f2 = Arc::clone(&factorizer);
    let t2 = std::thread::spawn(move || {
        action(f2);
    });

    t1.join().unwrap();
    println!("Thread 1 terminated!");
    t2.join().unwrap(); 
    println!("Thread 2 terminated!");
}

struct FactorizerService {
    last_number: u64,
    last_factors: [u64; 100],
}

impl FactorizerService {
    fn new() -> Self {
        Self {
            last_number: 0,
            last_factors: [0; 100],
        }
    }

    fn service(shared: &Arc<Mutex<FactorizerService>>, number: u64) -> [u64; 100] {
        {
            let guard = shared.lock().unwrap();
            if guard.last_number == number {
                println!("cache hit!");
                return guard.last_factors;
            }
            println!("cache miss!");
        }
        let mut factors = [0; 100];
        Self::factorize(&mut factors, number);

        let mut guard = shared.lock().unwrap();
        guard.last_number = number;
        guard.last_factors = factors;

        factors
    }

    fn factorize(factors: &mut [u64; 100], mut n: u64) {
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

    fn print_result(factors: [u64; 100], n: u64) {
        let mut result = format!("{} = ", n);
        for &factor in factors.iter() {
            if factor == 0 {
                break;
            }
            result.push_str(&format!("{} * ", factor));
        }
        result.truncate(result.len().saturating_sub(3));
        println!("{}\n", result);
    }
}
