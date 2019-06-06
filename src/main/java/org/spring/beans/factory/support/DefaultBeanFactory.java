package org.spring.beans.factory.support;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spring.beans.BeanDefinition;
import org.spring.beans.PropertyValue;
import org.spring.beans.SimpleTypeConverter;
import org.spring.beans.factory.BeanCreationException;
import org.spring.beans.factory.BeanFactoryAware;
import org.spring.beans.factory.NoSuchBeanDefinitionException;
import org.spring.beans.factory.config.BeanPostProcessor;
import org.spring.beans.factory.config.DependencyDescriptor;
import org.spring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.spring.util.ClassUtils;

public class DefaultBeanFactory extends AbstractBeanFactory 
	implements BeanDefinitionRegistry{

	private List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();
	
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
	private ClassLoader beanClassLoader;
	
	public DefaultBeanFactory() {
		
	}
	public void addBeanPostProcessor(BeanPostProcessor postProcessor){
		this.beanPostProcessors.add(postProcessor);
	}
	public List<BeanPostProcessor> getBeanPostProcessors() {
		return this.beanPostProcessors;
	}
	public void registerBeanDefinition(String beanID,BeanDefinition bd){
		this.beanDefinitionMap.put(beanID, bd);
	}
	public BeanDefinition getBeanDefinition(String beanID) {
		return this.beanDefinitionMap.get(beanID);
	}
	
	public Object getBean(String beanID) {
		BeanDefinition bd = this.getBeanDefinition(beanID);
		if(bd == null){
			return null;
		}
		
		//TODO ? 会不会线程不安全
		if(bd.isSingleton()){
			Object bean = this.getSingleton(beanID);
			if(bean == null){
				bean = createBean(bd);
				this.registerSingleton(beanID, bean);
			}
			return bean;
		} 
		return createBean(bd);
	}
	
	protected Object createBean(BeanDefinition bd) {
		//创建实例
		Object bean = instantiateBean(bd);
		//设置属性
		populateBean(bd, bean);
		
		//bean的初始化，返回代理
		bean = initializeBean(bd,bean);
		
		return bean;		
	}
	
	private Object instantiateBean(BeanDefinition bd) {
		if(bd.hasConstructorArgumentValues()){
			ConstructorResolver resolver = new ConstructorResolver(this);
			return resolver.autowireConstructor(bd);
		}else{
			ClassLoader cl = this.getBeanClassLoader();
			String beanClassName = bd.getBeanClassName();
			try {
				Class<?> clz = cl.loadClass(beanClassName);
				return clz.newInstance();
			} catch (Exception e) {			
				throw new BeanCreationException("create bean for "+ beanClassName +" failed",e);
			}	
		}
	}
	
	protected void populateBean(BeanDefinition bd, Object bean){
		
		for(BeanPostProcessor processor : this.getBeanPostProcessors()){
			if(processor instanceof InstantiationAwareBeanPostProcessor){
				((InstantiationAwareBeanPostProcessor)processor).postProcessPropertyValues(bean, bd.getID());
			}
		}
		
		List<PropertyValue> pvs = bd.getPropertyValues();
		
		//如果没有propety  则说明不需要做任何setter注入
		if (pvs == null || pvs.isEmpty()) {
			return;
		}
		
		BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this);
		//类型转换
		SimpleTypeConverter converter = new SimpleTypeConverter(); 
		try{
			//java.beans.Introspector
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			
			for (PropertyValue pv : pvs){
				String propertyName = pv.getName();
				//originalValue可能是RuntimeBeanReference或TypedStringValue
				Object originalValue = pv.getValue();
				//将RuntimeBeanReference解析成bean  将TypedStringValue解析成值返回
				Object resolvedValue = valueResolver.resolveValueIfNecessary(originalValue);
				//假设现在originalValue表示的是ref=accountDao,已经通过resolve得到了accountDao对象
				//接下来怎么办?如何取调用PetService的setAccountDao方法?
				//我们只有propertyName(来自xml)
				//1.通过java.beans.Introspector我们可以拿到class有哪些字段和setter getter方法等等
				//2.获得该类的属性描述器PropertyDescriptor
				//3.通过propertyName和属性描述器(类中关于这个属性的所有信息 包括get set)
				//	找到我们要进行setter的那个属性，进行类型转换，然后通过属性描述器获得set方法，利用反射进行注入
				
				for (PropertyDescriptor pd : pds) {
					//判断<property name="xxx" ref="yyy"/>中的xxx是否是bean中的某个属性
					if(pd.getName().equals(propertyName)){
						//类型转换
						Object convertedValue = converter.convertIfNecessary(resolvedValue, pd.getPropertyType());
						//利用反射调用xxx属性的set方法
						pd.getWriteMethod().invoke(bean, convertedValue);
						break;
					}
				}
 
				
			}
		}catch(Exception ex){
			throw new BeanCreationException("Failed to obtain BeanInfo for class [" + bd.getBeanClassName() + "]", ex);
		}	
	}

	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

    public ClassLoader getBeanClassLoader() {
		return (this.beanClassLoader != null ? this.beanClassLoader : ClassUtils.getDefaultClassLoader());
	}
    
    public Object resolveDependency(DependencyDescriptor descriptor) {
		
		Class<?> typeToMatch = descriptor.getDependencyType();
		for(BeanDefinition bd: this.beanDefinitionMap.values()){		
			//确保BeanDefinition 有Class对象
			resolveBeanClass(bd);
			Class<?> beanClass = bd.getBeanClass();			
			if(typeToMatch.isAssignableFrom(beanClass)){
				return this.getBean(bd.getID());
			}
		}
		return null;
	}
    
    public void resolveBeanClass(BeanDefinition bd) {
		if(bd.hasBeanClass()){
			return;
		} else{
			try {
				bd.resolveBeanClass(this.getBeanClassLoader());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("can't load class:"+bd.getBeanClassName());
			}
		}
	}
    
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		BeanDefinition bd = this.getBeanDefinition(name);
		if(bd == null){
			throw new NoSuchBeanDefinitionException(name);
		}
		resolveBeanClass(bd);		
		return bd.getBeanClass();
	}
    
    public List<Object> getBeansByType(Class<?> type) {
        List<Object> result = new ArrayList<Object>();
        List<String> beanIDs = this.getBeanIDsByType(type);
        for(String beanID : beanIDs){
            result.add(this.getBean(beanID));
        }
        return result;
    }
    
    private List<String> getBeanIDsByType(Class<?> type) {
        List<String> result = new ArrayList<String>();
        for (String beanName : this.beanDefinitionMap.keySet()) {
            if (type.isAssignableFrom(this.getType(beanName))) {
                result.add(beanName);
            }
        }
        return result;
    }
    
    //bean的初始化
    protected Object initializeBean(BeanDefinition bd, Object bean) {
    	//初始化前调用BeanFactoryAware的setBeanFactory方法
        invokeAwareMethods(bean);
        
        //TODO,对Bean做初始化 调用bean的init方法，暂不实现
        if (!bd.isSynthetic()) {
            return applyBeanPostProcessorsAfterInitialization(bean, bd.getID());
        }
        return bean;
    }
    
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            result = beanPostProcessor.afterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    private void invokeAwareMethods(final Object bean) {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
    }
}
