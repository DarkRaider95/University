.class public input
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
 ldc 5
 istore 0
 ldc 20
 istore 1
 ldc 30
 istore 2
L3:
 iload 0
 iload 1
 if_icmplt L5
 ldc 0
 goto L6
L5:
 ldc 1
L6:
 ldc 0
 if_icmpeq L4
 iload 0
 invokestatic input/printInt(I)V
 iload 0
 ldc 1
 iadd 
 istore 0
 goto L3
L4:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic input/run()V
 return
.end method

