public class Number extends Token {
 
 public int lexeme = 0;
 
 public Number (int tag, int s) { super(tag); lexeme=s; }
 
 public String toString() { return "<" + tag + ", " + lexeme + ">"; }
 
}
