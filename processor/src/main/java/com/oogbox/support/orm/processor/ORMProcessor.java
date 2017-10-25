package com.oogbox.support.orm.processor;

import com.oogbox.support.orm.annotation.DataModel;
import com.oogbox.support.orm.annotation.ORMBaseModel;
import com.oogbox.support.orm.generate.ModelRegistryGenerator;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

@SupportedAnnotationTypes({})
public class ORMProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnvironment;
    private Messager messager;
    private HashMap<String, String> modelsMap = new HashMap<>();
    private String appPackageName = "com.oogbox.support.orm";
    private String superModelName = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.processingEnvironment = processingEnvironment;
        this.messager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(DataModel.class.getCanonicalName());
        annotations.add(ORMBaseModel.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            logLine("******************************");
            logLine("*   Processing Annotations   *");
            logLine("******************************\n");
            for (TypeElement annotation : annotations) {
                logLine("Finding for annotation -> " + annotation.getSimpleName() + "\n");
                for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                    log("-> " + element.getSimpleName() + "...");
                    if (processDataModel(annotation, (TypeElement) element)) {
                        log("... [OK]");
                    } else {
                        log("...... [FAIL]\n");
                    }
                    log("\n");
                }

                logLine("\n\nGenerating Mapped model registry.");
                if (new ModelRegistryGenerator(processingEnv, appPackageName, modelsMap).generate()) {
                    logLine("->> Success ! (Model Registry generated)");
                }

            }
            logLine("\n\n");
        }
        return true;
    }


    private boolean processDataModel(TypeElement annotation, TypeElement element) {
        if (annotation.getQualifiedName().toString().equals(ORMBaseModel.class.getCanonicalName())) {
            superModelName = element.getQualifiedName().toString();
            return true;
        }
        if (annotation.getQualifiedName().toString().equals(DataModel.class.getCanonicalName())) {
            DataModel dataModel = element.getAnnotation(DataModel.class);
            log(dataModel.value());
            TypeElement superClass = (TypeElement) ((DeclaredType) element.getSuperclass()).asElement();
            String superModelName = this.superModelName != null ? this.superModelName : "BaseModel";
            if (!superClass.getQualifiedName().toString().contains(superModelName)) {
                log(" [X] ERROR: Invalid Super class. It must be '" + superModelName + "'");
                return false;
            }
            modelsMap.put(dataModel.value(), element.getQualifiedName().toString());
            return true;
        }
        return false;
    }

    private static void log(String message) {
        System.out.print(message);
    }

    private static void logLine(String message) {
        System.out.println(message);
    }
}
