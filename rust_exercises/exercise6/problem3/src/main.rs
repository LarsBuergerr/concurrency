use std::sync::{Arc, Mutex};
use std::thread;
use std::time::Duration;

struct FactorizerService {
    last_number: u64,
    last_factors: [u64; 100],
}

impl FactorizerService {
    fn serivce(serivce: &Arc<Mutex<FactorizerService>>, n: u64) -> [u64; 100] {
        let mut factors: [u64; 100] = [0; 100];

        if n == 0 {
            return factors;
        }

        {
            let mut lock = serivce.lock().unwrap();
            if n == lock.last_number {
                println!("cache hit");
                return lock.last_factors;
            }
        }
        Self::factorizer(& mut factors, n);
        {
            println!("cache miss");
            let mut lock = serivce.lock().unwrap();
            lock.last_number = n;
            lock.last_factors = factors;
        }
        factors
    }

    fn factorizer(factors: &mut[u64; 100], mut n: u64) {
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
}

fn main() {
    let fac = Arc::new(Mutex::new(FactorizerService {
        last_factors: [0; 100],
        last_number: 0,
    }));
    let clone1 = Arc::clone(&fac);
    let clone2 = Arc::clone(&fac);
    let handle1 = thread::spawn(move || {
        for i in 0 .. 1000 {
            let factors = FactorizerService::serivce(&clone1, i);
            print_result(factors, i);
            thread::sleep(Duration::from_micros(1));
        }
    });

    let handle2 = thread::spawn(move || {
        for i in 0 .. 1000 {
            let factors = FactorizerService::serivce(&clone2, i);
            print_result(factors, i);
            thread::sleep(Duration::from_micros(1));
        }
    });

    let _ = handle1.join();
    let _ = handle2.join();
}

fn print_result(factors: [u64; 100], n: u64) {
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