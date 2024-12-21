
Welcome to Grammar Parsers!
1. Top-Down Parser
2. Bottom-Up Parser
1
Enter grammar rules (e.g., S->aSb|ε). End with an empty line:
E -> T E'
E' -> + T E' | ε
T -> F T'
T' -> * F T' | ε
F -> id

Enter input string:
id + id * id
Parsing result: Accepted
