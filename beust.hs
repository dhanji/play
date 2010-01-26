

list [] n =  (list [0] n)
list ls n = list (ls ++ [(head ls)] +1) n
