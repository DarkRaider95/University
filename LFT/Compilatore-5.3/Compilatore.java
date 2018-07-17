import java.io.*;

public class Compilatore {
    private Lexer lex;
    private Token look;
    private CodeGenerator code = new CodeGenerator();
    private SymbolTable var = new SymbolTable();
    int type_val;

    public Compilatore(Lexer l) {
        lex = l;
        move();
    }

    void move() {
        look = lex.lexical_scan();
        System.err.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error"+look.tag);
    }

    public void prog() {
        //la procedura start puo‘ essere estesa(opzionale)
        int address=0;

        declist(address);
        stat();
        match(Tag.EOF);

        try{
          code.toJasmin();
        }
        catch (IOException e){
         e.printStackTrace();
        }
    }

    private void declist(int address) {
      address=dec(address);
      if(look.tag!=Tag.BEGIN && look.tag != Tag.EOF && look.tag != Tag.PRINT) match(';');
      if(look.tag!=Tag.BEGIN && look.tag != Tag.EOF && look.tag != Tag.PRINT) declist(address);
    }

    private int dec(int address) {
      
      type_val=type();

      if(look.tag!=Tag.BEGIN && look.tag != Tag.EOF && look.tag != Tag.PRINT){
        if(look.tag==Tag.ID){
          Word w = (Word) look;
          var.insert(w.lexeme,type_val,address);
          match(Tag.ID);
          address = idlist(++address);
        }
      }
            
      return address;
    }

    private int type() {
      int types=0;
      switch(look.tag){
        case Tag.BOOLEAN:
          match(Tag.BOOLEAN);
          types=Tag.BOOLEAN;
          break;
        
        case Tag.INTEGER:
          match(Tag.INTEGER);
          types=Tag.INTEGER;
          break;

        case Tag.BEGIN:
        case Tag.EOF:
        case Tag.PRINT:
        case ';':
        break;

        default:
          error("syntax error type "+look.tag);
          break;
      }
      return types;
    }

    private int idlist(int address) {
      if(look.tag!=Tag.BEGIN && look.tag != Tag.EOF && look.tag != Tag.PRINT && look.tag != ';'){
        match(',');
        
        if(look.tag == Tag.ID){
          Word w = (Word) look;
          var.insert(w.lexeme,type_val,address);
          match(Tag.ID);
        }
        if(look.tag != ';') address=idlist(++address);
      }
      return address;
    }

    private void stat(){
      Object expr_val;

      switch(look.tag){
        case Tag.ID:
          Word w = (Word) look;
          match(Tag.ID);
          match(Tag.ASSIGN);
          expr_val=orExpr();
          //System.out.println("Boolean "+expr_val);
          if(var.lookupType(w.lexeme)==Tag.BOOLEAN){
            Boolean b = (Boolean) expr_val;
            var.insval(w.lexeme,b);
          }
          else{
            Integer i = (Integer) expr_val;
            var.insval(w.lexeme,i);
          }
          code.emit(OpCode.istore, var.lookupAddress(w.lexeme));
          break;

        case Tag.PRINT:
          match(Tag.PRINT);
          match('(');
          //System.out.println("me la maccio io stronzo ");
          expr_val=orExpr();
          match(')');
          if(expr_val.getClass().equals(Boolean.class)){
            //System.out.println("Boolean "+expr_val);
            code.emit(OpCode.invokestatic,0);
          }
          else{
            //System.out.println("Numerical "+expr_val);
            code.emit(OpCode.invokestatic,1);
          }
          break;

        case Tag.BEGIN:
          match(Tag.BEGIN);
          statlist();
          match(Tag.END);
          break;

        case ';':
        case Tag.EOF:
          break;

        default:
          error("syntax error stat "+look.tag);
          break;
      }
    }

    private void statlist() {
      stat();
      statlist_p();
    }

    private void statlist_p() {
      match(';');
      if(look.tag!=Tag.END){
        stat();
        statlist_p();
      }
    }

    private Object orExpr() {
        //la procedura expr puo‘ essere estesa(opzionale)
        Object andExpr_val, orExprp_val;

        andExpr_val = andExpr();
        //System.out.println("expr"+term_val);
        orExprp_val = orExprp(andExpr_val);
        return orExprp_val;
    }

    private Object orExprp(Object orExpr_i) {
        Object andExpr_val, orExprp_val=null;

        switch (look.tag) {
            case Tag.OR:
                match(Tag.OR);
                andExpr_val = andExpr();
                //System.out.println("\nexprp"+term_val+"\n");
                if(orExpr_i.getClass().equals(Boolean.class) && andExpr_val.getClass().equals(Boolean.class)) {
                  Boolean x,y;

                  x = (Boolean) orExpr_i;
                  y = (Boolean) andExpr_val;
                  code.emit(OpCode.ior);
                  orExprp_val = orExprp(x || y);
                 }

                else error("can't compare different types andExprp");
                break;

            case ';':
            case ')':
            case Tag.ID:
            case Tag.EOF:
                orExprp_val = orExpr_i;
                break;
            

            default:
                error("syntax error orExprp "+look.tag);
                break;
            
                //...gestire gli altri casi... //
        }
     
     return orExprp_val;
    }

