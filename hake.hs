-- a blaze-style grammar for maven. Simply converts build to pom.xml
import System.IO
import Data.Char
import Text.Regex.Posix


main = do
          buildFile <- openFile "build" ReadMode
          contents <- hGetContents buildFile
          
          -- strip comment lines
          print $ map mavenize (glomify (map stripComments (lines contents)))
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
    | line =~ repos = parseRepositories line
    | line =~ proj = parseProject line ++ (parseDeps $ (line =~ deps :: String) =~ "\\[.*\\]")
    | line =~ plugins = parsePlugins line
    | otherwise = []
    where repos = "[ ]*repositories.*"
          proj = "[ ]*project.*"
          deps = "(deps[ ]*:).*\\]"
          plugins = "[ ]*plugins.*"
          
          
-- parsing rule for repositories line
parseRepositories st = xmlTag "repositories" (concat 
                          [ repo url | url <- urls, url /= "repositories", url /= "<<" ])
    where urls = words st
          repo url = xmlTag "repository" (concat [(xmlTag "url" url), 
                                                    (xmlTag "releases" enabled),
                                                    (xmlTag "snapshots" enabled)])
              where enabled = xmlTag "enabled" "true"


-- parsing rule for project metadata
parseProject st = concat [xmlTag "name" $ mid $ name st,
                          xmlTag "url" $ url st,
                          let i = ident st in
                              (concat [xmlTag "groupId" (i !! 0),
                                       xmlTag "artifactId" (i !! 1),
                                       xmlTag "version" (i !! 2)])]
    where name = ( =~ "\\\".*\\\"")
          url u = head $ words ( u =~ "(http).*" )  -- not super efficient =(
          ident i = colonSplit $ words (i =~ "id: .*") !! 1 -- pick the second word after "id:"


-- parsing rule for deps glom (we cheat a bit, the grammar is not well enforced)
parseDeps st = xmlTag "dependencies" (concat [ depTag dep | dep <- deps, dep /= "]", dep /= "[" ])
    where deps = words st
          depTag d = xmlTag "dependency" (let dep = colonSplit d in
                                          concat [xmlTag "groupId"    (dep !! 0),
                                                  xmlTag "artifactId" (dep !! 1),
                                                  xmlTag "version"    (dep !! 2)])


-- parsing rule for plugins by group id
parsePlugins st = xmlTag "plugins" (concat [ plg | plg <- plugs ])
    where plugs = words st

-- Wraps a tag name and content in xml lingo
xmlTag name content = '<' : name ++ ">" ++ content ++ ( "</" ++ name ++ ">")

-- strips first and last character from a list
mid [] = []
mid st = init $ tail st

-- turns xx:xx:xx.. into [xx, xx, xx, ..]
colonSplit :: String -> [String]
colonSplit = (=~ "[^:]+")