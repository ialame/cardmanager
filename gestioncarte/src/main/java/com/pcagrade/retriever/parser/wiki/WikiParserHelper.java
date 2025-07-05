package com.pcagrade.retriever.parser.wiki;

import com.pcagrade.retriever.PCAUtils;
import de.fau.cs.osr.ptk.common.ast.AstStringNode;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.sweble.wikitext.parser.nodes.WtEmptyImmutableNode;
import org.sweble.wikitext.parser.nodes.WtHeading;
import org.sweble.wikitext.parser.nodes.WtInternalLink;
import org.sweble.wikitext.parser.nodes.WtLinkTitle;
import org.sweble.wikitext.parser.nodes.WtName;
import org.sweble.wikitext.parser.nodes.WtNamedXmlElement;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtSection;
import org.sweble.wikitext.parser.nodes.WtTemplate;
import org.sweble.wikitext.parser.nodes.WtTemplateArgument;
import org.sweble.wikitext.parser.nodes.WtValue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class WikiParserHelper {

    private WikiParserHelper() {}

    public static String getAsString(@Nullable WtNode node) {
        if (node == null || node instanceof WtEmptyImmutableNode) {
            return "";
        } else if (node instanceof WtName name && name.isResolved()) {
            return StringUtils.trimToEmpty(name.getAsString());
        } else if (node instanceof AstStringNode<?> text) {
            return StringUtils.trimToEmpty(text.getContent());
        } else if (node instanceof WtNamedXmlElement xmlElement) {
            return StringUtils.trimToEmpty(xmlElement.getName());
        } else if (node.isEmpty()) {
            return "";
        } else if (node instanceof WtLinkTitle title) {
            return getAsString(title.isEmpty() ? null :title.get(0));
        } else if (node instanceof WtInternalLink link) {
            return link.hasTitle() ? getAsString(link.getTitle()) : getAsString(link.getTarget());
        } else if (node instanceof WtTemplate template) {
            return getAsString(template.getName());
        } else if (node instanceof WtTemplateArgument templateArgument) {
            return getAsString(templateArgument.getName());
        } else if (node instanceof WtValue value) {
            return getAsString(value.isEmpty() ? null :value.get(0));
        } else if (node instanceof WtSection section) {
            return getAsString(section.getHeading());
        } else if (node instanceof WtHeading heading) {
            return getAsString(heading.isEmpty() ? null :heading.get(0));
        }
        return "";
    }

    public static boolean isNodeEmpty(WtNode node) {
        return node == null || node instanceof WtEmptyImmutableNode || CollectionUtils.isEmpty(node);
    }

    public static WtValue getArgument(WtTemplate template, int index) {
        var args = template.getArgs();

        if (index < args.size()) {
            var arg = args.get(index);

            if (arg instanceof WtTemplateArgument templateArgument) {
                return templateArgument.getValue();
            }
        }
        return WtValue.NO_VALUE;
    }

    public static String parseFirstArgument(WtTemplate template) {
        return getAsString(getArgument(template, 0));
    }

    public static boolean templateIs(WtTemplate template, String name) {
        return StringUtils.equalsIgnoreCase(name, getAsString(template));
    }

    public static boolean templateIs(WtTemplate template, List<String> nameList) {
        var name = getAsString(template);

        return nameList.stream().anyMatch(s -> StringUtils.equalsIgnoreCase(s, name));
    }

    public static String getArgumentAsString(WtTemplate template, String name) {
        return getAsString(getArgument(template, name));
    }

    public static String getArgumentAsString(WtTemplate template, int index) {
        return getAsString(getArgument(template, index));
    }

    public static WtValue getArgument(WtTemplate template, String name) {
        var args = template.getArgs();

        for (WtNode arg : args) {
            if (arg instanceof WtTemplateArgument templateArgument && templateArgument.size() > 1 && name.equalsIgnoreCase(getAsString(templateArgument))) {
                return templateArgument.getValue();
            }
        }
        return WtValue.NO_VALUE;
    }

    public static List<WtTemplateArgument> getArguments(WtTemplate template) {
        return template.getArgs().stream()
                .filter(arg -> arg instanceof WtTemplateArgument templateArgument && templateArgument.size() > 1)
                .map(WtTemplateArgument.class::cast)
                .toList();
    }

    public static Pair<String, String> parseLink(WtNode node) {
        return parseLink(getAsString(node));
    }

    public static Pair<String, String> parseLink(String link) {
        var clean = PCAUtils.substringBetween(link, "[[", "]]");

        if (StringUtils.isBlank(clean)) {
            return Pair.of("", "");
        }

        var split = StringUtils.split(clean, "|");

        if (split.length == 0) {
            return Pair.of("", "");
        } else if (split.length == 1) {
            return Pair.of(split[0], split[0]);
        } else if (split.length == 2) {
            return Pair.of(split[0], split[1]);
        }
        return Pair.of(split[0], StringUtils.join(split, "|", 1, split.length));
    }

    public static Optional<WtTemplate> findTemplate(WtNode page, String name) {
        return findTemplate(page, name, t -> true);
    }

    public static Optional<WtTemplate> findTemplate(WtNode page, String name, Predicate<WtTemplate> predicate) {
        return findNode(page, n -> testTemplate(name, predicate, n))
                .map(WtTemplate.class::cast);
    }

    public static List<WtTemplate> findTemplates(WtNode page, String name) {
        return findTemplates(page, name, t -> true);
    }

    public static List<WtTemplate> findTemplates(WtNode page, String name, Predicate<WtTemplate> predicate) {
        return findAllNodes(page, n -> testTemplate(name, predicate, n)).stream()
                .map(WtTemplate.class::cast)
                .toList();
    }

    private static boolean testTemplate(String name, Predicate<WtTemplate> predicate, WtNode n) {
        return n instanceof WtTemplate template && WikiParserHelper.templateIs(template, name) && predicate.test(template);
    }

    public static Optional<WtNode> findNode(WtNode page, Predicate<WtNode> predicate) {
        if (WikiParserHelper.isNodeEmpty(page)) {
            return Optional.empty();
        }

        for (WtNode node : page) {
            if (predicate.test(node)) {
                return Optional.of(node);
            } else {
                var opt = findNode(node, predicate);

                if (opt.isPresent()) {
                    return opt;
                }
            }
        }
        return Optional.empty();
    }

    public static List<WtNode> findAllNodes(WtNode page, Predicate<WtNode> predicate) {
        if (WikiParserHelper.isNodeEmpty(page)) {
            return Collections.emptyList();
        }

        var list = new LinkedList<WtNode>();

        for (WtNode node : page) {
            if (predicate.test(node)) {
                list.add(node);
            }
            list.addAll(findAllNodes(node, predicate));
        }
        return List.copyOf(list);
    }
}
