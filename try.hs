puts str = putStrLn str

<<<<<<< HEAD:try.hs
-- range function
alphabet x1 x2 = if x1 == x2
                   then [x2]
                   else x1 : alphabet (succ x1) x2

<<<<<<< HEAD:try.hs

main = puts (alphabet 'a' 'z')

=======
glom ls = [ st | st <- ls ]
>>>>>>> 3dd62feb0ea35e5de74e826cf25add2768f90255:try.hs
=======
-- operational transform function
insert st c index version = 
>>>>>>> 526209b3e5deca765f7927a737554eb201518ec1:try.hs
