package org.jacoco.core.diff;


import org.jacoco.core.data.MethodInfo;
import org.jacoco.core.tools.Util;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * <li>Package:com.ttp.gnirts_plugin</li>
 * <li>Author: Administrator  </li>
 * <li>Date: 2020/9/15</li>
 * <li>Description:   </li>
 */
public class DiffClassVisitor extends ClassVisitor {
    private String className;
    //CURRENT = 0X10;
    //BRANCH = 0X11;
    private int type;

    public DiffClassVisitor(int api, int type) {
        super(api);
        this.type = type;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    /**
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     * @return
     */

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      System.out.println("className:" + className + "  methodName:" + name + "  desc:" + desc + "  signature:" + signature);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        final MethodInfo methodInfo = new MethodInfo();
        methodInfo.className = className;
        methodInfo.methodName = name;
        methodInfo.desc = desc;
        methodInfo.signature = signature;
        methodInfo.exceptions = exceptions;
        mv = new MethodVisitor(Opcodes.ASM5, mv) {
            StringBuilder builder = new StringBuilder();


            @Override
            public void visitCode() {
//                System.out.println("visitCode");
                super.visitCode();
            }


            @Override
            public void visitParameter(String name, int access) {
                builder.append(name);
                builder.append(access);
//                System.out.println("visitParameter--name:" + name + "  access" + access);
                super.visitParameter(name, access);
            }


            //@TargetApi(21)
            //public static void Toast(Context context, String s) {
            //       ...
            //}

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                builder.append(desc);
                builder.append(visible);
//                System.out.println("visitAnnotation--desc:" + desc + "  visible:" + visible);
                return super.visitAnnotation(desc, visible);
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
                builder.append(typeRef);
                builder.append(typePath.toString());
                builder.append(desc);
                builder.append(visible);
//                System.out.println("visitTypeAnnotation--typeRef:" + typeRef + "  TypePath:" + typePath.toString() + "  desc:" + desc + "  visible" + visible);
                return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
            }


            //public static void Toast(@Nullable Context context, @Nullable String s) {
            //     ...
            // }
            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
                builder.append(parameter);
                builder.append(desc);
                builder.append(visible);
//                System.out.println("visitParameterAnnotation--parameter:" + parameter + "  desc:" + desc + "  visible" + visible);
                return super.visitParameterAnnotation(parameter, desc, visible);
            }


            @Override
            public void visitAttribute(Attribute attr) {
//                System.out.println("visitAttribute--attr:" + attr.toString());
                super.visitAttribute(attr);
            }


            @Override
            public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
                builder.append(type);
                builder.append(nLocal);
                builder.append(nStack);
//                System.out.println("visitFrame--type:" + type + "  nLocal:" + nLocal + "  local:" + local.toString() + "  nStack:" + nStack + "  stack:" + stack);
                super.visitFrame(type, nLocal, local, nStack, stack);
            }


            @Override
            public void visitInsn(int opcode) {
                builder.append(opcode);
//                System.out.println("visitInsn--opcode:" + opcode);
                super.visitInsn(opcode);
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                builder.append(opcode);
                builder.append(operand);
//                System.out.println("visitIntInsn--opcode:" + opcode + "  operand:" + operand);
                super.visitIntInsn(opcode, operand);
            }


            @Override
            public void visitVarInsn(int opcode, int var) {
                builder.append(opcode);
                builder.append(var);
//                System.out.println("visitVarInsn--opcode:" + opcode + "  var:" + var);
                super.visitVarInsn(opcode, var);
            }


            @Override
            public void visitTypeInsn(int opcode, String type) {
                builder.append(opcode);
                builder.append(type);
//                System.out.println("visitTypeInsn--opcode:" + opcode + "  type:" + type);
                super.visitTypeInsn(opcode, type);
            }


            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                builder.append(opcode);
                builder.append(owner);
                builder.append(name);
                builder.append(desc);
//                System.out.println("visitFieldInsn--opcode:" + opcode + "  owner:" + owner + "  name:" + name + "  desc:" + desc);
                super.visitFieldInsn(opcode, owner, name, desc);
            }


            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                builder.append(opcode);
                builder.append(owner);
                builder.append(name);
                builder.append(desc);
                builder.append(itf);
//                System.out.println("visitMethodInsn--opcode:" + opcode + "  owner:" + owner + "  name:" + name + "  desc:" + desc + "  itf:" + itf);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }


            @Override
            public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
                builder.append(name);
                builder.append(desc);
                builder.append(bsm.toString());
