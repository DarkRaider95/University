import java.io.*;

public class CompilatoreFast {
    private LexerF lex;
    private Token look;
    private CodeGenerator code = new CodeGenerator();
    private SymbolTable var = new SymbolTable();
    private BufferedReader pbr;
    private int address;

    public CompilatoreFast(LexerF l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
        address=0;
    }

    void move() {
        look =  lex.lexical_scan(pbr);
        System.err.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error: expected "+t+" found "+look.tag);
    }

    public void prog(String f) {
        //la procedura start puo‘ essere estesa(opzionale)

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

    private void declist() {
      if(dec()){
        match(';');
        declist();
      }
    }

    private boolean dec() {
      
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

    private int type() {
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

    private void idlist(int type) {
      if(look.tag == ','){
        match(',');
        
        if(look.tag == Tag.ID){
          Word w = (Word) look;
          var.insert(w.lexeme,type,address);
          match(Tag.ID);
        }
        idlist(type);
      }
    }

    private void stat(){
      int lstart,lfalse,ltrue;

      switch(look.tag){
        case Tag.ID:
          Word w = (Word) look;
          match(Tag.ID);
          match(Tag.ASSIGN);
          //System.out.println("Boolean "+expr_val);
          if(var.lookupType(w.lexeme)!=orExpr()) error("incopitible type assignment");
          code.emit(OpCode.istore, var.lookupAddress(w.lexeme));
          break;

        case Tag.PRINT:
          match(Tag.PRINT);
          match('(');
          int expr_val = orExpr();
          match(')');
          if(expr_val==Tag.BOOLEAN) code.emit(OpCode.invokestatic,0);
          else code.emit(OpCode.invokestatic,1);
          break;

        case Tag.BEGIN:
          match(Tag.BEGIN);
          statlist();
          match(Tag.END);
          break;

        case Tag.WHILE:
          match(Tag.WHILE);
          lstart = code.newLabel();
          ltrue = code.newLabel();
          lfalse = code.newLabel();
          code.emitLabel(lstart);
          if(orExpr()!=Tag.BOOLEAN) error("expr inside while must be boolean");
          code.emit(OpCode.ldc,1);
          code.emit(OpCode.if_icmpeq,ltrue);
          code.emit(OpCode.GOto,lfalse);
          code.emitLabel(ltrue);
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

    private void statlist() {
      stat();
      statlist_p();
    }

    private void statlist_p() {
      if(look.tag == ';'){
        match(';');
        stat();
        statlist_p();
      }
    }

    private int orExpr() {
      return orExprp(andExpr());
    }

    private int orExprp(int orExpr_i) {
        int andExpr_val;

       if(look.tag==Tag.OR){
         match(Tag.OR);
         int ltrue=code.newLabel();
         int lfalse=code.newLabel();
         code.emit(OpCode.ifne,ltrue);
         if(orExpr_i != Tag.BOOLEAN || andExpr() != Tag.BOOLEAN) error("operand or can be applied only to boolean expressions"); 
         code.emit(OpCode.GOto,lfalse);
         code.emitLabel(ltrue);
         code.emit(OpCode.ldc,1);
         code.emitLabel(lfalse);
         return orExprp(Tag.BOOLEAN);          
        }
     
     return orExpr_i;
    }

    private int andExpr() {      
      return andExprp(relExpr());
    }

    private int andExprp(int andExpr_i) {
       if(look.tag==Tag.AND){
         match(Tag.AND);
         int ltrue=code.newLabel();
         int lfalse=code.newLabel();
         code.emit(OpCode.ifeq,lfalse);
         if(andExpr_i != Tag.BOOLEAN || relExpr() != Tag.BOOLEAN) error("operand or can be applied only to boolean expressions");
         code.emit(OpCode.GOto,ltrue);
         code.emitLabel(lfalse);
         code.emit(OpCode.ldc,0);
         code.emitLabel(ltrue);
         return andExprp(Tag.BOOLEAN);          
        }
            
      return andExpr_i;
    }

    private int relExpr() {
      return relExprp(addExpr());
    }

    private int relExprp(int relExpr_i) {
      int ltrue,lnext, old_look = look.tag;
      OpCode op;
      
      op = OpRel();

      if(op == null) return relExpr_i;

      if(old_look != Tag.EQ && old_look != Tag.NE) {
        if(relExpr_i!= Tag.INTEGER || addExpr() != Tag.INTEGER) error("can't compare different types");
      }
            
      else{
        if(relExpr_i!=addExpr()) error("can't compare different types");
      }
            
      ltrue = code.newLabel();
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

    private int addExpr() {
      return addExprp(multExpr());
    }

    private int addExprp(int addExpr_i) {
        switch (look.tag) {
            case '+':
                match('+');
                if(addExprp(multExpr())!=Tag.INTEGER || addExpr_i!=Tag.INTEGER) error("can't sum boolean");
                code.emit(OpCode.iadd);
                return Tag.INTEGER;

            case '-':
                match('-');
                if(addExprp(multExpr())!=Tag.INTEGER || addExpr_i!=Tag.INTEGER) error("can't sub boolean");
                code.emit(OpCode.isub);
                return Tag.INTEGER;
        }
            
      return addExpr_i;
    }

    private int multExpr() {
      return multExprp(fact());
    }

    private int multExprp(int multExpr_i) {
      switch (look.tag) {
            case '*':
                match('*');
                if(multExprp(fact())!=Tag.INTEGER || multExpr_i!=Tag.INTEGER) error("can't mult boolean");
                code.emit(OpCode.imul);
                return Tag.INTEGER;

            case '/':
                match('/');
                if(addExprp(multExpr())!=Tag.INTEGER || multExpr_i!=Tag.INTEGER) error("can't div boolean");
                code.emit(OpCode.idiv);
                return Tag.INTEGER;
        }
            
      return multExpr_i;
    }

    private int fact() {
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