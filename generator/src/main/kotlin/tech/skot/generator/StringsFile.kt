package tech.skot.generator

import com.squareup.kotlinpoet.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

fun buildStringsFile(moduleName: String, withPhrase: Boolean = false) {
    val module = Paths.get("../$moduleName")
    val values = module.resolve("src/androidMain/res/values")
    val modulePackageName = getPackageName(Paths.get("../$moduleName/src/androidMain"))


    val strings =
            Files.list(values).filter { it.fileName.toString().startsWith("strings") }.flatMap {
                it.getDocumentElement().childElements().stream().filter { it.tagName == "string" }
                        .map { it.getAttribute("name") }
            }.collect(Collectors.toList())


    //Construction du fichier
    val contextClassName = ClassName("android.content", "Context")
    val phraseClassName = ClassName("com.phrase.android.sdk", "Phrase")


    val fileAndroid = FileSpec.builder("$appPackageName.model", "Strings").apply {
        if (withPhrase) {
            addImport(phraseClassName.packageName, phraseClassName.simpleName)
        }
    }
    val classBuilderAndroid = TypeSpec.classBuilder("StringsGen")
            .addSuperinterface(ClassName("$appPackageName.model", "Strings"))
            .addPrimaryConstructorWithParams(listOf(ParamInfos("applicationContext", contextClassName, listOf(KModifier.PRIVATE))))
            .apply {
                if (withPhrase) {
                    addProperty(
                            PropertySpec.builder("phraseWrappedContext", contextClassName)
                                    .addModifiers(KModifier.PRIVATE)
                                    .mutable(true)
                                    .initializer(
                                    "Phrase.wrap(applicationContext)"
                            ).build()
                    )
                } else {
                    addProperty(
                            PropertySpec.builder(
                                    "applicationContext",
                                    contextClassName
                            ).initializer("applicationContext").build()
                    )
                }
            }

            .addFunction(
                    FunSpec.builder("reinit")
                            .addModifiers(KModifier.OVERRIDE)
                            .addCode(CodeBlock.of("phraseWrappedContext = Phrase.wrap(applicationContext)"))
                            .build()
            )
            .addFunction(
                    FunSpec.builder("get")
                            .addParameter("strInt", Int::class)
                            .returns(String::class)
                            .addCode(CodeBlock.of("return ${if (withPhrase) "phraseWrappedContext" else "applicationContext"}.getString(strInt)"))
                            .build()
            )
            .addProperties(
                    strings.map {
                        PropertySpec.builder(it.decapitalize(), String::class, KModifier.OVERRIDE)
                                .getter(FunSpec.getterBuilder().addStatement("return get(R.string.$it)").build())
//                                .initializer(CodeBlock.of("get(R.string.$it)"))
                                .build()
                    }
            )


    fileAndroid.addImport(modulePackageName, "R")
    fileAndroid.addType(classBuilderAndroid.build())

    fileAndroid.build().writeTo(module.resolve("generated/androidMain/kotlin").toFile())


    val fileCommon = FileSpec.builder("$appPackageName.model", "Strings")
    val classBuilderCommon = TypeSpec.interfaceBuilder("Strings")
            .addProperties(
                    strings.map {
                        PropertySpec.builder(it.decapitalize(), String::class)
                                .build()
                    }
            )
            .addFunction(
                    FunSpec.builder("reinit")
                            .addModifiers(KModifier.ABSTRACT)
                            .build()
            )


    fileCommon.addType(classBuilderCommon.build())
    fileCommon.build().writeTo(module.resolve("generated/commonMain/kotlin").toFile())

}
