
fn main() {
    let factors: [u64; 100] = [0; 100];

    let calculated_factors = service(20);

    println!("Factors: {:?}", calculated_factors);

}



fn service(mut n: u64) -> [u64; 100] {
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
        i += 1;
    }

    return factors;
}

