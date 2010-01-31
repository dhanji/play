-- a blaze-style grammar for maven. Simply converts build to pom.xml
import System.IO
import Data.Char
import Text.Regex.Posix


main = do
          buildFile <- openFile "hakefile" ReadMode
          contents <- hGetContents buildFile
          
          -- todo output: pom.hake.xml and invoke mvn -f
          
          -- strip comment lines
          writeFile "pom.hake.xml" $ root $ concat $ map mavenize (glomify (map stripComments (lines contents)))
                                  ++ [defaultBuild, defaultModel]
          hClose buildFile
    where defaultBuild = xmlTag "build" $ (xmlTag "sourceDirectory" "src") ++ (xmlTag "testSourceDirectory" "test")
                                        ++ (xmlTag "plugins" $ (xmlTag "plugin" $
                                                artifactDescriptorTag ["org.apache.maven.plugins",
                                                                      "maven-compiler-plugin",
                                                                       (xmlTag "configuration" 
                                                                            (concat [xmlTag "source" "1.5",
                                                                                    xmlTag "target" "1.5"]))]))
          defaultModel = xmlTag "modelVersion" "4.0.0"
          root = xmlTag "project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\""
          
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
          repo url = xmlTag "repository" (concat [(xmlTag "id" url), -- use URL as ID for now
                                                    (xmlTag "url" url), 
                                                    (xmlTag "releases" enabled),
                                                    (xmlTag "snapshots" enabled)])
              where enabled = xmlTag "enabled" "true"


-- parsing rule for project metadata
parseProject st = concat [xmlTag "name" $ mid $ name st,
                          xmlTag "url" $ url st,
                          let i = ident st in artifactDescriptorTag (init i ++ [xmlTag "version" (i !! 2)])]
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
parsePlugins st = xmlTag "build" $ xmlTag "plugins" (concat [ plg | plg <- plugs ])
    where plugs = words st

-- Wraps a tag name and content in xml lingo
xmlTag name content = '<' : name ++ ">" ++ content ++ ( "</" ++ (head $ words name) ++ ">")

-- strips first and last character from a list
mid [] = []
mid st = init $ tail st

-- turns xx:xx:xx.. into [xx, xx, xx, ..]
colonSplit :: String -> [String]
colonSplit = (=~ "[^:]+")



-- produces an xml glob with the three bits set
artifactDescriptorTag i = (concat [xmlTag "groupId" (i !! 0),
                                   xmlTag "artifactId" (i !! 1),
                                   i !! 2])
