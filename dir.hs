
import System.Directory
import System.Environment

main = do readDir ["."]

readDir [] [] = []
readDir (dir) list = do getDirectoryContents dir list
readDir (dir:dirs) list = list : (readDir dirs list)
