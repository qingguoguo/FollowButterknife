package com.qingguoguo.butterknife.compiler;

import com.google.auto.service.AutoService;
import com.qingguoguo.butterknife.annotations.BindView;
import com.qingguoguo.butterknife.annotations.OnClick;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {

    private Filer mFile;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFile = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    /**
     * 用来指定支持的 SourceVersion
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 用来指定支持的 AnnotationTypes
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    /**
     * 参考 ButterKnife 的写法
     */
    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class);
        annotations.add(OnClick.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("------------------------------------");
        System.out.println("------------------------------------");
        System.out.println("------------------------------------");
        System.out.println("------------------------------------");

        // 解析注解
        HashMap<Element, List<Element>> elementMap = new HashMap<>(16);
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elementSet) {
            System.out.println("----" + element.getSimpleName() + "-----" + element.getEnclosingElement());
            /**
             *----mTextView1-----com.qingguoguo.followbutterknife.LoginActivity
             *----mTextView2-----com.qingguoguo.followbutterknife.LoginActivity
             *----mTextView1-----com.qingguoguo.followbutterknife.MainActivity
             *----mTextView2-----com.qingguoguo.followbutterknife.MainActivity
             */
            Element enclosingElement = element.getEnclosingElement();
            List<Element> elements = elementMap.get(enclosingElement);
            if (elements == null) {
                elements = new ArrayList<>();
                elementMap.put(enclosingElement, elements);
            }
            elements.add(element);
        }

        Set<? extends Element> elementSetClick = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        System.out.println("------------------elementSetClick------------------" + elementSetClick);
        for (Element element : elementSetClick) {
            System.out.println("----" + element.getSimpleName() + "-----" + element.getEnclosingElement());
            Element enclosingElement = element.getEnclosingElement();
            System.out.println("------------------enclosingElement------------------" + enclosingElement);
            List<Element> elements = elementMap.get(enclosingElement);
            if (elements == null) {
                elements = new ArrayList<>();
                elementMap.put(enclosingElement, elements);
            }
            elements.add(element);
        }

        System.out.println("----" + elementMap + "-----");
        // 生成代码
        Set<Map.Entry<Element, List<Element>>> entries = elementMap.entrySet();
        for (Map.Entry<Element, List<Element>> entry : entries) {
            System.out.println("entry----" + entry + "-----");
            Element element = entry.getKey();
            // 父类
            ClassName unBinderClassName = ClassName.get("com.qingguoguo.butterknife", "UnBinder");
            // 生成类文件
            // activity Class Name
            String activityClassNameStr = element.getSimpleName().toString();
            ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);
            TypeSpec.Builder activityBuilder = TypeSpec.classBuilder(activityClassNameStr + "_ViewBinding")
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC);

            //---------添加父接口-----------------//
            activityBuilder.addSuperinterface(unBinderClassName);

            //---------添加属性 private XXXActivity target ;-------//
            activityBuilder.addField(activityClassName, "mTarget", Modifier.PRIVATE);

            //---------添加构造方法  public xxx_ViewBinding(xxx target) -------------------//
            MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder();
            constructorMethodBuilder.addParameter(activityClassName, "target").addModifiers(Modifier.PUBLIC);
            constructorMethodBuilder.addStatement("this.mTarget = target");

            //-------------------添加实现接口UnBinder要重写的unbind方法----------//
            ClassName callSuperClassName = ClassName.get("android.support.annotation",
                    "CallSuper");
            MethodSpec.Builder unbindMethodBuilder = MethodSpec
                    .methodBuilder("unbind")
                    .addAnnotation(Override.class)
                    .addAnnotation(callSuperClassName).addModifiers(Modifier.PUBLIC);
            unbindMethodBuilder.addStatement("$T target = this.mTarget", activityClassName);
            unbindMethodBuilder.addStatement("if (target == null) throw new IllegalStateException(\"Bindings already cleared.\")");

            //-------------------解析类有注解的属性，生成代码----------------------//
            List<Element> elementList = entry.getValue();
            for (Element viewBindIdElement : elementList) {
                String filedName = viewBindIdElement.getSimpleName().toString();
                BindView annotation = viewBindIdElement.getAnnotation(BindView.class);
                ClassName utilsClassName = ClassName.get("com.qingguoguo.butterknife",
                        "Utils");
                if (annotation != null) {
                    int id = annotation.value();
                    constructorMethodBuilder.addStatement("this.mTarget.$L = $T.findViewById(target,$L)", filedName, utilsClassName, id);
                    unbindMethodBuilder.addStatement("this.mTarget.$L = null", filedName);
                } else {
                    int[] idArray = viewBindIdElement.getAnnotation(OnClick.class).value();
                    System.out.println("----" + idArray.length + "-----");
                    for (int item : idArray) {
                        constructorMethodBuilder.addStatement("$T.findViewById(target,$L).setOnClickListener(new $L.OnClickListener() {\n" +
                                "      @Override\n" +
                                "      public void onClick($L v) {\n" +
                                "           mTarget." +
                                viewBindIdElement.getSimpleName() +
                                "(v);\n" +
                                "      }\n" +
                                "})", utilsClassName, item, "android.view.View", "android.view.View");
                    }
                }
                System.out.println("-----***************************----" + filedName);
            }
            unbindMethodBuilder.addStatement("this.mTarget = null");
            activityBuilder.addMethod(unbindMethodBuilder.build());
            activityBuilder.addMethod(constructorMethodBuilder.build());

            //-----------------包名----------------------//
            String packageName = mElementUtils.getPackageOf(element.getEnclosingElement()).getQualifiedName().toString();

            //----------------生成Java文件----------------//
            try {
                JavaFile.builder(packageName,
                        activityBuilder.build()).addFileComment("APT自动生成").build().writeTo(mFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
