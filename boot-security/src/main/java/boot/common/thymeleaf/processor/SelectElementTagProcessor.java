package boot.common.thymeleaf.processor;

import boot.common.thymeleaf.SysDictFunc;
import boot.system.model.SysDict;
import net.dreamlu.mica.core.utils.StringUtil;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉框
 */
public class SelectElementTagProcessor extends AbstractElementTagProcessor {
    private static final String TAG_NAME = "select";
    private final SysDictFunc dictFunc;

    public SelectElementTagProcessor(final String prefix, int precedence, SysDictFunc dictFunc) {
        super(TemplateMode.HTML, prefix, TAG_NAME, true, null, false, precedence);
        this.dictFunc = dictFunc;
    }

    @Override
    protected void doProcess(final ITemplateContext context, final IProcessableElementTag tag, final IElementTagStructureHandler structureHandler) {
        // select html tag
        String id = tag.getAttributeValue("id");
        String name = tag.getAttributeValue("name");
        String classValue = tag.getAttributeValue("class");
        String style = tag.getAttributeValue("style");
        String dataOptions = tag.getAttributeValue("data-options");

        // 字典类型
        String dictType = tag.getAttributeValue("type");
        String defaultValue = tag.getAttributeValue("default");

        List<String> options = new ArrayList<>();
        if (StringUtil.isNotBlank(defaultValue)) {
            options.add("<option value=\"\">" + defaultValue + "</option>");
        }
        if (StringUtil.isNotBlank(dictType)) {
            List<SysDict> dictList = dictFunc.getByType(dictType);
            dictList.forEach(sysDict -> {
                String dictKey = sysDict.getDictKey();
                StringBuilder option = new StringBuilder();
                option.append("<option value=\"");
                option.append(dictKey);
                option.append("\">");
                option.append(sysDict.getDictValue());
                option.append("</option>");
                options.add(option.toString());
            });
        } else {
            List<String> typeList = dictFunc.getAllType();
            typeList.forEach(type -> {
                StringBuilder option = new StringBuilder();
                option.append("<option value=\"");
                option.append(type);
                option.append("\")>");
                option.append(type);
                option.append("</option>");
                options.add(option.toString());
            });
        }

        final IModelFactory modelFactory = context.getModelFactory();

        final IModel model = modelFactory.createModel();
        model.add(modelFactory.createText("\n\t"));

        IProcessableElementTag selectElement = modelFactory.createOpenElementTag("select");
        if (StringUtil.isNotBlank(id)) {
            selectElement = modelFactory.setAttribute(selectElement, "id", id);
        }
        if (StringUtil.isNotBlank(name)) {
            selectElement = modelFactory.setAttribute(selectElement, "name", name);
        }
        if (StringUtil.isNotBlank(style)) {
            selectElement = modelFactory.setAttribute(selectElement, "style", style);
        }
        if (StringUtil.isNotBlank(classValue)) {
            selectElement = modelFactory.setAttribute(selectElement, "class", classValue);
        }
        if (StringUtil.isNotBlank(dataOptions)) {
            selectElement = modelFactory.setAttribute(selectElement, "data-options", dataOptions);
        }
        model.add(selectElement);
        model.add(modelFactory.createText("\n\t\t"));
        model.add(modelFactory.createText(HtmlEscape.unescapeHtml(String.join("\n\t\t", options))));
        model.add(modelFactory.createText("\n\t"));
        model.add(modelFactory.createCloseElementTag("select"));

        structureHandler.replaceWith(model, false);
    }

}