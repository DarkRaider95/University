.class public Primo
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static printBool(I)V
 .limit stack 3
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 bipush 1
 if_icmpeq Ltrue
 ldc "false"
 goto Lnext
 Ltrue:
 ldc "true"
 Lnext:
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static printInt(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 ldc 1237
 istore 0
 ldc 2
 istore 1
 ldc 1
 istore 3
L3:
 iload 1
 iload 0
 ldc 2
 idiv 
 if_icmplt L6
 ldc 0
 goto L7
L6:
 ldc 1
L7:
 ifeq L9
 iload 3
 goto L8
L9:
 ldc 0
L8:
 ldc 1
 if_icmpeq L4
 goto L5
L4:
 iload 0
 iload 0
 iload 1
 idiv 
 iload 1
 imul 
 isub 
 istore 2
 iload 2
 invokestatic Primo/printInt(I)V
 iload 2
 ldc 0
 if_icmpne L14
 ldc 0
 goto L15
L14:
 ldc 1
L15:
 istore 3
 iload 1
 ldc 1
 iadd 
 istore 1
 goto L3
L5:
 iload 3
 invokestatic Primo/printBool(I)V
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Primo/run()V
 return
.end method

