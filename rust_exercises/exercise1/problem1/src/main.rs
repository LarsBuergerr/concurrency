use rand::Rng;

fn main() {
    for i in 0..100000 {
        let mut rng = rand::rng();
        let n = rng.random_range(1..1000);
        
        let calculated_factors = service(n);
        print_result(calculated_factors, n);
    }
}



fn service(n: u64) -> [u64; 100] {
    let mut factors: [u64; 100] = [0; 100];

    if n == 0 {
        return factors;
    }

    factors = factorizer(factors, n);

    return factors;
}

fn factorizer(mut factors: [u64; 100], mut n: u64) -> [u64; 100] {
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

    return factors;
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

