package cn.cym.codetoolkit.utils;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.EditorTextField;

public class UIComponentUtils {

    public static EditorTextField createIdeaEditorTextField(Project project, String text) {
        return new EditorTextField(text, project, HtmlFileType.INSTANCE) {
            @Override
            protected EditorEx createEditor() {
                EditorEx editor1 = super.createEditor();
                editor1.setVerticalScrollbarVisible(true);
                editor1.setHorizontalScrollbarVisible(true);
                editor1.setOneLineMode(false);// 不是单行模式
                editor1.setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, new LightVirtualFile("")));
                EditorSettings editorSettings = editor1.getSettings();
                // 关闭虚拟空间
                editorSettings.setVirtualSpace(false);
                // 关闭标记位置（断点位置）
                editorSettings.setLineMarkerAreaShown(false);
                // 关闭缩减指南
                editorSettings.setIndentGuidesShown(false);
                // 显示行号
                editorSettings.setLineNumbersShown(true);
                // 支持代码折叠
                editorSettings.setFoldingOutlineShown(true);
                // 附加行，附加列（提高视野）
                editorSettings.setAdditionalColumnsCount(3);
                editorSettings.setAdditionalLinesCount(3);
                // 不显示换行符号
                // editorSettings.setCaretRowShown(false);
                return editor1;
            }
        };
    }
}
