package org.spring.aop.config;

import java.util.ArrayList;
import java.util.List;

import org.spring.aop.aspectj.AspectJAfterReturningAdvice;
import org.spring.aop.aspectj.AspectJAfterThrowingAdvice;
import org.spring.aop.aspectj.AspectJBeforeAdvice;
import org.spring.aop.aspectj.AspectJExpressionPointcut;
import org.spring.beans.BeanDefinition;
import org.spring.beans.ConstructorArgument;
import org.spring.beans.PropertyValue;
import org.spring.beans.factory.config.RuntimeBeanReference;
import org.spring.beans.factory.support.BeanDefinitionReaderUtils;
import org.spring.beans.factory.support.BeanDefinitionRegistry;
import org.spring.beans.factory.support.GenericBeanDefinition;
import org.spring.util.StringUtils;
import org.dom4j.Element;


public class ConfigBeanDefinitionParser {
    private static final String ASPECT = "aspect";
    private static final String EXPRESSION = "expression";
    private static final String ID = "id";

    private static final String POINTCUT = "pointcut";
    private static final String POINTCUT_REF = "pointcut-ref";
    private static final String REF = "ref";
    private static final String BEFORE = "before";
    private static final String AFTER = "after";
    private static final String AFTER_RETURNING_ELEMENT = "after-returning";
    private static final String AFTER_THROWING_ELEMENT = "after-throwing";
    private static final String AROUND = "around";
    private static final String ASPECT_NAME_PROPERTY = "aspectName";


    public void parse(Element ele, BeanDefinitionRegistry registry) {
        List<Element> childElts = ele.elements();
        for (Element childElt : childElts) {
            String localName = childElt.getName();
            if (ASPECT.equals(localName)) {
                parseAspect(childElt, registry);
            }
        }
    }

//	<aop:aspect ref="tx">	
//		<aop:pointcut id="placeOrder" expression="execution(* org.litespring.service.v5.*.placeOrder(..))" />
//		<aop:before pointcut-ref="placeOrder" method="start" />
//		<aop:after-returning pointcut-ref="placeOrder"	method="commit" />	
//		<aop:after-throwing pointcut-ref="placeOrder" method = "rollback"/>
//	</aop:aspect>
    private void parseAspect(Element aspectElement, BeanDefinitionRegistry registry) {
        //TODO  aspectId没用到
        String aspectId = aspectElement.attributeValue(ID);
        String aspectName = aspectElement.attributeValue(REF);

        List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();
        List<RuntimeBeanReference> beanReferences = new ArrayList<RuntimeBeanReference>();

        List<Element> eleList = aspectElement.elements();
        boolean adviceFoundAlready = false;

        //解析advice元素
        for (int i = 0; i < eleList.size(); i++) {
            Element ele = eleList.get(i);
            //判断当前element是不是AdviceNode
            if (isAdviceNode(ele)) {
                if (!adviceFoundAlready) {
                    adviceFoundAlready = true;
                    if (!StringUtils.hasText(aspectName)) {
                        return;
                    }
                    beanReferences.add(new RuntimeBeanReference(aspectName));
                }
                GenericBeanDefinition advisorDefinition = parseAdvice(
                        aspectName, i, aspectElement, ele, registry, beanDefinitions, beanReferences);
                beanDefinitions.add(advisorDefinition);
            }
        }
        
        //解析pointcut元素
        List<Element> pointcuts = aspectElement.elements(POINTCUT);
        for (Element pointcutElement : pointcuts) {
            parsePointcut(pointcutElement, registry);
        }
    }

    private GenericBeanDefinition parsePointcut(Element pointcutElement, BeanDefinitionRegistry registry) {
        String id = pointcutElement.attributeValue(ID);
        String expression = pointcutElement.attributeValue(EXPRESSION);

        GenericBeanDefinition pointcutDefinition = null;

        pointcutDefinition = createPointcutDefinition(expression);
        String pointcutBeanName = id;
        if (StringUtils.hasText(pointcutBeanName)) {
            registry.registerBeanDefinition(pointcutBeanName, pointcutDefinition);
        } else {
            BeanDefinitionReaderUtils.registerWithGeneratedName(pointcutDefinition, registry);
        }
        return pointcutDefinition;
    }

