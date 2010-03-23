
primes = sieve [2..]
  where sieve (p:xs) = p : sieve [x|x <- xs, x `mod` p > 0]


-- ignore
primes2 = sieve [2] [2..]
  where
    sieve ps (x:xs) = if x `mod`  : sieve xs