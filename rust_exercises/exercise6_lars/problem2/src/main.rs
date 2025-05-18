fn main() {



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

        }

        fn factorize(number: u64) -> [u64; 100] {
            let mut factors = [0; 100];
            let mut count = 0;
            let mut n = number;

            for i in 2..=number {
                while n % i == 0 {
                    factors[count] = i;
                    count += 1;
                    n /= i;
                }
            }

            factors
        }
    }
}
