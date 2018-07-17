.class public Mcd
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
 ldc 123456
 istore 0
 ldc 30
 istore 1
L2:
 iload 0
 ldc 0
 if_icmpne L5
 ldc 0
 goto L6
L5:
 ldc 1
L6:
 ldc 1
 if_icmpeq L3
 goto L4
L3:
 iload 0
 iload 1
 if_icmplt L7
 ldc 0
 goto L8
L7:
 ldc 1
L8:
 ldc 1
 if_icmpeq L9
 goto L10
L9:
 iload 0
 istore 2
 iload 1
 istore 0
 iload 2
 istore 1
L10:
 iload 0
 iload 1
 isub 
 istore 0
 goto L2
L4:
 iload 1
 invokestatic Mcd/printInt(I)V
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Mcd/run()V
 return
.end method

