import java.io.*;

public class CompilatoreF {
    private LexerF lex;
    private Token look;
    private CodeGenerator code = new CodeGenerator();
    private SymbolTable var = new SymbolTable();
    private BufferedReader pbr;
    private int address;

    //costruttore Compilatore
    public CompilatoreF(LexerF l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
        address=0;
    }

    // chiedo il prossimo carattere al lexer
    void move() {
        if ((look = lex.lexical_scan(pbr)) == null) error(null);
        System.err.println("token = " + look);
    }

    // stampo l'errore
    void error(String s) {
        throw new Error("near line " + lex.line + ((s == null) ? "" : (": " + s)));
    }

    // verifico che il tag corrisponda se si chiedo il prossimo carattere altrimenti stampo errore
    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error: expected "+t+" found "+look.tag);
    }

    // avvio la ricorsione prima la dichiarazioni delle variabili successivamente le istruzioni una volta terminate
    // e trovato il carattere EOF creo il file con le istruzioni bytecode

    public void prog(String f) {                // prog := (declist) (stat) EOF

        declist();
        stat();
        match(Tag.EOF);

        try{
          f=f.substring(0,f.indexOf('.'))+".j";
          System.out.println(f);
          code.toJasmin(f);
        }
        catch (IOException e){
         e.printStackTrace();
        }
    }

    // controllo se c'è una dichiarazione di variabili se si
    // comincio la ricorsione altrimenti esco

    private void declist() {                    // declist := (dec) ; (declist) | ε
      if(dec()){
        match(';');
        declist();
      }
    }

    // se è stato trovato un tipo comincio la ricorsione per la dichiariazione di variabili
    // altrimenti esco

    private boolean dec() {                     // dec := (type) ID (idlist)

      int type_val=type();

      if(type_val!=0){
        if(look.tag==Tag.ID){
          Word w = (Word) look;
          var.insert(w.lexeme, type_val, address++);  
          match(Tag.ID);
          idlist(type_val);
          return true;
        }
      }
            
      return false;
    }

    //controllo il tipo delle variabili da dichiarare

    private int type() {                        // type := integer | boolean
      switch(look.tag){
        case Tag.BOOLEAN:
          match(Tag.BOOLEAN);
          return Tag.BOOLEAN;
        
        case Tag.INTEGER:
          match(Tag.INTEGER);
          return Tag.INTEGER;
      }
      return 0;
    }

    // inserisco le variabili nella symbol table

    private void idlist(int type) {             // idlist := , ID (idlist) | ε
      if(look.tag == ','){
        match(',');
        if(look.tag == Tag.ID){
          Word w = (Word) look;
          var.insert(w.lexeme,type,address++);
          match(Tag.ID);
        }
        idlist(type);
      }
    }

    // controllo che tipo di istruzione è

    private void stat(){                        // stat := ID := (orExpr)
      int lstart,lfalse,ltrue;                  //       | print((orExpr)) 
                                                //       | begin (statlist) end
      switch(look.tag){                         //       | while (orExpr) do (stat)
                                                //       | if (orExpr) then (stat)
                                                //       | if (orExpr) then (stat) else (stat)

        // assegnazione di un valore a una variabile
        case Tag.ID:                            
          Word w = (Word) look;                 
          match(Tag.ID);
          match(Tag.ASSIGN);
          if(var.lookupType(w.lexeme)!=orExpr()) error("incopitible type assignment");
          code.emit(OpCode.istore, var.lookupAddress(w.lexeme));
          break;

        //stampo valore espressione
        case Tag.PRINT:
          match(Tag.PRINT);
          match('(');
          int expr_val = orExpr();
          match(')');
          if(expr_val==Tag.BOOLEAN) code.emit(OpCode.invokestatic,0);
          else code.emit(OpCode.invokestatic,1);
          break;

        //inizio un set di istruzioni
        case Tag.BEGIN:
          match(Tag.BEGIN);
          statlist();
          match(Tag.END);
          break;

        case Tag.WHILE:
          match(Tag.WHILE);
          lstart = code.newLabel();
          lfalse = code.newLabel();
          code.emitLabel(lstart);
          if(orExpr()!=Tag.BOOLEAN) error("expr inside while must be boolean");
          code.emit(OpCode.ldc,0);
          code.emit(OpCode.if_icmpeq,lfalse);
          match(Tag.DO);
          stat();
          code.emit(OpCode.GOto,lstart);
          code.emitLabel(lfalse);
          break;

        case Tag.IF:          
          match(Tag.IF);
          if(orExpr()!=Tag.BOOLEAN) error("expr inside if must be boolean");
          ltrue = code.newLabel();
          lfalse = code.newLabel();
          int lend = code.newLabel();
          code.emit(OpCode.ldc,1);
          code.emit(OpCode.if_icmpeq,ltrue);
          code.emit(OpCode.GOto,lfalse);
          code.emitLabel(ltrue);
          match(Tag.THEN);
          stat();
          if(look.tag==Tag.ELSE)code.emit(OpCode.GOto,lend);
          code.emitLabel(lfalse);
          
          if(look.tag==Tag.ELSE){
            match(Tag.ELSE);
            stat();
            code.emitLabel(lend);
          }
          break;

        case ';':
        case Tag.ELSE:
        case Tag.EOF:
          break;

        default:
          error("syntax error stat "+look.tag);
          break;
      }
    }

    //ricorsione per le istruzioni
    private void statlist() {                   // statlist := (stat) (statlist_p)
      stat();
      statlist_p();
    }

    //se il tag è diverso da END vado avanti con la ricorsione

    private void statlist_p() {                // statlist_p := ; (stat) (statlist_p) | ε
      if(look.tag == ';'){
        match(';');
        stat();
        statlist_p();
      }
    }

    //comincia la ricorsione per le espressioni aritmetico logiche

    private int orExpr() {                      // orExpr := (andExpr) (orExprp)
      int andExpr_val, orExprp_val;

        andExpr_val = andExpr();
        orExprp_val = orExprp(andExpr_val);
        
        return orExprp_val;  
    }

    private int orExprp(int orExpr_i) {         // orExprp := || (andExpr) (orExprp) | ε 
       int andExpr_val, orExprp_val=0;

       switch(look.tag){

        case Tag.OR:
          match(Tag.OR);
          int ltrue=code.newLabel();
          int lfalse=code.newLabel();
          code.emit(OpCode.ifne,ltrue);
          andExpr_val = andExpr();
          if(orExpr_i != Tag.BOOLEAN || andExpr_val != Tag.BOOLEAN) error("Operand or can be applied only to boolean expressions\n"); 
          code.emit(OpCode.GOto,lfalse);
          code.emitLabel(ltrue);
          code.emit(OpCode.ldc,1);
          code.emitLabel(lfalse);
          orExprp_val = orExprp(Tag.BOOLEAN);          
          break;

        case ';':
        case ')':
        case Tag.DO:
        case Tag.ELSE:
        case Tag.THEN:
        case Tag.ID:
        case Tag.END:
        case Tag.EOF:
          orExprp_val = orExpr_i;
          break;
        
        default:
          error("syntax error orExprp "+look.tag);
          break;
       }
     
     return orExprp_val;
    }

    private int andExpr() {                     // andExpr := (relExpr) (andExprp)
      int relExpr_val, andExprp_val;
      
      relExpr_val = relExpr();
      andExprp_val = andExprp(relExpr_val);
      
      return andExprp_val;    
    }

    private int andExprp(int andExpr_i) {       // andExprp := && (relExpr) (andExprp) | ε
      int andExprp_val=0, relExpr_val;
      
      switch (look.tag){
        case Tag.AND:
          match(Tag.AND);
          int ltrue=code.newLabel();
          int lfalse=code.newLabel();
          code.emit(OpCode.ifeq,lfalse);
          relExpr_val = relExpr();
          if(andExpr_i != Tag.BOOLEAN || relExpr_val != Tag.BOOLEAN) error("operand or can be applied only to boolean expressions");
          code.emit(OpCode.GOto,ltrue);
          code.emitLabel(lfalse);
          code.emit(OpCode.ldc,0);
          code.emitLabel(ltrue);
          andExprp_val = andExprp(Tag.BOOLEAN);

        case ';':
        case ')':
        case Tag.DO:
        case Tag.ELSE:
        case Tag.THEN:
        case Tag.ID:
        case Tag.OR:
        case Tag.END:
        case Tag.EOF: 
          andExprp_val=andExpr_i;
          break;

        default:
          error("syntax error andExprp "+look.tag);;
          break;
      }
            
      return andExprp_val;
    }

    private int relExpr() {                     // relExpr := (addExpr) (relExprp)
      int addExpr_val, relExprp_val;
      
      addExpr_val = addExpr();
      relExprp_val = relExprp(addExpr_val);
      
      return relExprp_val;
    }

    private int relExprp(int relExpr_i){        // relExprp := (oprel) (addExpr) | ε
      int ltrue = code.newLabel(), lnext, addExpr_val, old_look = look.tag; 
      OpCode op;

      op = OpRel();

      if(op == null) return relExpr_i;

      addExpr_val = addExpr();
      
      if(old_look != Tag.EQ && old_look != Tag.NE) {
        if(relExpr_i!= Tag.INTEGER || addExpr_val != Tag.INTEGER) error("can't compare different types");
      }
      else {
        if(relExpr_i != addExpr_val) error("can't compare different types");
      }
      
      lnext = code.newLabel();
      code.emit (op,ltrue);
      code.emit (OpCode.ldc,0);
      code.emit (OpCode.GOto,lnext);
      code.emitLabel (ltrue);
      code.emit (OpCode.ldc,1);
      code.emitLabel (lnext);
      return Tag.BOOLEAN;
    }

    private OpCode OpRel() {     // OpRel := == | <> | <= | >= | < | >

        switch (look.tag) {
            case Tag.EQ:
              match(Tag.EQ);
              return OpCode.if_icmpeq;

            case Tag.NE:
              match(Tag.NE);
              return OpCode.if_icmpne;

            case Tag.LE:
              match(Tag.LE);
              return OpCode.if_icmple;

            case Tag.GE:
              match(Tag.GE);
              return OpCode.if_icmpge;

            case '<':
              match('<');
              return OpCode.if_icmplt;

            case '>':
              match('>');
              return OpCode.if_icmpgt ;
        }
        return null;
    }

    private int addExpr() {                     // addExpr := (multExpr) (addExprp)
      int multExpr_val, addExprp_val;

      multExpr_val = multExpr();
      addExprp_val = addExprp(multExpr_val);

      return addExprp_val;
    }

    private int addExprp(int addExpr_i) {       // addExprp := + (multExpr) (addExprp)
      int multExpr_val, addExprp_val=0;         //           | - (multExpr) (addExprp)
                                                //           | ε
        switch (look.tag) {
            case '+':
                match('+');
                multExpr_val = multExpr();
                if(multExpr_val !=Tag.INTEGER || addExpr_i!=Tag.INTEGER) error("can't sum boolean");
                code.emit(OpCode.iadd);
                addExprp_val = addExprp(multExpr_val);
                break;

            case '-':
                match('-');
                multExpr_val = multExpr();
                if(multExpr_val !=Tag.INTEGER || addExpr_i!=Tag.INTEGER) error("can't sub boolean");
                code.emit(OpCode.isub);
                addExprp_val = addExprp(multExpr_val);
                break;
            
            case ';':
            case '<':
            case '>':
            case ')':
            case Tag.DO:
            case Tag.THEN:
            case Tag.ID:
            case Tag.EQ:
            case Tag.NE:
            case Tag.LE:
            case Tag.ELSE:
            case Tag.GE:
            case Tag.OR:
            case Tag.AND:
            case Tag.EOF:
            case Tag.END:
              addExprp_val=addExpr_i;
              break;            

            default:
              error("syntax error sumExprp "+look.tag);;
              break;
        }
            
      return addExprp_val;
    }

    private int multExpr() {                    // multExpr := (fact) (multExprp)
      int fact_val, multExprp_val;

      fact_val = fact();
      multExprp_val = multExprp(fact_val);

      return multExprp_val;
    }

    private int multExprp(int multExpr_i) {     // multExprp := * (fact) (multExprp)
      int fact_val, multExprp_val=0;            //            | / (fact) (multExprp)
                                                //            | ε
      switch (look.tag) {
            case '*':
              match('*');
              fact_val = fact();
              if(fact_val !=Tag.INTEGER || multExpr_i!=Tag.INTEGER) error("can't mult boolean");
              code.emit(OpCode.imul);
              multExprp_val = multExprp(fact_val);
              break;

            case '/':
              match('/');
              fact_val = fact();
              if(fact_val !=Tag.INTEGER || multExpr_i!=Tag.INTEGER) error("can't div boolean");
              code.emit(OpCode.idiv);
              multExprp_val = multExprp(fact_val);
              break;

            case ';':
            case '<':
            case '>':
            case ')':
            case '+':
            case '-':
            case Tag.DO:
            case Tag.THEN:
            case Tag.ID:
            case Tag.EQ:
            case Tag.NE:
            case Tag.ELSE:
            case Tag.LE:
            case Tag.GE:
            case Tag.OR:
            case Tag.AND:
            case Tag.EOF:
            case Tag.END:
              multExprp_val=multExpr_i;
              break;            

            default:
              error("syntax error multExprp "+look.tag);;
              break;
        }
            
      return multExprp_val;
    }

    private int fact() {                        // fact := NUM | ID | ((orExpr)) | not | true | false 
      int type;

      switch (look.tag) {
        case Tag.NUM:
          code.emit(OpCode.ldc,((Number)look).lexeme);
          match(Tag.NUM);
          return Tag.INTEGER;

        case Tag.ID:
          code.emit(OpCode.iload,var.lookupAddress(((Word)look).lexeme));
          type = var.lookupType(((Word)look).lexeme);
          match(Tag.ID);
          return type;

        case '(':
          match('(');
          type=orExpr();
          match(')');
          return type;

        case Tag.NOT:
          match(Tag.NOT);
          if(fact()!=Tag.BOOLEAN) error("NOT applicable only to boolean");
          code.emit(OpCode.ldc,1);
          code.emit(OpCode.ixor);
          return Tag.BOOLEAN;

        case Tag.TRUE:
          match(Tag.TRUE);
          code.emit(OpCode.ldc,1);
          return Tag.BOOLEAN;

        case Tag.FALSE:
          match(Tag.FALSE);
          code.emit(OpCode.ldc,0);
          return Tag.BOOLEAN;

        default:
          error("syntax error fact "+look.tag);
          return -1;
        }
    }

    public static void main(String[] args) {
      
      //apro file da parsificare e inizio la parsificazione
      if (args.length > 0) { 
        LexerF lex = new LexerF();
        String path = args[0];
        try {
          BufferedReader br = new BufferedReader(new FileReader(path));
          CompilatoreF comp = new CompilatoreF(lex, br);
          comp.prog(path);
          br.close();
        } 
        catch (IOException e){
          e.printStackTrace();
        }
      }
      
      else System.out.println("Missing file name as parameter!!");   
    }
}