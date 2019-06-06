package org.spring.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.spring.beans.BeansException;
import org.spring.beans.factory.BeanCreationException;
import org.spring.beans.factory.config.AutowireCapableBeanFactory;
import org.spring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.spring.core.annotation.AnnotationUtils;
import org.spring.util.ReflectionUtils;


public class AutowiredAnnotationProcessor implements InstantiationAwareBeanPostProcessor  {
	
	private AutowireCapableBeanFactory beanFactory;
	private String requiredParameterName = "required";
	private boolean requiredParameterValue = true;
	
	private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
			new LinkedHashSet<Class<? extends Annotation>>();
	
	public AutowiredAnnotationProcessor(){
		this.autowiredAnnotationTypes.add(Autowired.class);
	}
	
	/*
	 * 根据目标类的每个field上是否存在Autowired注解，是否为required信息构建(field为静态则不构建)AutowiredFieldElement,
	 * 最后根据class和LinkedList<InjectionElement>创建InjectionMetadata
	 */
	public InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
		
		LinkedList<InjectionElement> elements = new LinkedList<InjectionElement>();
		Class<?> targetClass = clazz;

		do {
			LinkedList<InjectionElement> currElements = new LinkedList<InjectionElement>();
			
			//处理field
			for (Field field : targetClass.getDeclaredFields()) {
				//判断field上是否有Autowired注解
				Annotation ann = findAutowiredAnnotation(field);
				if (ann != null) {
					//field是否为静态的
					if (Modifier.isStatic(field.getModifiers())) {
						
						continue;
					}
					//判断是否为required
					boolean required = determineRequiredStatus(ann);
					currElements.add(new AutowiredFieldElement(field, required,beanFactory));
				}
			}
			
			//处理method
			for (Method method : targetClass.getDeclaredMethods()) {
				//TODO 处理方法注入
			}
			elements.addAll(0, currElements);
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);
		
		//构建InjectionMetadata
		return new InjectionMetadata(clazz, elements);
	}
	
	protected boolean determineRequiredStatus(Annotation ann) {
		try {
			Method method = ReflectionUtils.findMethod(ann.annotationType(), this.requiredParameterName);
			if (method == null) {
				// Annotations like @Inject and @Value don't have a method (attribute) named "required"
				// -> default to required status
				return true;
			}
			return (this.requiredParameterValue == (Boolean) ReflectionUtils.invokeMethod(method, ann));
		}
		catch (Exception ex) {
			// An exception was thrown during reflective invocation of the required attribute
			// -> default to required status
			return true;
		}
	}
	
	private Annotation findAutowiredAnnotation(AccessibleObject ao) {
		for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
			//ao属性上是否有type类型的注解
			Annotation ann = AnnotationUtils.getAnnotation(ao, type);
			if (ann != null) {
				return ann;
			}
		}
		return null;
	}
	public void setBeanFactory(AutowireCapableBeanFactory beanFactory){
		this.beanFactory = beanFactory;
	}
	public Object beforeInitialization(Object bean, String beanName) throws BeansException {
		//do nothing
		return bean;
	}
	public Object afterInitialization(Object bean, String beanName) throws BeansException {
		// do nothing
		return bean;
	}
	public Object beforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	public boolean afterInstantiation(Object bean, String beanName) throws BeansException {
		// do nothing
		return true;
	}

	public void postProcessPropertyValues(Object bean, String beanName) throws BeansException {		
		InjectionMetadata metadata = buildAutowiringMetadata(bean.getClass());
		try {
			metadata.inject(bean);
		}
		catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
		}		
	}
}