    private Object andExpr() {
      Object relExpr_val, andExprp_val;
      
      relExpr_val = relExpr();
      andExprp_val = andExprp(relExpr_val);
      
      return andExprp_val;
    }

    private Object andExprp(Object andExpr_i) {
        Object relExpr_val, andExprp_val=null;

        switch (look.tag) {
            case Tag.AND:
                match(Tag.AND);
                relExpr_val = relExpr();
                if((andExpr_i.getClass().equals(Boolean.class) && relExpr_val.getClass().equals(Boolean.class)) || (andExpr_i.getClass().equals(Integer.class) && relExpr_val.getClass().equals(Integer.class))){
                  Boolean x,y;

                  x = (Boolean) andExpr_i;
                  y = (Boolean) relExpr_val;
                  code.emit(OpCode.iand);
                  andExprp_val = andExprp(x && y);
                 }

                else  error("can't compare different types andExprp");            
                break;

            case ';':
            case ')':
            case Tag.ID:
            case Tag.OR:
            case Tag.EOF: 
                andExprp_val=andExpr_i;
                break;            

            default:
                error("syntax error andExprp "+look.tag);;
                break;
        }
            
      return andExprp_val;
      //...gestire gli altri casi...//
    }

    private Object relExpr() {
      Object addExpr_val, relExprp_val;
      
      addExpr_val = addExpr();
      relExprp_val = relExprp(addExpr_val);
      
      return relExprp_val;
    }

    private Object relExprp(Object relExpr_i) {
        Object addExpr_val, relExprp_val=null;
        int ltrue,lnext;

        switch (look.tag) {
            case Tag.EQ:
                match(Tag.EQ);
                addExpr_val = addExpr();
                if(relExpr_i.getClass().equals(Boolean.class) && addExpr_val.getClass().equals(Boolean.class)){
                  Boolean x,y;

                  x = (Boolean) relExpr_i;
                  y = (Boolean) addExpr_val;

                  relExprp_val = relExprp(x == y);
                 }

                else if(relExpr_i.getClass().equals(Integer.class) && addExpr_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  x = (Integer) relExpr_i;
                  y = (Integer) addExpr_val;

                  relExprp_val = relExprp(x == y);
                }

                else  error("can't compare different types andExprp");

                ltrue = code.newLabel();
                lnext = code.newLabel();
                code.emit (OpCode.if_icmpeq,ltrue);
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext);
                code.emitLabel (ltrue);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext);
                break;

            case Tag.NE:
                match(Tag.NE);
                addExpr_val = addExpr();
                if(relExpr_i.getClass().equals(Integer.class) && addExpr_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  x = (Integer) relExpr_i;
                  y = (Integer) addExpr_val;

                  relExprp_val = relExprp(x != y);
                 }

                else if (relExpr_i.getClass().equals(Boolean.class) && addExpr_val.getClass().equals(Boolean.class)){
                  Boolean x,y;

                  x = (Boolean) relExpr_i;
                  y = (Boolean) addExpr_val;

                  relExprp_val = relExprp(x != y);
                }

                else  error("can't compare different types andExprp");