//                System.out.println("visitInvokeDynamicInsn--name:" + name + "  desc:" + desc + "  bsm:" + bsm.toString() + "  bsmArgs:" + bsmArgs.toString());
                super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
            }


            @Override
            public void visitJumpInsn(int opcode, Label label) {
                builder.append(opcode);
//                System.out.println("visitJumpInsn--opcode:" + opcode + "  label:" + label.toString());
                super.visitJumpInsn(opcode, label);
            }


            @Override
            public void visitLabel(Label label) {
                super.visitLabel(label);
            }


            @Override
            public void visitLdcInsn(Object cst) {
//                System.out.println("visitLdcInsn--cst:" + cst.toString() + " " + cst.getClass());

                if (!(cst instanceof Integer) || !isResourceId((Integer)cst)) {
                    builder.append(cst.toString());
                }
                super.visitLdcInsn(cst);
            }


            @Override
            public void visitIincInsn(int var, int increment) {
                builder.append(var);
                builder.append(increment);
//                System.out.println("visitIincInsn--var:" + var + "  increment:" + increment);
                super.visitIincInsn(var, increment);
            }

            @Override
            public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
                builder.append(min);
                builder.append(max);
//                System.out.println("visitTableSwitchInsn--min:" + min + "  max:" + max + "  dflt:" + dflt.toString() + "  labels:" + labels.toString());
                super.visitTableSwitchInsn(min, max, dflt, labels);
            }


            @Override
            public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
                if (keys != null && keys.length > 0) {
                    for (int key : keys) {
                        if(!isResourceId(key)){
                            builder.append(key);
                        }
                    }
                }
//                System.out.println("visitLookupSwitchInsn--dflt:" + dflt.toString() + "  keys:" + keys.toString() + "  labels:" + labels.toString());
                super.visitLookupSwitchInsn(dflt, keys, labels);
            }

            @Override
            public void visitMultiANewArrayInsn(String desc, int dims) {
                builder.append(desc);
                builder.append(dims);
//                System.out.println("visitMultiANewArrayInsn--desc:" + desc + "  dims:" + dims);
                super.visitMultiANewArrayInsn(desc, dims);
            }

            @Override
            public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
                builder.append(typeRef);
                builder.append(typePath.toString());
                builder.append(desc);
                builder.append(visible);
//                System.out.println("visitInsnAnnotation--typeRef:" + typeRef + "  TypePath:" + typePath.toString() + "  desc:" + desc + "  visible:" + visible);
                return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
            }

            @Override
            public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                builder.append(type);
//                System.out.println("visitTryCatchBlock--start:" + start.toString() + "  end:" + end.toString() + "  handler:" + handler.toString() + "  type:" + type);
                super.visitTryCatchBlock(start, end, handler, type);
            }

            @Override
            public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
                builder.append(typeRef);
                builder.append(typePath.toString());
                builder.append(desc);
                builder.append(visible);
//                System.out.println("visitTryCatchAnnotation--typeRef:" + typeRef + "  typePath:" + typePath.toString() + "  desc:" + desc + "  visible:" + visible);
                return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
            }


            @Override
            public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                builder.append(name);
                builder.append(desc);
                builder.append(signature);
                builder.append(index);
//                System.out.println("visitLocalVariable--name:" + name + "  desc:" + desc + "  signature:" + signature + "  start:" + start.toString() + "  end:" + end.toString() + "  index:" + index);
                super.visitLocalVariable(name, desc, signature, start, end, index);
            }

            @Override
            public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
                builder.append(typeRef);
                builder.append(typePath.toString());
                if (index != null && index.length > 0) {
                    for (int i : index) {
                        builder.append(i);
                    }
                }
                builder.append(desc);
                builder.append(visible);
//                System.out.println("visitLocalVariableAnnotation--typeRef:" + typeRef + "  TypePath:" + typePath.toString() + "  desc:" + desc + "  visible" + visible);
                return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
            }


            @Override
            public void visitLineNumber(int line, Label start) {
                super.visitLineNumber(line, start);
            }


            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
                builder.append(maxStack);
                builder.append(maxLocals);
//                System.out.println("visitMaxs--maxStack:" + maxStack + "  maxLocals:" + maxLocals);
                super.visitMaxs(maxStack, maxLocals);
            }


            @Override
            public void visitEnd() {
                String md5 = "";
                try {
                    md5 = Util.MD5(builder.toString());
                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                methodInfo.md5 = md5;
                DiffAnalyzer.getInstance().addMethodInfo(methodInfo, type);
//                System.out.println("visitEnd>>>md5:" + md5);
                super.visitEnd();
            }
        };
        return mv;
    }

    private boolean isResourceId(Integer id) {
        String hex=String.format("0x%8s",Integer.toHexString(id)).replace(' ','0');
        return hex.startsWith("0x7f");
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
