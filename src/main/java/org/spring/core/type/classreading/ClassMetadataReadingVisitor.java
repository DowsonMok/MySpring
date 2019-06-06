package org.spring.core.type.classreading;

import org.spring.core.type.ClassMetadata;
import org.spring.util.ClassUtils;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.SpringAsmInfo;

/**
 * ASM的Reader调用Visitor的visit方法 告知被Reader读取的类的元信息 比如是否是抽象类 是否是接口 是否为final
 * @author Administrator
 *
 */
public class ClassMetadataReadingVisitor extends ClassVisitor implements ClassMetadata {

	private String className;

	private boolean isInterface;

	private boolean isAbstract;

	private boolean isFinal;

	private String superClassName;

	private String[] interfaces;

	public ClassMetadataReadingVisitor() {
		super(SpringAsmInfo.ASM_VERSION);
	}

	
	/**
	 * version:class的编译版本 ---> javac编译java文件的时候 在java的class文件当中写入版本号
	 * access: public ... default private Interface Abstract final
	 * name:类名称
	 * signature:类签名（非泛型为NUll）
	 * superName:类的父类
	 * interfaces:类实现的接口
	 */
	public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
		//将路径中的/换成.
		this.className = ClassUtils.convertResourcePathToClassName(name);
		this.isInterface = ((access & Opcodes.ACC_INTERFACE) != 0);
		this.isAbstract = ((access & Opcodes.ACC_ABSTRACT) != 0);
		this.isFinal = ((access & Opcodes.ACC_FINAL) != 0);
		if (supername != null) {
			//将路径中的/换成.
			this.superClassName = ClassUtils.convertResourcePathToClassName(supername);
		}
		this.interfaces = new String[interfaces.length];
		for (int i = 0; i < interfaces.length; i++) {
			this.interfaces[i] = ClassUtils.convertResourcePathToClassName(interfaces[i]);
		}
	}

	public String getClassName() {
		return this.className;
	}

	public boolean isInterface() {
		return this.isInterface;
	}

	public boolean isAbstract() {
		return this.isAbstract;
	}

	public boolean isConcrete() {
		return !(this.isInterface || this.isAbstract);
	}

	public boolean isFinal() {
		return this.isFinal;
	}

	public boolean hasSuperClass() {
		return (this.superClassName != null);
	}

	public String getSuperClassName() {
		return this.superClassName;
	}

	public String[] getInterfaceNames() {
		return this.interfaces;
	}


}
