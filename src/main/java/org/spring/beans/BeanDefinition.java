package org.spring.beans;

import java.util.List;

//BeanDefinition用于保存对象的所有必要信息，包括对象的class类型，是否是抽象类、构造方法参数以及其它属性等
public interface BeanDefinition {
	public static final String SCOPE_SINGLETON = "singleton";
	public static final String SCOPE_PROTOTYPE = "prototype";
	public static final String SCOPE_DEFAULT = "";
	
	public boolean isSingleton();
	public boolean isPrototype();
	String getScope();
	void setScope(String scope);
	
	public String getBeanClassName();
	
	public List<PropertyValue> getPropertyValues();
	public ConstructorArgument getConstructorArgument();
	public String getID();
	public boolean hasConstructorArgumentValues();
	
	public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException;
	public Class<?> getBeanClass() throws IllegalStateException ;
	public boolean hasBeanClass();
	public boolean isSynthetic();
}
