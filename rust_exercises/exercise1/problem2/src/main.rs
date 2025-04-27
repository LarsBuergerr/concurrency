use rand::Rng;

static mut LAST_NUMBER: u64 = 0;
static mut LAST_FACTORS: [u64; 100] = [0; 100];

fn main() {
    for _ in 0..100_000 {
        let mut rng = rand::rng();
        let n = rng.random_range(1..20);
        
        let calculated_factors = service(n);
        print_result(calculated_factors, n);
    }
}



fn service(n: u64) -> [u64; 100] {
    let mut factors: [u64; 100] = [0; 100];

    if n == 0 {
        return factors;
    }
    unsafe {
        if n == LAST_NUMBER {
            println!("cache hit");
            return LAST_FACTORS;
        }
    }
    factorizer(& mut factors, n);
    println!("cache miss");
    unsafe {
        LAST_NUMBER = n;
        LAST_FACTORS = factors;
    }
    return factors;
}

fn factorizer(factors: &mut [u64; 100], mut n: u64) {
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


// use rand::Rng;

// static mut LAST_NUMBER: u64 = 0;
// static mut LAST_FACTORS: [u64; 100] = [0; 100];

// fn main() {
//     for i in 0..100000 {
//         let mut rng = rand::rng();
//         let n = rng.random_range(1..20);
        
//         let calculated_factors = service(n);
//         print_result(calculated_factors, n);
//     }
// }



// fn service(n: u64) -> [u64; 100] {
//     unsafe {
//         if n == LAST_NUMBER {
//             println!("cache hit");
//             return LAST_FACTORS;
//         }
//     factorizer(&mut *&raw mut LAST_FACTORS, n);
//     println!("cache miss");
//         LAST_NUMBER = n;
//         return LAST_FACTORS;
//     }
// }

// fn factorizer(factors: &mut [u64; 100], mut n: u64) {
//     factors = [0; 100];
//     let mut i = 0;
//     let upper_limit = (n as f64).sqrt() as u64 + 1;
//     for j in 2..upper_limit {
//         while n % j == 0 {
//             factors[i] = j;
//             i += 1;
//             n /= j;
//         }
//     }

//     if n > 1 {
//         factors[i] = n;
//     }
// }

// fn print_result(factors: [u64; 100], n: u64) {
//     let mut result = String::new();
//     result.push_str(&(n.to_string() + " = "));

//     for i in 0..factors.len() + 1 {
        
//         if factors[i] == 0 {
//             break;
//         }
//         result.push_str(&factors[i].to_string());
//         result.push_str(" * ");
//     }
//     result.pop();
//     result.pop();
//     println!("{} \n", result);
// }


