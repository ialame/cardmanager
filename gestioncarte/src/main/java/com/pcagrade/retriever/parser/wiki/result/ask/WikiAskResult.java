package com.pcagrade.retriever.parser.wiki.result.ask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pcagrade.retriever.parser.wiki.result.ask.printout.IWikiAskPrintout;
import com.pcagrade.retriever.parser.wiki.result.ask.printout.WikiAskPrintoutNumber;
import com.pcagrade.retriever.parser.wiki.result.ask.printout.WikiAskPrintoutPage;
import com.pcagrade.retriever.parser.wiki.result.ask.printout.WikiAskPrintoutText;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiAskResult(
        IWikiAskPrintout value,
        Map<String, List<IWikiAskPrintout>> printouts
) implements IWikiAskPrintout {

    public String getString(String key) {
        if (!(getPrintout(key) instanceof WikiAskPrintoutText text)) {
            return "";
        }
        return text.value();
    }

    public long getNumber(String key) {
        if (!(getPrintout(key) instanceof WikiAskPrintoutNumber number)) {
            return 0;
        }
        return number.value();
    }

    public WikiAskPrintoutPage getPage(String key) {
        if (!(getPrintout(key) instanceof WikiAskPrintoutPage page)) {
            return null;
        }
        return page;
    }

    public IWikiAskPrintout getPrintout(String key) {
        var printout = printouts.get(key);

        if (CollectionUtils.isEmpty(printout)) {
            return null;
        }
        return printout.isEmpty() ? null :printout.get(0);
    }
}
