package dev.kalbarczyk.canbevar;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

public final class VarCanBeUsedInspection extends AbstractBaseJavaLocalInspectionTool {


    @Override
    public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, final boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitLocalVariable(final @NotNull PsiLocalVariable variable) {
                var type = variable.getType();
                var typeElement = variable.getTypeElement();
                var initializer = variable.getInitializer();

                if (initializer == null) return;
                if ("var".equals(typeElement.getText()) || "val".equals(typeElement.getText())) return;
                if (type.equalsToText("var")) return;

                var initType = initializer.getType();
                if (initType == null) return;
                if (!(initType instanceof PsiClassType) && !(initType instanceof PsiArrayType)) return;

                if (initType.equals(type)) {
                    holder.registerProblem(
                            variable.getTypeElement(),
                            "Explicit type can be replaced with 'var'",
                            ProblemHighlightType.WEAK_WARNING,
                            new ReplaceWithVarQuickFix()
                    );
                }

            }

            @Override
            public void visitForeachStatement(@NotNull PsiForeachStatement statement) {
                var iteratedValue = statement.getIteratedValue();
                var parameter = statement.getIterationParameter();

                var typeElement = parameter.getTypeElement();
                if (typeElement == null) return;
                if ("var".equals(typeElement.getText()) || "val".equals(typeElement.getText())) return;

                var iteratedType = iteratedValue != null ? iteratedValue.getType() : null;
                if (iteratedType == null) return;

                var parameterType = parameter.getType();

                PsiType elementType = null;
                if (iteratedType instanceof PsiClassType classType) {
                    elementType = PsiUtil.extractIterableTypeParameter(classType, false);
                } else if (iteratedType instanceof PsiArrayType arrayType) {
                    elementType = arrayType.getComponentType();
                }

                if (elementType == null) return;

                if (elementType.equals(parameterType)) {
                    holder.registerProblem(
                            typeElement,
                            "Explicit type in 'for-each' can be replaced with 'var'",
                            ProblemHighlightType.WEAK_WARNING,
                            new ReplaceWithVarQuickFix()
                    );
                }
            }
        };
    }


    private final static class ReplaceWithVarQuickFix implements LocalQuickFix {
        @Override
        public void applyFix(final @NotNull Project project, final @NotNull ProblemDescriptor problemDescriptor) {
            var element = problemDescriptor.getPsiElement();

            if (!(element instanceof PsiTypeElement typeElement)) return;

            var factory = JavaPsiFacade.getElementFactory(project);
            var newType = factory.createTypeElementFromText("var", null);

            typeElement.replace(newType);
        }

        @Override
        public @NotNull String getFamilyName() {
            return "Replace with 'var'";
        }
    }
}
