package com.opensymphony.module.sitemesh.parser.html;

import junit.framework.Assert;

public class MockTokenHandler implements TokenHandler {

    private StringBuffer expected = new StringBuffer();
    private StringBuffer actual = new StringBuffer();

    public void expectText(String tag) {
        expected.append(tag);
    }

    public void expectTag(int type, String tag) {
        expectTag(type, tag, new String[0]);
    }

    public void expectTag(int type, String tag, String[] attributes) {
        expected.append("{{TAG : ").append(tag);
        for (int i = 0; i < attributes.length; i+=2) {
            expected.append(' ').append(attributes[i]).append("=\"").append(attributes[i + 1]).append('"');
        }
        expected.append(' ').append(typeAsString(type)).append("}}");
    }

    public boolean caresAboutTag(String name) {
        return true;
    }

    public void tag(Tag tag) {
        actual.append("{{TAG : ").append(tag.getName());
        for (int i = 0; i < tag.getAttributeCount(); i++) {
            actual.append(' ').append(tag.getAttributeName(i)).append("=\"").append(tag.getAttributeValue(i)).append('"');
        }
        actual.append(' ').append(typeAsString(tag.getType())).append("}}");
    }

    public void text(Text text) {
        actual.append(text.getText());
    }

    public void error(String message, int line, int column) {
        //Assert.fail("Encountered error: " + message);
    }

    public void verify() {
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    private String typeAsString(int type) {
        switch (type) {
            case Tag.OPEN: return "*open*";
            case Tag.CLOSE: return "*close*";
            case Tag.EMPTY: return "*empty*";
            default: return "*unknown*";
        }
    }

}
