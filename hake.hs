-- a blaze-style grammar for maven. Simply converts build to pom.xml
import System.IO
import Data.Char
import Text.Regex.Posix


main = do
          buildFile <- openFile "build" ReadMode
          contents <- hGetContents buildFile
          
          -- strip comment lines
          print $ (glomify (map stripComments (lines contents)))
          hClose buildFile
          
-- removes any line that begins with '#' and removes leading whitespace
stripComments [] = []
stripComments (x:xs)
    | x == '#' = [] 
    | otherwise = trim (x:xs)
    -- should probably use a library func hehe
    where trim [] = []
          trim (x:xs)
              | x == ' ' = xs
              | otherwise = x:xs


-- gloms sequences of non-empty lines into a single string
glomify :: [String] -> [String]
glomify ls = glom ls []
  where glom [] out = out
        glom (x:xs) out
          | null x = glom xs (out ++ [""])
          | otherwise = glom xs (init out ++ [last out ++ x])

-- turns a list of hake lines into maven pom.xml fragments
mavenize line 
    | line =~ repos = line
    | line =~ lib = line
    | otherwise = []
    where repos = "[ ]*repositories.*"
          lib = "[ ]*lib[(].*[)]"
          
