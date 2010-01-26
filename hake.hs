-- a blaze-style grammar for maven. Simply converts build to pom.xml
import System.IO
import Data.Char
import Text.Regex.Posix


main = do
          buildFile <- openFile "build" ReadMode
          contents <- hGetContents buildFile
          
          -- strip comment lines
          print $ map mavenize (map stripComments (lines contents))
          hClose buildFile
          
-- removes any line that begins with '#'  
stripComments [] = []
stripComments (x:xs) 
    | x == '#' = [] 
    | otherwise = x:xs


-- turns a list of hake lines into maven pom.xml fragments
mavenize line 
    | line =~ repos = line
    | line =~ lib = line
    | otherwise = []
    where repos = "[ ]*repositories.*"
          lib = "[ ]*lib[(].*[)]"
          