    private GenericBeanDefinition createPointcutDefinition(String expression) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition(AspectJExpressionPointcut.class);
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanDefinition.setSynthetic(true);
        beanDefinition.getPropertyValues().add(new PropertyValue(EXPRESSION, expression));
        return beanDefinition;
    }

    private boolean isAdviceNode(Element element) {
        String name = element.getName();
        return (BEFORE.equals(name) || AFTER.equals(name) || AFTER_RETURNING_ELEMENT.equals(name) ||
                AFTER_THROWING_ELEMENT.equals(name) || AROUND.equals(name));
    }

    private GenericBeanDefinition parseAdvice(String aspectName, int order, Element aspectElement, Element adviceElement, BeanDefinitionRegistry registry,
                                              List<BeanDefinition> beanDefinitions, List<RuntimeBeanReference> beanReferences) {
    	//创建第一个参数
        GenericBeanDefinition methodDefinition = new GenericBeanDefinition(MethodLocatingFactory.class);
        methodDefinition.getPropertyValues().add(new PropertyValue("targetBeanName", aspectName));
        methodDefinition.getPropertyValues().add(new PropertyValue("methodName", adviceElement.attributeValue("method")));
        methodDefinition.setSynthetic(true);

        // 创建第三个参数
        GenericBeanDefinition aspectFactoryDef = new GenericBeanDefinition(AspectInstanceFactory.class);
        aspectFactoryDef.getPropertyValues().add(new PropertyValue("aspectBeanName", aspectName));
        aspectFactoryDef.setSynthetic(true);
        
        //创建该advice的BeanDefinition
        GenericBeanDefinition adviceDef = createAdviceDefinition(adviceElement, registry, aspectName, order, methodDefinition, aspectFactoryDef,
                beanDefinitions, beanReferences);
        adviceDef.setSynthetic(true);

        //使用生成的名称org.litespring.aop.aspectj.AspectJBeforeAdvice#0作为id注册到容器中
        BeanDefinitionReaderUtils.registerWithGeneratedName(adviceDef, registry);
        return adviceDef;
    }


    private GenericBeanDefinition createAdviceDefinition(
            Element adviceElement, BeanDefinitionRegistry registry, String aspectName, int order,
            GenericBeanDefinition methodDef, GenericBeanDefinition aspectFactoryDef,
            List<BeanDefinition> beanDefinitions, List<RuntimeBeanReference> beanReferences) {
    	
        GenericBeanDefinition adviceDefinition = new GenericBeanDefinition(getAdviceClass(adviceElement));
        adviceDefinition.getPropertyValues().add(new PropertyValue(ASPECT_NAME_PROPERTY, aspectName));

        ConstructorArgument cav = adviceDefinition.getConstructorArgument();
        //添加第一个参数
        cav.addArgumentValue(methodDef);

        //解析并添加第二个参数
        Object pointcut = parsePointcutProperty(adviceElement);
        if (pointcut instanceof BeanDefinition) {
            cav.addArgumentValue(pointcut);
            beanDefinitions.add((BeanDefinition) pointcut);
        } else if (pointcut instanceof String) {
            RuntimeBeanReference pointcutRef = new RuntimeBeanReference((String) pointcut);
            cav.addArgumentValue(pointcutRef);
            beanReferences.add(pointcutRef);
        }
        
        //添加第三个参数
        cav.addArgumentValue(aspectFactoryDef);
        return adviceDefinition;
    }

    private Object parsePointcutProperty(Element element) {
        if ((element.attribute(POINTCUT) == null) && (element.attribute(POINTCUT_REF) == null)) {
            return null;
        } else if (element.attribute(POINTCUT) != null) {
            String expression = element.attributeValue(POINTCUT);
            GenericBeanDefinition pointcutDefinition = createPointcutDefinition(expression);
            return pointcutDefinition;
        } else if (element.attribute(POINTCUT_REF) != null) {
            String pointcutRef = element.attributeValue(POINTCUT_REF);
            if (!StringUtils.hasText(pointcutRef)) {
                return null;
            }
            return pointcutRef;
        }
        return null;
    }

    private Class<?> getAdviceClass(Element adviceElement) {
        String elementName = adviceElement.getName();
        if (BEFORE.equals(elementName)) {
            return AspectJBeforeAdvice.class;
        } else if (AFTER_RETURNING_ELEMENT.equals(elementName)) {
            return AspectJAfterReturningAdvice.class;
        } else if (AFTER_THROWING_ELEMENT.equals(elementName)) {
            return AspectJAfterThrowingAdvice.class;
        } else {
            throw new IllegalArgumentException("Unknown advice kind [" + elementName + "].");
        }
    }

}