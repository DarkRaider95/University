.class public bo
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
 ldc 0
 istore 0
L1:
 iload 0
 ldc 100
 if_icmplt L4
 ldc 0
 goto L5
L4:
 ldc 1
L5:
 ldc 1
 if_icmpeq L2
 goto L3
L2:
 iload 0
 ldc 1
 iadd 
 istore 0
 iload 0
 invokestatic bo/printInt(I)V
 iload 0
 ldc 50
 if_icmpgt L8
 ldc 0
 goto L9
L8:
 ldc 1
L9:
 ldc 1
 if_icmpeq L10
 goto L11
L10:
 ldc 1
 invokestatic bo/printBool(I)V
 goto L12
L11:
 ldc 0
 invokestatic bo/printBool(I)V
L12:
 goto L1
L3:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic bo/run()V
 return
.end method

