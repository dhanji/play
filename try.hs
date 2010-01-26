puts str = putStrLn str

-- range function
alphabet x1 x2 = if x1 == x2
                   then [x2]
                   else x1 : alphabet (succ x1) x2


main = puts (alphabet 'a' 'z')