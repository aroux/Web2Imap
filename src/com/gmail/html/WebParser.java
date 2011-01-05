package com.gmail.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

import com.gmail.utils.Pair;
import com.webmail.data.EmailDetails;
import com.webmail.data.EmailSummary;

public class WebParser {

	private Parser htmlParser;

	private NodeList list;

	public WebParser() {
		// Nothing to do
	}

	public void feedParser(StringBuffer htmlPage) {
		try {
			htmlParser = new Parser(htmlPage.toString());
			list = htmlParser.parse(null);
		}
		catch (ParserException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public Map<String, String> getLoginFormInputs() {
		Map<String, String> map = new HashMap<String, String>();

		NodeFilter formFilter = new HasAttributeFilter("id", "gaia_loginform");
		NodeList filteredNodes = list.extractAllNodesThatMatch(formFilter, true);
		if (filteredNodes.size() != 1) {
			System.err.println("Impossible to get form with id 'gaia_loginform' -> size : " + filteredNodes.size());
			System.exit(-1);
		}

		Node formNode = filteredNodes.elementAt(0);
		NodeFilter inputFilter = new TagNameFilter("input");
		NodeList inputNodes = formNode.getChildren().extractAllNodesThatMatch(inputFilter, true);

		try {
			NodeIterator iter = inputNodes.elements();
			while (iter.hasMoreNodes()) {
				Node node = iter.nextNode();
				TagNode tNode = (TagNode) node;
				map.put(tNode.getAttribute("name"), tNode.getAttribute("value"));
			}
		}
		catch (ParserException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return map;
	}

	public String getRedirectingUrl() {
		String url = null;
		NodeFilter metaFilter = new HasAttributeFilter("http-equiv", "refresh");
		NodeList filteredNodes = list.extractAllNodesThatMatch(metaFilter, true);
		if (filteredNodes.size() != 1) {
			System.err.println("Impossible to get meta tag with http-equiv 'refresh' -> size : " + filteredNodes.size());
			//System.out.println(list.toHtml());
			System.exit(-1);
		}
		String content = ((TagNode) filteredNodes.elementAt(0)).getAttribute("content");
		url = content.split("&#39;")[1].replace("&amp;", "&");
		//		try {
		//			url = URLDecoder.decode(content.split("&#39;")[1], "UTF-8");
		//		} catch (UnsupportedEncodingException e) {
		//			e.printStackTrace();
		//			System.exit(-1);
		//		}
		return url;
	}

	private String formatOnOneLine(String s) {
		return s.replace('\n', ' ').replaceAll("\\s+", " ");
	}

	@SuppressWarnings("unused")
	private void printChildren(Node n) {
		NodeIterator iter = n.getChildren().elements();
		try {
			while (iter.hasMoreNodes()) {
				System.out.println(iter.nextNode().getText());
			}
		}
		catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private TagNode getNextTagNode(NodeIterator iter) {
		try {
			while (iter.hasMoreNodes()) {
				Node node = iter.nextNode();
				if (node instanceof TagNode) {
					return (TagNode) node;
				}
			}
		}
		catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	private TextNode getNextTextNode(NodeIterator iter) {
		try {
			while (iter.hasMoreNodes()) {
				Node node = iter.nextNode();
				if (node instanceof TextNode) {
					return (TextNode) node;
				}
			}
		}
		catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*************************************************************************/
	/** BEGIN EMAIL SUMMARY */
	/*************************************************************************/

	private String summaryEmailGetFrom(NodeList tdNodes) {
		NodeList innerTdNodes = tdNodes.elementAt(1).getChildren();
		return formatOnOneLine(innerTdNodes.elementAt(0).getText());
	}

	private String summaryEmailGetDate(NodeList tdNodes) {
		NodeList innerTdNodes = tdNodes.elementAt(3).getChildren();
		String rawDate = innerTdNodes.elementAt(0).getText();
		return formatOnOneLine(Translate.decode(rawDate)).replace("�", " ");
	}

	private String summaryEmailGetLink(NodeList tdNodes) {
		NodeList innerTdNodes = tdNodes.elementAt(2).getChildren();
		NodeIterator iter = innerTdNodes.elements();
		TagNode linkNode = getNextTagNode(iter);
		/* Some mails have an image between from and subject */
		if (linkNode instanceof ImageTag) {
			linkNode = getNextTagNode(iter);
		}
		return linkNode.getAttribute("href");
	}

	private Pair<String, String> summaryEmailGetSubjectAndContent(NodeList tdNodes) {
		NodeList innerTdNodes = tdNodes.elementAt(2).getChildren();
		NodeIterator iter = innerTdNodes.elements();
		TagNode aNode = getNextTagNode(iter);
		/* Some mails have an image between from and subject */
		if (aNode instanceof ImageTag) {
			aNode = getNextTagNode(iter);
		}
		NodeList innerANodes = aNode.getChildren();
		iter = innerANodes.elements();

		TagNode spanNode = getNextTagNode(iter); /* span */
		iter = spanNode.getChildren().elements();

		getNextTagNode(iter); /* font */
		getNextTagNode(iter); /* font */
		getNextTagNode(iter); /* /font */
		getNextTagNode(iter); /* /font */

		TextNode textNodeSubject = getNextTextNode(iter);
		String subject = Translate.decode(formatOnOneLine(textNodeSubject.getText()));
		//formatOnOneLine(filteredNodes.elementAt(2).getChildren().elementAt(1).getChildren().elementAt(1).getChildren().elementAt(5).getText());

		getNextTagNode(iter); /* font */

		TextNode textNodeSummary = getNextTextNode(iter);
		String summary = Translate.decode(formatOnOneLine(textNodeSummary.getText()).substring(3));
		//String summary = filteredNodes.elementAt(2).getChildren().elementAt(1).getChildren().elementAt(1).getChildren().elementAt(7).getText();
		//summary = Translate.decode(formatOnOneLine(summary.substring(3)));

		return new Pair<String, String>(subject, summary);
	}

	private EmailSummary extractEmailSummary(TagNode trNode) {
		EmailSummary emailSummary = null;
		NodeFilter trFilter = new TagNameFilter("td");
		NodeList filteredNodes = trNode.getChildren().extractAllNodesThatMatch(trFilter, true);

		if (filteredNodes.size() > 1) {
			/* From */
			String from = summaryEmailGetFrom(filteredNodes);
			/* Date */
			String date = summaryEmailGetDate(filteredNodes);
			/* Link */
			String link = summaryEmailGetLink(filteredNodes);

			Pair<String, String> content = summaryEmailGetSubjectAndContent(filteredNodes);
			/* Subject */
			String subject = content.getFirst();
			/* Summary */
			String summary = content.getSecond();

			emailSummary = new EmailSummary(from, subject, date, link, summary);
			//System.out.println(emailSummary);
		}
		return emailSummary;
	}

	public List<EmailSummary> getEmailSummaryList() {
		List<EmailSummary> emailsSummary = new ArrayList<EmailSummary>();
		NodeFilter tableFilter = new HasAttributeFilter("class", "th");
		NodeList filteredNodes = list.extractAllNodesThatMatch(tableFilter, true);
		if (filteredNodes.size() != 1) {
			System.err.println("Impossible to get table containing emails : " + filteredNodes.size());
			System.exit(-1);
		}
		NodeFilter trFilter = new TagNameFilter("tr");
		filteredNodes = filteredNodes.extractAllNodesThatMatch(trFilter, true);

		try {
			NodeIterator iter = filteredNodes.elements();
			while (iter.hasMoreNodes()) {
				EmailSummary emailSummary;
				Node node = iter.nextNode();
				TagNode trNode = (TagNode) node;
				emailSummary = extractEmailSummary(trNode);
				if (emailSummary != null) {
					emailsSummary.add(emailSummary);
					//System.out.println("node name: " + node.getText());
				}
			}
		}
		catch (ParserException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return emailsSummary;
	}

	/*************************************************************************/
	/** END EMAIL SUMMARY */
	/*************************************************************************/

	/*************************************************************************/
	/** BEGIN EMAIL DETAILS */
	/*************************************************************************/

	@Deprecated
	public EmailDetails getEmailDetails() {
		EmailDetails email = null;

		/*
		 * Subject
		 */
		NodeFilter tableFilter = new HasAttributeFilter("class", "h");
		NodeList filteredNodes = list.extractAllNodesThatMatch(tableFilter, true);
		if (filteredNodes.size() != 1) {
			System.err.println("Impossible to get table containing subject : " + filteredNodes.size());
			System.exit(-1);
		}

		TagNode subjectTableNode = (TagNode) filteredNodes.elementAt(0);//.getChildren().elementAt(1);//.getChildren().elementAt(0);
		TagNode subjectTRNode = getNextTagNode(subjectTableNode.getChildren().elements());
		TagNode subjectTDNode = getNextTagNode(subjectTRNode.getChildren().elements());
		NodeFilter headFilter = new TagNameFilter("h2");
		NodeList headNodes = subjectTDNode.getChildren().extractAllNodesThatMatch(headFilter, true);
		if (headNodes.size() != 1) {
			System.err.println("Impossible to get head tag containing subject : " + headNodes.size());
			System.exit(-1);
		}

		//TextNode subjectTextNode = getNextTextNode(headNodes.elementAt(0).getChildren().elements());
		//String subject = Translate.decode(formatOnOneLine(subjectTextNode.getText()));

		return email;
	}

	/*************************************************************************/
	/** END EMAIL DETAILS */
	/*************************************************************************/

	public String getCurrentLocation() {
		return list.elementAt(0).getPage().getBaseUrl();
	}
}
