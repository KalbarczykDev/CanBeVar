package dev.kalbarczyk.canbevar;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
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
                if ("var".equals(typeElement.getText())) return;
                if (type.equalsToText("var")) return;

                if (!(initializer.getType() instanceof PsiClassType)) return;

                var initType = initializer.getType();

                if (initType.equals(type)) {
                    holder.registerProblem(
                            variable.getTypeElement(),
                            "Explicit type can be replaced with 'var'",
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

            if (!(element instanceof PsiLocalVariable typeElement)) return;

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
