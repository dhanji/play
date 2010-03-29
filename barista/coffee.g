grammar coffee;


tokens {
  INDENT;
  OUTDENT;
}

@lexer::members {
int implicitLineJoiningLevel = 0;
int startPos = -1;
}

single_input
	: ( simpleStatement
	| compoundStatement ) NEWLINE?
	;



functionDef
	:	functionSig ( expr 
		| NEWLINE )
		
	;
	

compoundStatement
	:	functionDef
	;

simpleStatement
	:	expr
	;
	
	
functionSig
	:	NAME ASSIGN ('(' argDefList? ')')? ARROW
	;
	 
expr
	:	lhs rhs*
	;
	
lhs	:	variableDotChain (ASSIGN variableDotChain)* | term;
	
rhs	:	(operator (term | methodCall) | DOT methodCall)
	;
	
methodCall
	:	 variableDotChain argList?;
	
term	:	STRING
	|	INTEGER
	|	listDef
	| 	mapDef
	|	ifThenElse;
	
variableDotChain
	:	NAME ( DOT NAME )*
	;
	
argList
	:	'(' listInterior? ')';
	
operator:	OPERATOR (OPERATOR | ASSIGN)?
	;
	
listDef	:	'[' listInterior? ']'
	;
	
mapDef	:	'{' (expr ASSIGN expr) (COMMA expr ASSIGN expr)* '}'
	;


ifThenElse
	:	IF expr THEN expr ELSE expr
	;


listInterior
	:	expr (COMMA expr)*
	;
	
argDefList
	:	NAME (COMMA NAME)*
	;
	
	
// KEYWORDS

AND	:	'and';

OR	:	'or';
	
NOT	:	'not';

IF	:	'if';

THEN	:	'then';

ELSE	:	'else';

WHEN	:	'when';

DOT	:	'.';

COMMA	:	',';

ARROW	:	'->';

FAT_ARROW
	:	'=>';
	
NAME:	( 'a' .. 'z' | 'A' .. 'Z' | '_')
        ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
    ;
    
OPERATOR:	
	 ('+' | '-' | '/' | '*' | '&' | '|' | '!' | '>' | '<')
	 ;
    
INTEGER	:	('0' .. '9')+;

ASSIGN	:	':' | '=';

EXISTS	:	'?'	;
	
/** Match various string types.  Note that greedy=false implies '''
 *  should make us exit loop not continue.
 */
STRING
    : 
        (//   '\'\'\'' (options {greedy=false;}:.)* '\'\'\''
      //  |   '"""' (options {greedy=false;}:.)* '"""'
           '"' (ESC|~('\\'|'\n'|'"'))* '"'
        |   '\'' (ESC|~('\\'|'\''))* '\''
        )
	;
	
fragment
ESC
	:	'\\' .
	;
	
NEWLINE	:	(('\r')?'\n')+
	{ // Routes consecutive blank lines into nowhere (treat as one)
	  if (startPos == 0 || implicitLineJoiningLevel > 0)
		$channel=HIDDEN;
	}
	;
	
COMMENT
@init {
	$channel=HIDDEN;
}
	:	{startPos ==0}? => (' '|'\t')* '#' (~'\n')* '\n'+
	|	{startPos > 0}? => '#' (~'\n')*
	;

LEADING_WS
@init {
    int spaces = 0;
}
    :   {startPos==0}?=>
    	(   {implicitLineJoiningLevel>0}? ( ' ' | '\t' )+ {$channel=HIDDEN;}
       	|	( 	' '  { spaces++; }
        	|	'\t' { spaces += 8; spaces -= (spaces \% 8); }
       		)+
        	{
            // make a string of n spaces where n is column number - 1
            char[] indentation = new char[spaces];
            for (int i=0; i<spaces; i++) {
                indentation[i] = ' ';
            }
            String s = new String(indentation);
            emit(new ClassicToken(LEADING_WS,new String(indentation)));
        	}
        	// kill trailing newline if present and then ignore
        	( ('\r')? '\n' {if (state.token!=null) state.token.setChannel(HIDDEN); else $channel=HIDDEN;})*
           // {token.setChannel(99); }
        )
    ;


WS    :    (' '|'\t') {$channel=HIDDEN;}; 
