package com.oogbox.support.orm.utils;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class JUtils {

    public static String findPackage(ProcessingEnvironment proEnv, TypeElement element) {
        Elements elements = proEnv.getElementUtils();
        PackageElement pkg = elements.getPackageOf(element);
//        if (pkg.isUnnamed()) {
//            throw new UnnamedPackageException(element);
//        }
        return pkg.getQualifiedName().toString();
    }


    public static boolean generateSourceFile(ProcessingEnvironment env, String packageName,
                                             TypeSpec spec) throws IOException {
        JavaFile javaFile = JavaFile.builder(packageName, spec).build();
        javaFile.writeTo(env.getFiler());
        return true;
    }

}