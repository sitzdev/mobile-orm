package com.oogbox.support.orm.generate;

import com.oogbox.support.orm.utils.JUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

public class ModelRegistryGenerator {

    private ProcessingEnvironment processingEnvironment;
    private String packageName;
    private HashMap<String, String> modelMaps = new HashMap<>();
    private ClassName context = ClassName.get("android.content", "Context");
    private ClassName modelName = ClassName.get("java.lang", "String");

    public ModelRegistryGenerator(ProcessingEnvironment proEnv, String packageName, HashMap<String, String> modelMaps) {
        processingEnvironment = proEnv;
        this.packageName = packageName;
        this.modelMaps = modelMaps;
    }

    public boolean generate() {
        TypeSpec.Builder modelRegistry = TypeSpec.classBuilder("DataModels")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC);

        MethodSpec.Builder getMethod = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(context, "context")
                .addParameter(modelName, "modelName")
                .returns(TypeName.OBJECT)
                .addCode("try {\n");

        getMethod.addCode("$T modelClass = null;\n", String.class);

        // Creating switch for model class path
        getMethod.addCode("switch(modelName) {\n");
        for (String key : modelMaps.keySet()) {
            getMethod.addCode("case $S:\n", key);
            getMethod.addStatement("modelClass = $S", modelMaps.get(key));
            getMethod.addStatement("break");
        }
        getMethod.addCode("}");

        // Checking for modelClass and generating object for model
        getMethod.addCode("if (modelClass != null) {\n");
        getMethod.addCode("$T modelClassObj = $T.forName(modelClass);\n", Class.class, Class.class);
        getMethod.addCode("$T constructor = modelClassObj.getConstructor($T.class);\n", Constructor.class, context);
        getMethod.addCode("return constructor.newInstance(context);\n");
        getMethod.addCode("}\n");

        getMethod.addCode("} catch($T e) {\n", Exception.class)
                .addStatement("e.printStackTrace()")
                .addCode("}\n")
                .addStatement("return null");

        modelRegistry.addMethod(getMethod.build());

        try {
            return JUtils.generateSourceFile(processingEnvironment, packageName, modelRegistry.build());
        } catch (IOException e) {
            processingEnvironment.getMessager()
                    .printMessage(Diagnostic.Kind.NOTE,e.getMessage()+" ERROR WHILE GENERATING SOURCE FILE");
        }
        return false;
    }

}