                ltrue = code.newLabel();
                lnext = code.newLabel();
                code.emit (OpCode.if_icmpne,ltrue);
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext);
                code.emitLabel (ltrue);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext);
                break;

            case Tag.LE:
                match(Tag.LE);
                addExpr_val = addExpr();
                if(relExpr_i.getClass().equals(Integer.class) && addExpr_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  x = (Integer) relExpr_i;
                  y = (Integer) addExpr_val;

                  relExprp_val = relExprp(x <= y);
                 }

                else  error("can't compare different types andExprp");

                ltrue = code.newLabel();
                lnext = code.newLabel();
                code.emit (OpCode.if_icmple,ltrue);
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext);
                code.emitLabel (ltrue);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext);
                break;

            case Tag.GE:
                match(Tag.GE);
                addExpr_val = addExpr();
                if(relExpr_i.getClass().equals(Integer.class) && addExpr_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  x = (Integer) relExpr_i;
                  y = (Integer) addExpr_val;

                  relExprp_val = relExprp(x >= y);
                 }

                else  error("can't compare different types andExprp");

                ltrue = code.newLabel();
                lnext = code.newLabel();
                code.emit (OpCode.if_icmpge,ltrue);
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext);
                code.emitLabel (ltrue);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext);
                break;

            case '<':
                match('<');
                addExpr_val = addExpr();
                if(relExpr_i.getClass().equals(Integer.class) && addExpr_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  x = (Integer) relExpr_i;
                  y = (Integer) addExpr_val;

                  relExprp_val = relExprp(x < y);
                 }

                else  error("can't compare different types andExprp");

                ltrue = code.newLabel();
                lnext = code.newLabel();
                code.emit (OpCode.if_icmplt,ltrue);
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext);
                code.emitLabel (ltrue);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext);
                break;

            case '>':
                match('>');
                addExpr_val = addExpr();
                if(relExpr_i.getClass().equals(Integer.class) && addExpr_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  x = (Integer) relExpr_i;
                  y = (Integer) addExpr_val;

                  relExprp_val = relExprp(x > y);
                 }

                else  error("can't compare different types andExprp");

                ltrue = code.newLabel();
                lnext = code.newLabel();
                code.emit (OpCode.if_icmpgt,ltrue);
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext);
                code.emitLabel (ltrue);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext);
                break;

            case ';':
            case ')':
            case Tag.ID:
            case Tag.OR:
            case Tag.AND:
            case Tag.EOF: 
                relExprp_val=relExpr_i;
                break;            

            default:
                error("syntax error andExprp "+look.tag);;
                break;
        }
            
      return relExprp_val;
    }

    private Object addExpr() {
      Object multExpr_val, addExprp_val;

      multExpr_val = multExpr();
      addExprp_val = addExprp(multExpr_val);

      return addExprp_val;
    }

    private Object addExprp(Object addExpr_i) {
        Object multExpr_val, addExprp_val=null;

        switch (look.tag) {
            case '+':
                match('+');
                multExpr_val = multExpr();
                if(addExpr_i.getClass().equals(Integer.class) && multExpr_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  x = (Integer) addExpr_i;
                  y = (Integer) multExpr_val;
                  //code.emit(OpCode.ldc,w);
                  //code.emit(OpCode.ldc,z);
                  code.emit(OpCode.iadd);
                  addExprp_val = addExprp(x + y);
                 }

                else  error("can't sum boolean with number");

                
                break;

            case '-':
                match('-');
                multExpr_val = multExpr();
                if(addExpr_i.getClass().equals(Integer.class) && multExpr_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  code.emit(OpCode.isub);
                  x = (Integer) addExpr_i;
                  y = (Integer) multExpr_val;
                  addExprp_val = addExprp(x - y);
                 }

                else  error("can't subtract boolean with number");

                
                break;

            case ';':
            case '<':
            case '>':
            case ')':
            case Tag.ID:
            case Tag.EQ:
            case Tag.NE:
            case Tag.LE:
            case Tag.GE:
            case Tag.OR:
            case Tag.AND:
            case Tag.EOF: 
                addExprp_val=addExpr_i;
                break;            

            default:
                error("syntax error sumExprp "+look.tag);;
                break;
        }
            
      return addExprp_val;
    }

    private Object multExpr() {
      Object fact_val, multExprp_val;

      fact_val = fact();
      multExprp_val = multExprp(fact_val);

      return multExprp_val;
    }

    private Object multExprp(Object multExpr_i) {
        Object fact_val, multExprp_val=null;

        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                if(multExpr_i.getClass().equals(Integer.class) && fact_val.getClass().equals(Integer.class)){
                  Integer x,y;

                  x = (Integer) multExpr_i;
                  y = (Integer) fact_val;
                  code.emit(OpCode.imul);
                  multExprp_val = multExprp(x * y);
                 }

                else  error("can't multiply boolean with number multExprp");
                
                break;

            case '/':
                match('/');
                fact_val = fact();
                if(multExpr_i.getClass().equals(Integer.class) && fact_val.getClass().equals(Integer.class)){
                  Integer x,y;
                  code.emit(OpCode.idiv);
                  x = (Integer) multExpr_i;
                  y = (Integer) fact_val;
                  multExprp_val = multExprp(x / y);
                 }

                else  error("can't multiply boolean with number multExprp");                
                break;

            case ';':
            case '<':
            case '>':
            case ')':
            case '+':
            case '-':
            case Tag.ID:
            case Tag.EQ:
            case Tag.NE:
            case Tag.LE:
            case Tag.GE:
            case Tag.OR:
            case Tag.AND:
            case Tag.EOF: 
                multExprp_val=multExpr_i;
                break;            

            default:
                error("syntax error multExprp "+look.tag);;
                break;
        }
            
      return multExprp_val;
    }

    private Object fact() {
      Object fact_val=null;

      switch (look.tag) {
        case Tag.NUM:
          Number n = (Number)look;
          Integer i = n.lexeme;
          code.emit(OpCode.ldc,i);
          fact_val = i;
          //System.out.println(fact_val+"fact");
          match(Tag.NUM);
          break;

        case Tag.ID:
          Word w = (Word) look;
          code.emit(OpCode.iload,var.lookupAddress(w.lexeme));
          fact_val=var.lookupValue(w.lexeme);
          System.out.println(fact_val+"fact");
          match(Tag.ID);
          break;

        case '(':
          match('(');
          fact_val = orExpr();
          match(')');
          break;

        case Tag.TRUE:
          match(Tag.TRUE);
          Boolean b = true;
          code.emit(OpCode.ldc,1);
          fact_val = b;
          break;

        case Tag.FALSE:
          match(Tag.FALSE);
          Boolean c = false;
          code.emit(OpCode.ldc,0);
          fact_val = c;
          break;

        default:
          error("syntax error fact "+look.tag);;
          break;
           
        //...gestire tutti i casi...//
        }
     return fact_val;
    }

    public static void main(String[] args) {
      Lexer lex = new Lexer();
      Compilatore comp = new Compilatore(lex);
      comp.prog();
    }
}
