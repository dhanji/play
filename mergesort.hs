

mergesort [] = []
mergesort [x] = [x]
mergesort xs = merge left ++ (merge right)
         where (left, right) = split xs
