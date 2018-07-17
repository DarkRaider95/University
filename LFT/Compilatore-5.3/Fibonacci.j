.class public Fibonacci
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
 ldc 10
 istore 1
 iload 1
 ldc 0
 if_icmpeq L1
 ldc 0
 goto L2
L1:
 ldc 1
L2:
 ldc 1
 if_icmpeq L3
 goto L4
L3:
 ldc 0
 istore 4
 goto L5
L4:
 ldc 1
 istore 0
 ldc 0
 istore 2
 ldc 1
 istore 3
L10:
 iload 0
 iload 1
 ldc 1
 isub 
 if_icmple L12
 ldc 0
 goto L13
L12:
 ldc 1
L13:
 ldc 0
 if_icmpeq L11
 iload 3
 iload 2
 iadd 
 istore 3
 iload 3
 iload 2
 isub 
 istore 2
 iload 0
 ldc 1
 iadd 
 istore 0
 goto L10
L11:
 iload 3
 istore 4
L5:
 iload 4
 invokestatic Fibonacci/printInt(I)V
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Fibonacci/run()V
 return
.end method

