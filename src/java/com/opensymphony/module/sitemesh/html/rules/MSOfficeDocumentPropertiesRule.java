package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BlockExtractingRule;
import com.opensymphony.module.sitemesh.html.Tag;

/**
 * Extracts the extra properties saved in HTML from MS Office applications (Word and Excel),
 * such as Author, Company, Version, etc.
 *
 * @author Joe Walnes
 */
public class MSOfficeDocumentPropertiesRule extends BlockExtractingRule {

    private final PageBuilder page;
    private boolean inDocumentProperties;

    public MSOfficeDocumentPropertiesRule(PageBuilder page) {
        super(true);
        this.page = page;
    }

    public boolean shouldProcess(String name) {
        return (inDocumentProperties && name.startsWith("o:")) || name.equals("o:documentproperties");
    }

    public void process(Tag tag) {
        if (tag.getName().equals("o:DocumentProperties")) {
            inDocumentProperties = (tag.getType() == Tag.OPEN);
            tag.writeTo(currentBuffer());
        } else {
            super.process(tag);
        }
    }

    protected void start(Tag tag) {
    }

    protected void end(Tag tag) {
        String name = tag.getName().substring(2);
        page.addProperty("office.DocumentProperties." + name, currentBuffer().toString());
        context.mergeBuffer();
    }

}
