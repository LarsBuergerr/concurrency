use std::sync::{Arc, Mutex};
use std::thread;
use std::time::Duration;

static mut LAST_NUMBER: u64 = 0;
static mut LAST_FACTORS: [u64; 100] = [0; 100];

struct FactorizerService {
    last_number: u64,
    last_factors: [u64; 100],
}

impl FactorizerService {
    fn serivce(&mut self, n: u64) -> [u64; 100] {
        let mut factors: [u64; 100] = [0; 100];

        if n == 0 {
            return factors
        }
        if n == self.last_number {
            println!("cache hit");
            return self.last_factors
        }

        Self::factorizer(& mut factors, n);
        println!("cache miss");
        self.last_number = n;
        self.last_factors = factors;
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
    let mut fac = Arc::new(Mutex::new(FactorizerService {
        last_factors: [0; 100],
        last_number: 0,
    }));
    let clone1 = Arc::clone(&fac);
    let clone2 = Arc::clone(&fac);
    for i in 0..10 {
        let calculated_factors = fac.lock().unwrap().serivce(i);
        print_result(calculated_factors, i);
    }
    let factors = fac.lock().unwrap().serivce(9);
    print_result(factors, 9);
    let handle1 = thread::spawn(move || {
        for i in 0 .. 1000 {
            let factors = clone1.lock().unwrap().serivce(i);
            print_result(factors, i);
            thread::sleep(Duration::from_micros(1));
        }
    });

    let handle2 = thread::spawn(move || {
        for i in 0 .. 1000 {
            let factors = clone2.lock().unwrap().serivce(i);
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


